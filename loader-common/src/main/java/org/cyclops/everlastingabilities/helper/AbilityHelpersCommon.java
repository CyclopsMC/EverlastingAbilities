package org.cyclops.everlastingabilities.helper;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * General ability helpers.
 * @author rubensworks
 */
public abstract class AbilityHelpersCommon implements IAbilityHelpers {

    private final IModHelpers modHelpers;
    private int maxPlayerAbilitiesClient = -1;

    public AbilityHelpersCommon(IModHelpers modHelpers) {
        this.modHelpers = modHelpers;
    }

    @Override
    public int getMaxPlayerAbilitiesClient() {
        return maxPlayerAbilitiesClient;
    }

    @Override
    public void setMaxPlayerAbilitiesClient(int maxPlayerAbilitiesClient) {
        this.maxPlayerAbilitiesClient = maxPlayerAbilitiesClient;
    }

    @Override
    public int[] getRarityColors() {
        return new int[] {
                this.modHelpers.getBaseHelpers().RGBToInt(255, 255, 255),
                this.modHelpers.getBaseHelpers().RGBToInt(255, 255, 0),
                this.modHelpers.getBaseHelpers().RGBToInt(0, 255, 255),
                this.modHelpers.getBaseHelpers().RGBToInt(255, 0, 255),
        };
    }

    @Override
    public Registry<IAbilityType> getRegistry(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(AbilityTypes.REGISTRY_KEY);
    }

    @Override
    public HolderLookup.RegistryLookup<IAbilityType> getRegistryLookup(HolderLookup.Provider holderLookupProvider) {
        return holderLookupProvider.lookupOrThrow(AbilityTypes.REGISTRY_KEY);
    }

    @Override
    public int getExperienceForLevel(int level) {
        if (level == 0) {
            return 0;
        } else if (level < 16) {
            return (int) (Math.pow(level, 2) + 6 * level);
        } else if (level < 32) {
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        } else {
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
        }
    }

    @Override
    public int getLevelForExperience(int experience) {
        int i = 0;
        int newXp, lastXp = -1;
        while ((newXp = getExperienceForLevel(i)) <= experience) {
            if (newXp <= lastXp) break; // Avoid infinite loops when the MC level is too high, resulting in an xp overflow. See https://github.com/CyclopsMC/EverlastingAbilities/issues/27
            i++;
            lastXp = newXp;
        }
        return i - 1;
    }

    @Override
    public Predicate<Holder<IAbilityType>> createRarityPredicate(Rarity rarity) {
        return abilityType -> abilityType.value().getRarity() == rarity;
    }

    @Override
    public List<Holder<IAbilityType>> getAbilityTypes(Registry<IAbilityType> registry, Predicate<Holder<IAbilityType>> abilityFilter) {
        return registry
                .holders()
                .filter(abilityFilter)
                .collect(Collectors.toList());
    }

    @Override
    public List<Holder<IAbilityType>> getAbilityTypes(HolderLookup.Provider holderLookupProvider, Predicate<Holder<IAbilityType>> abilityFilter) {
        return this.getRegistryLookup(holderLookupProvider)
                .listElements()
                .filter(abilityFilter)
                .collect(Collectors.toList());
    }

    @Override
    public List<Holder<IAbilityType>> getAbilityTypesPlayerSpawn(Registry<IAbilityType> registry) {
        return getAbilityTypes(registry, getPredicateAbilityEnabled().and(holder -> holder.value().isObtainableOnPlayerSpawn()));
    }

    @Override
    public List<Holder<IAbilityType>> getAbilityTypesMobSpawn(Registry<IAbilityType> registry) {
        return getAbilityTypes(registry, getPredicateAbilityEnabled().and(holder -> holder.value().isObtainableOnMobSpawn()));
    }

    @Override
    public List<Holder<IAbilityType>> getAbilityTypesCrafting(Registry<IAbilityType> registry) {
        return getAbilityTypes(registry, getPredicateAbilityEnabled().and(holder -> holder.value().isObtainableOnCraft()));
    }

    @Override
    public List<Holder<IAbilityType>> getAbilityTypesCrafting(HolderLookup.Provider provider) {
        return getAbilityTypes(provider, getPredicateAbilityEnabled().and(holder -> holder.value().isObtainableOnCraft()));
    }

    @Override
    public List<Holder<IAbilityType>> getAbilityTypesLoot(Registry<IAbilityType> registry) {
        return getAbilityTypes(registry, getPredicateAbilityEnabled().and(holder -> holder.value().isObtainableOnLoot()));
    }

