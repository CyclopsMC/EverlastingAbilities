package org.cyclops.everlastingabilities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.BaseCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.helper.EntityHelpers;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.init.ModBaseVersionable;
import org.cyclops.cyclopscore.modcompat.capabilities.ICapabilityConstructor;
import org.cyclops.cyclopscore.proxy.IClientProxy;
import org.cyclops.cyclopscore.proxy.ICommonProxy;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeAttributeModifierSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeEffectSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialBonemealerSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialFertilitySerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialFlightSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialMagnetizeSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialPowerStareSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialStepAssistSerializerConfig;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.AbilityTypeSerializers;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.CompoundTagMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.command.CommandModifyAbilities;
import org.cyclops.everlastingabilities.command.argument.ArgumentTypeAbilityConfig;
import org.cyclops.everlastingabilities.component.DataComponentAbilityStoreConfig;
import org.cyclops.everlastingabilities.helper.AbilityHelpersNeoForge;
import org.cyclops.everlastingabilities.helper.IAbilityHelpers;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainerConfig;
import org.cyclops.everlastingabilities.item.ItemAbilityBottleConfig;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfig;
import org.cyclops.everlastingabilities.loot.modifier.LootModifierInjectAbilityTotemConfig;
import org.cyclops.everlastingabilities.network.packet.RequestAbilityStorePacket;
import org.cyclops.everlastingabilities.proxy.ClientProxy;
import org.cyclops.everlastingabilities.proxy.CommonProxy;
import org.cyclops.everlastingabilities.recipe.TotemRecycleRecipeConfig;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The main mod class of this mod.
 * @author rubensworks (aka kroeserr)
 *
 */
@Mod(Reference.MOD_ID)
public class EverlastingAbilities extends ModBaseVersionable<EverlastingAbilities> implements IEverlastingAbilitiesModBase {

    /**
     * The unique instance of this mod.
     */
    public static EverlastingAbilities _instance;
    private final IAbilityHelpers abilityHelpers;

