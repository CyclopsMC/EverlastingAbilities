package org.cyclops.everlastingabilities.ability;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.cyclopscore.helper.obfuscation.ObfuscationHelpers;
import org.cyclops.cyclopscore.network.packet.SendPlayerCapabilitiesPacket;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.item.ItemAbilityTotem;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Random;

/**
 * General ability helpers.
 * XP-related methods inspired by Ender IO's XpUtil and the Minecraft Wiki
 * @author rubensworks
 */
public class AbilityHelpers {

    public static final int[] RARITY_COLORS = new int[] {
            Helpers.RGBToInt(255, 255, 255),
            Helpers.RGBToInt(255, 255, 0),
            Helpers.RGBToInt(0, 255, 255),
            Helpers.RGBToInt(255, 0, 255),
    };

    public static int getExperienceForLevel(int level) {
        if (level == 0) {
            return 0;
        } else if (level < 16) {
            return (int) (Math.pow(level, 2) + 6 * level);
        } else if (level < 31) {
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        } else {
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
        }
    }

    public static int getLevelForExperience(int experience) {
        int i = 0;
        int newXp, lastXp = -1;
        while ((newXp = getExperienceForLevel(i)) <= experience) {
            if (newXp <= lastXp) break; // Avoid infinite loops when the MC level is too high, resulting in an xp overflow. See https://github.com/CyclopsMC/EverlastingAbilities/issues/27
            i++;
            lastXp = newXp;
        }
        return i - 1;
    }

    public static void sendPlayerUpdateCapabilities(EntityPlayerMP player) {
        EverlastingAbilities._instance.getPacketHandler().sendToPlayer(
                new SendPlayerCapabilitiesPacket(ObfuscationHelpers.getEntityCapabilities(player)), player);
    }

    public static void onPlayerAbilityChanged(EntityPlayer player, IAbilityType abilityType, int oldLevel, int newLevel) {
        abilityType.onChangedLevel(player, oldLevel, newLevel);
    }

    /**
     * Add the given ability.
     * @param player The player.
     * @param ability The ability.
     * @param doAdd If the addition should actually be done.
     * @param modifyXp Whether to require player to have enough XP before adding
     * @return The ability part that was added.
     */
    public static @Nullable Ability addPlayerAbility(EntityPlayer player, Ability ability, boolean doAdd, boolean modifyXp) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        int oldLevel = abilityStore.hasAbilityType(ability.getAbilityType())
                ? abilityStore.getAbility(ability.getAbilityType()).getLevel() : 0;

        // Check max ability count
        if (GeneralConfig.maxPlayerAbilities >= 0 && oldLevel == 0
                && GeneralConfig.maxPlayerAbilities <= abilityStore.getAbilities().size()) {
            return null;
        }