    @Override
    public void onPlayerAbilityChanged(Player player, IAbilityType abilityType, int oldLevel, int newLevel) {
        abilityType.onChangedLevel(player, oldLevel, newLevel);
    }

    @Override
    public int getMaxPlayerAbilities(Level world) {
        return world.isClientSide() ? maxPlayerAbilitiesClient : GeneralConfig.maxPlayerAbilities;
    }

    @Override
    public Ability addPlayerAbility(Player player, Ability ability, boolean doAdd, boolean modifyXp) {
        return getPlayerAbilityStore(player)
                .map(abilityStore -> {
                    int oldLevel = abilityStore.hasAbilityType(ability.getAbilityTypeHolder())
                            ? abilityStore.getAbility(ability.getAbilityTypeHolder()).getLevel() : 0;

                    // Check max ability count
                    if (getMaxPlayerAbilities(player.getCommandSenderWorld()) >= 0 && oldLevel == 0
                            && getMaxPlayerAbilities(player.getCommandSenderWorld()) <= abilityStore.getAbilities().size()) {
                        return Ability.EMPTY;
                    }

                    Ability result = abilityStore.addAbility(ability, doAdd);
                    int currentXp = player.totalExperience;
                    if (result != null && modifyXp && getExperience(result) > currentXp) {
                        int maxLevels = player.totalExperience / result.getAbilityType().getXpPerLevelScaled();
                        if (maxLevels == 0) {
                            result = Ability.EMPTY;
                        } else {
                            result = new Ability(result.getAbilityTypeHolder(), maxLevels);
                        }
                    }
                    if (doAdd && !result.isEmpty()) {
                        player.totalExperience -= getExperience(result);
                        // Fix xp bar
                        player.experienceLevel = getLevelForExperience(player.totalExperience);
                        int xpForLevel = getExperienceForLevel(player.experienceLevel);
                        player.experienceProgress = (float)(player.totalExperience - xpForLevel) / (float)player.getXpNeededForNextLevel();

                        int newLevel = abilityStore.getAbility(result.getAbilityTypeHolder()).getLevel();
                        onPlayerAbilityChanged(player, result.getAbilityType(), oldLevel, newLevel);
                    }
                    return result;
                })
                .orElse(Ability.EMPTY);
    }

    @Override
    public Ability removePlayerAbility(Player player, Ability ability, boolean doRemove, boolean modifyXp) {
        return getPlayerAbilityStore(player)
                .map(abilityStore -> {
                    int oldLevel = abilityStore.hasAbilityType(ability.getAbilityTypeHolder())
                            ? abilityStore.getAbility(ability.getAbilityTypeHolder()).getLevel() : 0;
                    Ability result = abilityStore.removeAbility(ability, doRemove);
                    if (modifyXp && !result.isEmpty()) {
                        player.giveExperiencePoints(getExperience(result));
                        int newLevel = abilityStore.hasAbilityType(result.getAbilityTypeHolder())
                                ? abilityStore.getAbility(result.getAbilityTypeHolder()).getLevel() : 0;
                        onPlayerAbilityChanged(player, result.getAbilityType(), oldLevel, newLevel);
                    }
                    return result;
                })
                .orElse(Ability.EMPTY);
    }

    @Override
    public void setPlayerAbilities(ServerPlayer player, Map<Holder<IAbilityType>, Integer> abilityTypes) {
        getPlayerAbilityStore(player)
                .ifPresent(abilityStore -> abilityStore.setAbilities(abilityTypes));
    }

    @Override
    public int getExperience(Ability ability) {
        if (ability.isEmpty()) {
            return 0;
        }
        return ability.getAbilityType().getXpPerLevelScaled() * ability.getLevel();
    }

    @Override
    public boolean canInsert(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        Ability added = mutableAbilityStore.addAbility(ability, false);
        return added.getLevel() == ability.getLevel();
    }

    @Override
    public boolean canExtract(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        Ability added = mutableAbilityStore.removeAbility(ability, false);
        return added.getLevel() == ability.getLevel();
    }

    @Override
    public boolean canInsertToPlayer(Ability ability, Player player) {
        Ability added = addPlayerAbility(player, ability, false, true);
        return added.getLevel() == ability.getLevel();
    }

    @Override
    public Ability insert(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        return mutableAbilityStore.addAbility(ability, true);
    }

    @Override
    public Ability extract(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        return mutableAbilityStore.removeAbility(ability, true);
    }

