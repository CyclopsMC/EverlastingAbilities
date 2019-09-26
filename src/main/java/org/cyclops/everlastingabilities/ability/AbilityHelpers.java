package org.cyclops.everlastingabilities.ability;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import lombok.NonNull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.AbilityTypes;
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
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public static Predicate<IAbilityType> createRarityPredicate(Rarity rarity) {
        return abilityType -> abilityType.getRarity() == rarity;
    }

    public static List<IAbilityType> getAbilityTypes(Predicate<IAbilityType> abilityFilter) {
        return AbilityTypes.REGISTRY.getValues()
                .stream()
                .filter(abilityFilter)
                .collect(Collectors.toList());
    }

    public static List<IAbilityType> getAbilityTypesPlayerSpawn() {
        return getAbilityTypes(IAbilityType::isObtainableOnPlayerSpawn);
    }

    public static List<IAbilityType> getAbilityTypesMobSpawn() {
        return getAbilityTypes(IAbilityType::isObtainableOnMobSpawn);
    }

    public static List<IAbilityType> getAbilityTypesCrafting() {
        return getAbilityTypes(IAbilityType::isObtainableOnCraft);
    }

    public static List<IAbilityType> getAbilityTypesLoot() {
        return getAbilityTypes(IAbilityType::isObtainableOnLoot);
    }

    public static void onPlayerAbilityChanged(PlayerEntity player, IAbilityType abilityType, int oldLevel, int newLevel) {
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
    @NonNull
    public static Ability addPlayerAbility(PlayerEntity player, Ability ability, boolean doAdd, boolean modifyXp) {
        return player.getCapability(MutableAbilityStoreConfig.CAPABILITY)
                .map(abilityStore -> {
                    int oldLevel = abilityStore.hasAbilityType(ability.getAbilityType())
                            ? abilityStore.getAbility(ability.getAbilityType()).getLevel() : 0;

                    // Check max ability count
                    if (GeneralConfig.maxPlayerAbilities >= 0 && oldLevel == 0
                            && GeneralConfig.maxPlayerAbilities <= abilityStore.getAbilities().size()) {
                        return Ability.EMPTY;
                    }

                    Ability result = abilityStore.addAbility(ability, doAdd);
                    int currentXp = player.experienceTotal;
                    if (result != null && modifyXp && getExperience(result) > currentXp) {
                        int maxLevels = player.experienceTotal / result.getAbilityType().getBaseXpPerLevel();
                        if (maxLevels == 0) {
                            result = Ability.EMPTY;
                        } else {
                            result = new Ability(result.getAbilityType(), maxLevels);
                        }
                    }
                    if (doAdd && !result.isEmpty()) {
                        player.experienceTotal -= getExperience(result);
                        // Fix xp bar
                        player.experienceLevel = getLevelForExperience(player.experienceTotal);
                        int xpForLevel = getExperienceForLevel(player.experienceLevel);
                        player.experience = (float)(player.experienceTotal - xpForLevel) / (float)player.xpBarCap();

                        int newLevel = abilityStore.getAbility(result.getAbilityType()).getLevel();
                        onPlayerAbilityChanged(player, result.getAbilityType(), oldLevel, newLevel);
                    }
                    return result;
                })
                .orElse(Ability.EMPTY);
    }

    /**
     * Remove the given ability.
     * @param player The player.
     * @param ability The ability.
     * @param doRemove If the removal should actually be done.
     * @param modifyXp Whether to refund XP cost of ability
     * @return The ability part that was removed.
     */
    @NonNull
    public static Ability removePlayerAbility(PlayerEntity player, Ability ability, boolean doRemove, boolean modifyXp) {
        return player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                .map(abilityStore -> {
                    int oldLevel = abilityStore.hasAbilityType(ability.getAbilityType())
                            ? abilityStore.getAbility(ability.getAbilityType()).getLevel() : 0;
                    Ability result = abilityStore.removeAbility(ability, doRemove);
                    if (modifyXp && !result.isEmpty()) {
                        player.giveExperiencePoints(getExperience(result));
                        int newLevel = abilityStore.hasAbilityType(result.getAbilityType())
                                ? abilityStore.getAbility(result.getAbilityType()).getLevel() : 0;
                        onPlayerAbilityChanged(player, result.getAbilityType(), oldLevel, newLevel);
                    }
                    return result;
                })
                .orElse(Ability.EMPTY);
    }

    public static int getExperience(@NonNull Ability ability) {
        if (ability.isEmpty()) {
            return 0;
        }
        return ability.getAbilityType().getBaseXpPerLevel() * ability.getLevel();
    }

    public static void setPlayerAbilities(ServerPlayerEntity player, Map<IAbilityType, Integer> abilityTypes) {
        player.getCapability(MutableAbilityStoreConfig.CAPABILITY)
                .ifPresent(abilityStore -> abilityStore.setAbilities(abilityTypes));
    }

    public static boolean canInsert(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        Ability added = mutableAbilityStore.addAbility(ability, false);
        return added.getLevel() == ability.getLevel();
    }

    public static boolean canExtract(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        Ability added = mutableAbilityStore.removeAbility(ability, false);
        return added.getLevel() == ability.getLevel();
    }

    public static boolean canInsertToPlayer(Ability ability, PlayerEntity player) {
        Ability added = addPlayerAbility(player, ability, false, true);
        return added.getLevel() == ability.getLevel();
    }

    public static Ability insert(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        return mutableAbilityStore.addAbility(ability, true);
    }

    public static Ability extract(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        return mutableAbilityStore.removeAbility(ability, true);
    }

    public static Optional<IAbilityType> getRandomAbility(List<IAbilityType> abilityTypes, Random random, Rarity rarity) {
        List<IAbilityType> filtered = abilityTypes.stream().filter(createRarityPredicate(rarity)).collect(Collectors.toList());
        if (filtered.size() > 0) {
            return Optional.of(filtered.get(random.nextInt(filtered.size())));
        }
        return Optional.empty();
    }

    public static Optional<IAbilityType> getRandomAbilityUntilRarity(List<IAbilityType> abilityTypes, Random random, Rarity rarity, boolean inclusive) {
        NavigableSet<Rarity> validRarities = AbilityHelpers.getValidAbilityRarities(abilityTypes).headSet(rarity, inclusive);
        Iterator<Rarity> it = validRarities.descendingIterator();
        while (it.hasNext()) {
            Optional<IAbilityType> optional = getRandomAbility(abilityTypes, random, it.next());
            if (optional.isPresent()) {
                return optional;
            }
        }
        return Optional.empty();
    }

    public static Optional<ItemStack> getRandomTotem(List<IAbilityType> abilityTypes, Rarity rarity, Random rand) {
        return getRandomAbility(abilityTypes, rand, rarity).flatMap(
                abilityType -> Optional.of(ItemAbilityTotem.getTotem(new Ability(abilityType, 1))));
    }
    

    public static Rarity getRandomRarity(List<IAbilityType> abilityTypes, Random rand) {
        int chance = rand.nextInt(50);
        Rarity rarity;
        if (chance >= 49) {
            rarity = Rarity.EPIC;
        } else if (chance >= 40) {
            rarity = Rarity.RARE;
        } else if (chance >= 25) {
            rarity = Rarity.UNCOMMON;
        } else {
            rarity = Rarity.COMMON;
        }

        // Fallback to a random selection of a rarity that is guaranteed to exist in the registered abilities
        if (!hasRarityAbilities(abilityTypes, rarity)) {
            int size = abilityTypes.size();
            if (size == 0) {
                throw new IllegalStateException("No abilities were registered, at least one ability must be enabled for this mod to function correctly.");
            }
            rarity = Iterables.get(abilityTypes, rand.nextInt(size)).getRarity();
        }

        return rarity;
    }

    public static boolean hasRarityAbilities(List<IAbilityType> abilityTypes, Rarity rarity) {
        return abilityTypes.stream().anyMatch(createRarityPredicate(rarity));
    }

    public static NavigableSet<Rarity> getValidAbilityRarities(List<IAbilityType> abilityTypes) {
        NavigableSet<Rarity> rarities = Sets.newTreeSet();
        for (Rarity rarity : Rarity.values()) {
            if (hasRarityAbilities(abilityTypes, rarity)) {
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

    public static Supplier<Rarity> getSafeRarity(Supplier<Integer> rarityGetter) {
        return () -> {
            Integer rarity = rarityGetter.get();
            return rarity < 0 ? Rarity.COMMON : (rarity >= Rarity.values().length ? Rarity.EPIC : Rarity.values()[rarity]);
        };
    }

}