        Ability result = abilityStore.addAbility(ability, doAdd);
        int currentXp = player.experienceTotal;
        if (result != null && modifyXp && getExperience(result) > currentXp) {
            int maxLevels = player.experienceTotal / result.getAbilityType().getBaseXpPerLevel();
            if (maxLevels == 0) {
                result = null;
            } else {
                result = new Ability(result.getAbilityType(), maxLevels);
            }
        }
        if (doAdd && result != null) {
            player.experienceTotal -= getExperience(result);
            // Fix xp bar
            player.experienceLevel = getLevelForExperience(player.experienceTotal);
            int xpForLevel = getExperienceForLevel(player.experienceLevel);
            player.experience = (float)(player.experienceTotal - xpForLevel) / (float)player.xpBarCap();

            int newLevel = abilityStore.getAbility(result.getAbilityType()).getLevel();
            onPlayerAbilityChanged(player, result.getAbilityType(), oldLevel, newLevel);
        }
        if (player instanceof EntityPlayerMP) {
            sendPlayerUpdateCapabilities((EntityPlayerMP) player);
        }
        return result;
    }

    /**
     * Remove the given ability.
     * @param player The player.
     * @param ability The ability.
     * @param doRemove If the removal should actually be done.
     * @param modifyXp Whether to refund XP cost of ability
     * @return The ability part that was removed.
     */
    public static @Nullable Ability removePlayerAbility(EntityPlayer player, Ability ability, boolean doRemove, boolean modifyXp) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        int oldLevel = abilityStore.hasAbilityType(ability.getAbilityType())
                ? abilityStore.getAbility(ability.getAbilityType()).getLevel() : 0;
        Ability result = abilityStore.removeAbility(ability, doRemove);
        if (modifyXp && result != null) {
            player.addExperience(getExperience(result));
            int newLevel = abilityStore.hasAbilityType(result.getAbilityType())
                    ? abilityStore.getAbility(result.getAbilityType()).getLevel() : 0;
            onPlayerAbilityChanged(player, result.getAbilityType(), oldLevel, newLevel);
        }
        if (player instanceof EntityPlayerMP) {
            sendPlayerUpdateCapabilities((EntityPlayerMP) player);
        }
        return result;
    }

    public static int getExperience(Ability ability) {
        if (ability == null) {
            return 0;
        }
        return ability.getAbilityType().getBaseXpPerLevel() * ability.getLevel();
    }

    public static void setPlayerAbilities(EntityPlayerMP player, Map<IAbilityType, Integer> abilityTypes) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        abilityStore.setAbilities(abilityTypes);
    }

    public static boolean canInsert(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        Ability added = mutableAbilityStore.addAbility(ability, false);
        return added != null && added.getLevel() == ability.getLevel();
    }

    public static boolean canExtract(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        Ability added = mutableAbilityStore.removeAbility(ability, false);
        return added != null && added.getLevel() == ability.getLevel();
    }

    public static boolean canInsertToPlayer(Ability ability, EntityPlayer player) {
        Ability added = addPlayerAbility(player, ability, false, true);
        return added != null && added.getLevel() == ability.getLevel();
    }

    public static Ability insert(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        return mutableAbilityStore.addAbility(ability, true);
    }

    public static Ability extract(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        return mutableAbilityStore.removeAbility(ability, true);
    }

    public static Optional<IAbilityType> getRandomAbility(Random random, EnumRarity rarity) {
        List<IAbilityType> abilities = AbilityTypes.REGISTRY.getAbilityTypes(rarity);
        if (abilities.size() > 0) {
            return Optional.of(abilities.get(random.nextInt(abilities.size())));
        }
        return Optional.empty();
    }

    public static Optional<IAbilityType> getRandomAbilityUntil(Random random, EnumRarity rarity, boolean inclusive) {
        NavigableSet<EnumRarity> validRarities = AbilityHelpers.getValidAbilityRarities().headSet(rarity, inclusive);
        Iterator<EnumRarity> it = validRarities.descendingIterator();
        while (it.hasNext()) {
            Optional<IAbilityType> optional = getRandomAbility(random, it.next());
            if (optional.isPresent()) {
                return optional;
            }
        }
        return Optional.empty();
    }

    public static Optional<ItemStack> getRandomTotem(EnumRarity rarity, Random rand) {
        return getRandomAbility(rand, rarity).flatMap(
                abilityType -> Optional.of(ItemAbilityTotem.getInstance().getTotem(new Ability(abilityType, 1))));
    }
    

    public static EnumRarity getRandomRarity(Random rand) {
        int chance = rand.nextInt(50);
        EnumRarity rarity;
        if (chance >= 49) {
            rarity = EnumRarity.EPIC;
        } else if (chance >= 40) {
            rarity = EnumRarity.RARE;
        } else if (chance >= 25) {
            rarity = EnumRarity.UNCOMMON;
        } else {
            rarity = EnumRarity.COMMON;
        }

        // Fallback to a random selection of a rarity that is guaranteed to exist in the registered abilities
        if (hasRarityAbilities(rarity)) {
            int size = AbilityTypes.REGISTRY.getAbilityTypes().size();
            if (size == 0) {
                throw new IllegalStateException("No abilities were registered, at least one ability must be enabled for this mod to function correctly.");
            }
            rarity = Iterables.get(AbilityTypes.REGISTRY.getAbilityTypes(), rand.nextInt(size)).getRarity();
        }

        return rarity;
    }

    public static boolean hasRarityAbilities(EnumRarity rarity) {
        return AbilityTypes.REGISTRY.getAbilityTypes(rarity).isEmpty();
    }

    public static NavigableSet<EnumRarity> getValidAbilityRarities() {
        NavigableSet<EnumRarity> rarities = Sets.newTreeSet();
        for (EnumRarity rarity : EnumRarity.values()) {
            if (!AbilityTypes.REGISTRY.getAbilityTypes(rarity).isEmpty()) {
                rarities.add(rarity);
            }
        }
        return rarities;
    }

    public static Triple<Integer, Integer, Integer> getAverageRarityColor(IAbilityStore abilityStore) {
        int r = 0;
        int g = 0;
        int b = 0;
        int count = 1;
        for (IAbilityType abilityType : abilityStore.getAbilityTypes()) {
            Triple<Float, Float, Float> color = Helpers.intToRGB(AbilityHelpers.RARITY_COLORS
                    [Math.min(AbilityHelpers.RARITY_COLORS.length - 1, abilityType.getRarity().ordinal())]);
            r += color.getLeft() * 255;
            g += color.getMiddle() * 255;
            b += color.getRight() * 255;
            count++;
        }
        return Triple.of(r / count, g / count, b / count);
    }

    public static EnumRarity getSafeRarity(int rarity) {
        return rarity < 0 ? EnumRarity.COMMON : (rarity >= EnumRarity.values().length ? EnumRarity.EPIC : EnumRarity.values()[rarity]);
    }

}