    @Override
    public Optional<Holder<IAbilityType>> getRandomAbility(List<Holder<IAbilityType>> abilityTypes, RandomSource random, Rarity rarity) {
        List<Holder<IAbilityType>> filtered = abilityTypes.stream().filter(createRarityPredicate(rarity)).toList();
        if (filtered.size() > 0) {
            return Optional.of(filtered.get(random.nextInt(filtered.size())));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Holder<IAbilityType>> getRandomAbilityUntilRarity(List<Holder<IAbilityType>> abilityTypes, RandomSource random, Rarity rarity, boolean inclusive) {
        NavigableSet<Rarity> validRarities = this.getValidAbilityRarities(abilityTypes).headSet(rarity, inclusive);
        Iterator<Rarity> it = validRarities.descendingIterator();
        while (it.hasNext()) {
            Optional<Holder<IAbilityType>> optional = getRandomAbility(abilityTypes, random, it.next());
            if (optional.isPresent()) {
                return optional;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> getRandomTotem(List<Holder<IAbilityType>> abilityTypes, Rarity rarity, RandomSource rand) {
        return getRandomAbility(abilityTypes, rand, rarity).flatMap(
                abilityType -> Optional.of(getTotem(new Ability(abilityType, 1))));
    }

    @Override
    public Optional<Rarity> getRandomRarity(List<Holder<IAbilityType>> abilityTypes, RandomSource rand) {
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
                return Optional.empty();
            }
            rarity = Iterables.get(abilityTypes, rand.nextInt(size)).value().getRarity();
        }

        return Optional.of(rarity);
    }

    @Override
    public boolean hasRarityAbilities(List<Holder<IAbilityType>> abilityTypes, Rarity rarity) {
        return abilityTypes.stream().anyMatch(createRarityPredicate(rarity));
    }

    @Override
    public NavigableSet<Rarity> getValidAbilityRarities(List<Holder<IAbilityType>> abilityTypes) {
        NavigableSet<Rarity> rarities = Sets.newTreeSet();
        for (Rarity rarity : Rarity.values()) {
            if (hasRarityAbilities(abilityTypes, rarity)) {
                rarities.add(rarity);
            }
        }
        return rarities;
    }

    @Override
    public Triple<Integer, Integer, Integer> getAverageRarityColor(IAbilityStore abilityStore) {
        int r = 0;
        int g = 0;
        int b = 0;
        int count = 1;
        int[] rarityColors = getRarityColors();
        for (Holder<IAbilityType> abilityType : abilityStore.getAbilityTypes()) {
            Triple<Float, Float, Float> color = modHelpers.getBaseHelpers().intToRGB(rarityColors
                    [Math.min(rarityColors.length - 1, abilityType.value().getRarity().ordinal())]);
            r += color.getLeft() * 255;
            g += color.getMiddle() * 255;
            b += color.getRight() * 255;
            count++;
        }
        return Triple.of(r / count, g / count, b / count);
    }

    @Override
    public Supplier<Rarity> getSafeRarity(Supplier<Integer> rarityGetter) {
        return () -> {
            Integer rarity = rarityGetter.get();
            return rarity < 0 ? Rarity.COMMON : (rarity >= Rarity.values().length ? Rarity.EPIC : Rarity.values()[rarity]);
        };
    }

    @Override
    public Tag serialize(Registry<IAbilityType> registry, IMutableAbilityStore capability) {
        ListTag list = new ListTag();
        for (Ability ability : capability.getAbilities()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", registry.getKey(ability.getAbilityType()).toString());
            tag.putInt("level", ability.getLevel());
            list.add(tag);
        }
        return list;
    }

    @Override
    public void deserialize(Registry<IAbilityType> registry, IMutableAbilityStore capability, Tag nbt) {
        Map<Holder<IAbilityType>, Integer> abilityTypes = Maps.newHashMap();
        if (nbt instanceof ListTag) {
            if (((ListTag) nbt).getElementType() == Tag.TAG_COMPOUND) {
                ListTag list = (ListTag) nbt;
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag tag = list.getCompound(i);
                    String name = tag.getString("name");
                    int level = tag.getInt("level");
                    Optional<Holder.Reference<IAbilityType>> abilityTypeOptional = registry.getHolder(ResourceLocation.parse(name));
                    if (abilityTypeOptional.isPresent()) {
                        abilityTypes.put(abilityTypeOptional.get(), level);
                    } else {
                        EverlastingAbilitiesInstance.MOD.log(org.apache.logging.log4j.Level.WARN, "Skipped loading unknown ability by name: " + name);
                    }
                }
            }
        } else {
            EverlastingAbilitiesInstance.MOD.log(org.apache.logging.log4j.Level.WARN, "Resetting a corrupted ability storage.");
        }
        capability.setAbilities(abilityTypes);
    }
}