    public EverlastingAbilities(IEventBus modEventBus) {
        super(Reference.MOD_ID, (instance) -> {
            _instance = instance;
            EverlastingAbilitiesInstance.MOD = instance;
        }, modEventBus);

        // Register capabilities
        getCapabilityConstructorRegistry().registerEntity(() -> EntityType.PLAYER, new ICapabilityConstructor<Player, Void, IMutableAbilityStore, EntityType<Player>>() {
            @Override
            public BaseCapability<IMutableAbilityStore, Void> getCapability() {
                return Capabilities.MutableAbilityStore.ENTITY;
            }

            @Override
            public ICapabilityProvider<Player, Void, IMutableAbilityStore> createProvider(EntityType<Player> host) {
                return (player, context) -> {
                    if (player.level().registryAccess().registry(AbilityTypes.REGISTRY_KEY).isEmpty()) {
                        return null;
                    }
                    return new CompoundTagMutableAbilityStore(player::getPersistentData, player.level().registryAccess());
                };
            }
        });
        ICapabilityConstructor<Mob, Void, IMutableAbilityStore, EntityType<?>> capCtor = new ICapabilityConstructor<>() {
            @Override
            public ICapabilityProvider<Mob, Void, IMutableAbilityStore> createProvider(EntityType<?> key) {
                return (host, context) -> {
                    if (host.level().registryAccess().registry(AbilityTypes.REGISTRY_KEY).isEmpty()) {
                        return null;
                    }
                    CompoundTagMutableAbilityStore store = new CompoundTagMutableAbilityStore(host::getPersistentData, host.level().registryAccess());
                    if (!host.getCommandSenderWorld().isClientSide
                            && !store.isInitialized()
                            && GeneralConfig.mobAbilityChance > 0
                            && host.getId() % GeneralConfig.mobAbilityChance == 0
                            && canMobHaveAbility(host)) {
                        RandomSource rand = RandomSource.create();
                        rand.setSeed(host.getId());
                        IAbilityHelpers abilityHelpers = getAbilityHelpers();
                        Registry<IAbilityType> registry = abilityHelpers.getRegistry(host.level().registryAccess());
                        List<Holder<IAbilityType>> abilityTypes = abilityHelpers.getAbilityTypesMobSpawn(registry);
                        abilityHelpers.getRandomRarity(abilityTypes, rand)
                                .flatMap(rarity -> abilityHelpers.getRandomAbility(abilityTypes, rand, rarity))
                                .ifPresent(abilityType -> store.addAbility(new Ability(abilityType, 1), true));
                    }
                    return store;
                };
            }

            @Override
            public BaseCapability<IMutableAbilityStore, Void> getCapability() {
                return Capabilities.MutableAbilityStore.ENTITY;
            }
        };
        getCapabilityConstructorRegistry().registerMobCategoryEntity(MobCategory.MONSTER, capCtor);
        getCapabilityConstructorRegistry().registerMobCategoryEntity(MobCategory.CREATURE, capCtor);
        getCapabilityConstructorRegistry().registerMobCategoryEntity(MobCategory.UNDERGROUND_WATER_CREATURE, capCtor);
        getCapabilityConstructorRegistry().registerMobCategoryEntity(MobCategory.WATER_CREATURE, capCtor);

        NeoForge.EVENT_BUS.addListener(this::onEntityJoinWorld);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(this::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(this::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(this::onLivingUpdate);
        modEventBus.addListener(this::onRegistriesCreate);
        modEventBus.addListener(this::onDatapackRegistryCreate);
        this.abilityHelpers = new AbilityHelpersNeoForge(getModHelpers());
    }

    @Override
    public IAbilityHelpers getAbilityHelpers() {
        return this.abilityHelpers;
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> constructBaseCommand(Commands.CommandSelection selection, CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> root = super.constructBaseCommand(selection, context);

        root.then(CommandModifyAbilities.make(context));

        return root;
    }

    @Override
    protected IClientProxy constructClientProxy() {
        return new ClientProxy();
    }

    @Override
    protected ICommonProxy constructCommonProxy() {
        return new CommonProxy();
    }

    private static boolean canMobHaveAbility(LivingEntity mob) {
        ResourceLocation mobName = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        return mobName != null && GeneralConfig.mobDropBlacklist.stream().noneMatch(mobName.toString()::matches);
    }

    @Override
    protected CreativeModeTab.Builder constructDefaultCreativeModeTab(CreativeModeTab.Builder builder) {
        return super.constructDefaultCreativeModeTab(builder)
                .icon(() -> new ItemStack(RegistryEntriesCommon.ITEM_ABILITY_BOTTLE));
    }

    @Override
    protected void onConfigsRegister(ConfigHandler configHandler) {
        super.onConfigsRegister(configHandler);
        configHandler.addConfigurable(new GeneralConfig(this));

        // Argument types
        configHandler.addConfigurable(new ArgumentTypeAbilityConfig<>(this));

        // Guis
        configHandler.addConfigurable(new ContainerAbilityContainerConfig());

        // Recipes
        configHandler.addConfigurable(new TotemRecycleRecipeConfig());

        // Items
        configHandler.addConfigurable(new ItemAbilityTotemConfig());
        configHandler.addConfigurable(new ItemAbilityBottleConfig());

        // Ability serializers
        configHandler.addConfigurable(new AbilityTypeEffectSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeAttributeModifierSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialBonemealerSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialFertilitySerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialFlightSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialMagnetizeSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialPowerStareSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialStepAssistSerializerConfig<>(this));

        // Loot modifiers
        configHandler.addConfigurable(new LootModifierInjectAbilityTotemConfig());

        // Data components
        configHandler.addConfigurable(new DataComponentAbilityStoreConfig<>(this));
    }

    /**
     * Log a new info message for this mod.
     * @param message The message to show.
     */
    public static void clog(String message) {
        clog(org.apache.logging.log4j.Level.INFO, message);
    }

    /**
     * Log a new message of the given level for this mod.
     * @param level The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void clog(org.apache.logging.log4j.Level level, String message) {
        EverlastingAbilities._instance.getLoggerHelper().log(level, message);
    }

    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide && event.getEntity().getCapability(Capabilities.MutableAbilityStore.ENTITY) != null) {
            getPacketHandler().sendToServer(new RequestAbilityStorePacket(event.getEntity().getUUID().toString()));
        }
    }

    private static final String NBT_TOTEM_SPAWNED = Reference.MOD_ID + ":totemSpawned";
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().registryAccess().registry(AbilityTypes.REGISTRY_KEY).isPresent() && GeneralConfig.totemMaximumSpawnRarity >= 0) {
            CompoundTag tag = event.getEntity().getPersistentData();
            if (!tag.contains(Player.PERSISTED_NBT_TAG)) {
                tag.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
            }
            CompoundTag playerTag = tag.getCompound(Player.PERSISTED_NBT_TAG);
            if (!playerTag.contains(NBT_TOTEM_SPAWNED)) {
                playerTag.putBoolean(NBT_TOTEM_SPAWNED, true);

                Level world = event.getEntity().level();
                Player player = event.getEntity();
                Rarity rarity = Rarity.values()[GeneralConfig.totemMaximumSpawnRarity];
                getAbilityHelpers().getRandomAbilityUntilRarity(getAbilityHelpers().getAbilityTypesPlayerSpawn(getAbilityHelpers().getRegistry(world.registryAccess())), world.random, rarity, true).ifPresent(abilityType -> {
                    ItemStack itemStack = new ItemStack(RegistryEntriesCommon.ITEM_ABILITY_BOTTLE);
                    Optional.ofNullable(itemStack.getCapability(Capabilities.MutableAbilityStore.ITEM))
                            .ifPresent(mutableAbilityStore -> mutableAbilityStore.addAbility(new Ability(abilityType, 1), true));

                    ItemStackHelpers.spawnItemStackToPlayer(world, player.blockPosition(), itemStack, player);
                    EntityHelpers.spawnXpAtPlayer(world, player, abilityType.value().getXpPerLevelScaled());
                });
            }
        }
    }

    public void onLivingDeath(LivingDeathEvent event) {
        boolean doMobLoot = event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        if (!event.getEntity().level().isClientSide
                && (event.getEntity() instanceof Player
                    ? (GeneralConfig.dropAbilitiesOnPlayerDeath > 0
                        && (GeneralConfig.alwaysDropAbilities || event.getSource().getEntity() instanceof Player))
                    : (doMobLoot && event.getSource().getEntity() instanceof Player))) {
            LivingEntity entity = event.getEntity();
            Optional.ofNullable(entity.getCapability(Capabilities.MutableAbilityStore.ENTITY)).ifPresent(mutableAbilityStore -> {
                int toDrop = 1;
                if (event.getEntity() instanceof Player
                        && (GeneralConfig.alwaysDropAbilities || event.getSource().getEntity() instanceof Player)) {
                    toDrop = GeneralConfig.dropAbilitiesOnPlayerDeath;
                }

                ItemStack itemStack = new ItemStack(RegistryEntriesCommon.ITEM_ABILITY_TOTEM);
                IMutableAbilityStore itemStackStore = itemStack.getCapability(Capabilities.MutableAbilityStore.ITEM);

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
                                            .setStyle(removed.getAbilityType().getRarity().getStyleModifier()
                                                    .apply(Style.EMPTY.withBold(true))),
                                    removed.getLevel()));
                        }
                    }
                }

                if (!itemStackStore.getAbilities().isEmpty()) {
                    ItemStackHelpers.spawnItemStack(entity.level(), entity.blockPosition(), itemStack);
                }
            });
        }
    }

    public void onPlayerClone(net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone event) {
        IMutableAbilityStore oldStore = event.getOriginal().getCapability(Capabilities.MutableAbilityStore.ENTITY);
        IMutableAbilityStore newStore = event.getEntity().getCapability(Capabilities.MutableAbilityStore.ENTITY);
        if (oldStore != null && newStore != null) {
            newStore.setAbilities(Maps.newHashMap(oldStore.getAbilitiesRaw()));
        }
    }

    public void onLivingUpdate(EntityTickEvent.Post event) {
        if (GeneralConfig.tickAbilities && event.getEntity() instanceof Player player) {
            Optional.ofNullable(player.getCapability(Capabilities.MutableAbilityStore.ENTITY)).ifPresent(abilityStore -> {
                for (Ability ability : abilityStore.getAbilities()) {
                    if (getAbilityHelpers().getPredicateAbilityEnabled().test(ability.getAbilityTypeHolder())) {
                        if (event.getEntity().level().getGameTime() % 20 == 0 && GeneralConfig.exhaustionPerAbilityTick > 0) {
                            player.causeFoodExhaustion((float) GeneralConfig.exhaustionPerAbilityTick);
                        }
                        ability.getAbilityType().onTick(player, ability.getLevel());
                    }
                }
            });
        }
    }

    private void onDatapackRegistryCreate(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(AbilityTypes.REGISTRY_KEY, AbilityTypes.DIRECT_CODEC, AbilityTypes.DIRECT_CODEC);
    }

    private void onRegistriesCreate(NewRegistryEvent event) {
        AbilityTypeSerializers.REGISTRY = event.create(new RegistryBuilder<>(AbilityTypeSerializers.REGISTRY_KEY));
    }

}
