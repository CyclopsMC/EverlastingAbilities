package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.core.helper.obfuscation.ObfuscationHelpers;
import org.cyclops.everlastingabilities.network.packet.SendPlayerCapabilitiesPacket;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * General ability helpers.
 * XP-related methods inspired by Ender IO's XpUtil for compatibility
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
        if (level == 0) { return 0; }
        if (level > 0 && level < 16) {
            return level * 17;
        } else if (level > 15 && level < 31) {
            return (int)(1.5 * Math.pow(level, 2) - 29.5 * level + 360);
        } else {
            return (int)(3.5 * Math.pow(level, 2) - 151.5 * level + 2220);
        }
    }

    public static int getLevelForExperience(int experience) {
        int i = 0;
        while (getExperienceForLevel(i) <= experience) {
            i++;
        }
        return i - 1;
    }

    public static void sendPlayerUpdateCapabilities(EntityPlayerMP player) {
        EverlastingAbilities._instance.getPacketHandler().sendToPlayer(
                new SendPlayerCapabilitiesPacket(ObfuscationHelpers.getEntityCapabilities(player)), player);
    }

    /**
     * Add the given ability.
     * @param ability The ability.
     * @param doAdd If the addition should actually be done.
     * @return The ability part that was added.
     */
    public static @Nullable Ability addPlayerAbility(EntityPlayer player, Ability ability, boolean doAdd, boolean modifyXp) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
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
        }
        if (player instanceof EntityPlayerMP) {
            sendPlayerUpdateCapabilities((EntityPlayerMP) player);
        }
        return result;
    }

    /**
     * Remove the given ability.
     * @param ability The ability.
     * @param doRemove If the removal should actually be done.
     * @return The ability part that was removed.
     */
    public static @Nullable Ability removePlayerAbility(EntityPlayer player, Ability ability, boolean doRemove, boolean modifyXp) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        Ability result = abilityStore.removeAbility(ability, doRemove);
        if (modifyXp && result != null) {
            player.addExperience(getExperience(result));
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

    public static IAbilityType getRandomAbility(Random random, EnumRarity rarity) {
        List<IAbilityType> abilities = AbilityTypes.REGISTRY.getAbilityTypes(EnumRarity.values()[Math.min(EnumRarity.values().length, rarity.ordinal())]);
        if (abilities.size() > 0) {
            return abilities.get(random.nextInt(abilities.size()));
        }
        return null;
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
        return rarity;
    }

    public static Triple<Integer, Integer, Integer> getAverageRarityColor(IAbilityStore abilityStore) {
        int r = 0;
        int g = 0;
        int b = 0;
        int count = 1;
        for (IAbilityType abilityType : abilityStore.getAbilityTypes()) {
            Triple<Float, Float, Float> color = Helpers.intToRGB(AbilityHelpers.RARITY_COLORS[abilityType.getRarity().ordinal()]);
            r += color.getLeft() * 255;
            g += color.getMiddle() * 255;
            b += color.getRight() * 255;
            count++;
        }
        return Triple.of(r / count, g / count, b / count);
    }

}
