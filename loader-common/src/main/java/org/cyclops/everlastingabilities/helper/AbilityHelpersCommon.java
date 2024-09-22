package org.cyclops.everlastingabilities.helper;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.CompoundTagMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.function.Consumer;
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
        return getEntityAbilityStore(player)
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
        return getEntityAbilityStore(player)
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
        getEntityAbilityStore(player)
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
    public ItemStack getTotem(Ability ability) {
        ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_TOTEM);
        getItemAbilityStore(itemStack)
                .ifPresent(mutableAbilityStore -> mutableAbilityStore.addAbility(ability, true));
        return itemStack;
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

    @Override
    public void injectLootTotem(Consumer<ItemStack> callback, LootContext context) {
        try {
            List<Holder<IAbilityType>> abilityTypes = this.getAbilityTypesLoot(EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getRegistry(context.getLevel().registryAccess()));
            this.getRandomRarity(abilityTypes, context.getRandom()).ifPresent(rarity -> {
                Holder<IAbilityType> abilityType = this.getRandomAbility(abilityTypes, context.getRandom(), rarity).get(); // Should always be present, as the method above guarantees that

                ItemStack stack = new ItemStack(RegistryEntries.ITEM_ABILITY_TOTEM);
                this.getItemAbilityStore(stack)
                        .ifPresent(mutableAbilityStore -> {
                            mutableAbilityStore.addAbility(new Ability(abilityType, 1), true);
                            callback.accept(stack);
                        });
            });
        } catch (IllegalStateException e) {
            // Do nothing on empty ability registry
        }
    }

    private boolean canMobHaveAbility(LivingEntity mob) {
        ResourceLocation mobName = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        return mobName != null && GeneralConfig.mobDropBlacklist.stream().noneMatch(mobName.toString()::matches);
    }

    @Override
    public void initializeEntityAbilities(Mob host, CompoundTagMutableAbilityStore store) {
        if (!host.getCommandSenderWorld().isClientSide
                && !store.isInitialized()
                && GeneralConfig.mobAbilityChance > 0
                && host.getId() % GeneralConfig.mobAbilityChance == 0
                && canMobHaveAbility(host)) {
            RandomSource rand = RandomSource.create();
            rand.setSeed(host.getId());
            Registry<IAbilityType> registry = this.getRegistry(host.level().registryAccess());
            List<Holder<IAbilityType>> abilityTypes = this.getAbilityTypesMobSpawn(registry);
            this.getRandomRarity(abilityTypes, rand)
                    .flatMap(rarity -> this.getRandomAbility(abilityTypes, rand, rarity))
                    .ifPresent(abilityType -> store.addAbility(new Ability(abilityType, 1), true));
        }
    }

    @Override
    public void initializePlayerAbilitiesOnSpawn(Player player) {
        Level world = player.level();
        if (world.registryAccess().registry(AbilityTypes.REGISTRY_KEY).isPresent() && GeneralConfig.totemMaximumSpawnRarity >= 0 && isFirstTotemSpawn(player)) {
            Rarity rarity = Rarity.values()[GeneralConfig.totemMaximumSpawnRarity];
            this.getRandomAbilityUntilRarity(this.getAbilityTypesPlayerSpawn(this.getRegistry(world.registryAccess())), world.random, rarity, true).ifPresent(abilityType -> {
                ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
                this.getItemAbilityStore(itemStack)
                        .ifPresent(mutableAbilityStore -> mutableAbilityStore.addAbility(new Ability(abilityType, 1), true));

                IModHelpers.get().getItemStackHelpers().spawnItemStackToPlayer(world, player.blockPosition(), itemStack, player);
                if (world instanceof ServerLevel serverLevel) {
                    ExperienceOrb.award(serverLevel, player.position(), abilityType.value().getXpPerLevelScaled());
                }
            });
        }
    }

    @Override
    public void onPlayerClone(Player playerOld, Player playerNew) {
        IMutableAbilityStore oldStore = this.getEntityAbilityStore(playerOld).orElse(null);
        IMutableAbilityStore newStore = this.getEntityAbilityStore(playerNew).orElse(null);
        if (oldStore != null && newStore != null) {
            newStore.setAbilities(Maps.newHashMap(oldStore.getAbilitiesRaw()));
        }
    }

    @Override
    public void onEntityDeath(Entity entity, DamageSource source) {
        boolean doMobLoot = entity.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        if (!entity.level().isClientSide
                && (entity instanceof Player
                ? (GeneralConfig.dropAbilitiesOnPlayerDeath > 0
                && (GeneralConfig.alwaysDropAbilities || source.getEntity() instanceof Player))
                : (doMobLoot && source.getEntity() instanceof Player))) {

            getEntityAbilityStore(entity).ifPresent(mutableAbilityStore -> {
                int toDrop = 1;
                if (entity instanceof Player
                        && (GeneralConfig.alwaysDropAbilities || source.getEntity() instanceof Player)) {
                    toDrop = GeneralConfig.dropAbilitiesOnPlayerDeath;
                }

                ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_TOTEM);
                IMutableAbilityStore itemStackStore = getItemAbilityStore(itemStack).get();

                Collection<Ability> abilities = Lists.newArrayList(mutableAbilityStore.getAbilities());
                for (Ability ability : abilities) {
                    if (toDrop > 0) {
                        Ability toRemove = new Ability(ability.getAbilityTypeHolder(), toDrop);
                        Ability removed = mutableAbilityStore.removeAbility(toRemove, true);
                        if (removed != null) {
                            toDrop -= removed.getLevel();
                            itemStackStore.addAbility(removed, true);
                            entity.sendSystemMessage(Component.translatable("chat.everlastingabilities.playerLostAbility",
                                    entity.getName(),
                                    Component.translatable(removed.getAbilityType().getTranslationKey())
                                            .setStyle(Style.EMPTY
                                                            .withBold(true)
                                                            .withColor(removed.getAbilityType().getRarity().color())),
                                    removed.getLevel()));
                        }
                    }
                }

                if (!itemStackStore.getAbilities().isEmpty()) {
                    IModHelpers.get().getItemStackHelpers().spawnItemStack(entity.level(), entity.blockPosition(), itemStack);
                }
            });
        }
    }

    @Override
    public void onEntityTick(Entity entity) {
        if (GeneralConfig.tickAbilities && entity instanceof Player player) {
            this.getEntityAbilityStore(player).ifPresent(abilityStore -> {
                for (Ability ability : abilityStore.getAbilities()) {
                    if (this.getPredicateAbilityEnabled().test(ability.getAbilityTypeHolder())) {
                        if (entity.level().getGameTime() % 20 == 0 && GeneralConfig.exhaustionPerAbilityTick > 0) {
                            player.causeFoodExhaustion((float) GeneralConfig.exhaustionPerAbilityTick);
                        }
                        ability.getAbilityType().onTick(player, ability.getLevel());
                    }
                }
            });
        }
    }
}
