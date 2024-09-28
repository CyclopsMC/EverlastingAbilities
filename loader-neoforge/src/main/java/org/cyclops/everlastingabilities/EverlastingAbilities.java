package org.cyclops.everlastingabilities;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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
import org.cyclops.cyclopscore.config.ConfigHandlerCommon;
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
import org.cyclops.everlastingabilities.api.AbilityTypeSerializers;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.capability.CompoundTagMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.command.CommandModifyAbilities;
import org.cyclops.everlastingabilities.command.argument.ArgumentTypeAbilityConfig;
import org.cyclops.everlastingabilities.component.DataComponentAbilityStoreConfig;
import org.cyclops.everlastingabilities.helper.AbilityHelpersNeoForge;
import org.cyclops.everlastingabilities.helper.IAbilityHelpers;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainerConfig;
import org.cyclops.everlastingabilities.item.ItemAbilityBottleConfigNeoForge;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfigNeoForge;
import org.cyclops.everlastingabilities.loot.modifier.LootModifierInjectAbilityTotemConfig;
import org.cyclops.everlastingabilities.network.packet.RequestAbilityStorePacket;
import org.cyclops.everlastingabilities.proxy.ClientProxy;
import org.cyclops.everlastingabilities.proxy.CommonProxy;
import org.cyclops.everlastingabilities.recipe.TotemRecycleRecipeConfig;

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
                    getAbilityHelpers().initializeEntityAbilities(host, store);
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

    @Override
    protected CreativeModeTab.Builder constructDefaultCreativeModeTab(CreativeModeTab.Builder builder) {
        return super.constructDefaultCreativeModeTab(builder)
                .icon(() -> new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE));
    }

    @Override
    protected void onConfigsRegister(ConfigHandlerCommon configHandler) {
        super.onConfigsRegister(configHandler);
        configHandler.addConfigurable(new GeneralConfig(this));

        // Argument types
        configHandler.addConfigurable(new ArgumentTypeAbilityConfig<>(this));

        // Guis
        configHandler.addConfigurable(new ContainerAbilityContainerConfig<>(this));

        // Recipes
        configHandler.addConfigurable(new TotemRecycleRecipeConfig<>(this));

        // Items
        configHandler.addConfigurable(new ItemAbilityTotemConfigNeoForge());
        configHandler.addConfigurable(new ItemAbilityBottleConfigNeoForge());

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
        if (event.getLevel().isClientSide && getAbilityHelpers().getEntityAbilityStore(event.getEntity()).isPresent()) {
            getPacketHandlerCommon().sendToServer(new RequestAbilityStorePacket(event.getEntity().getUUID().toString()));
        }
    }

    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        getAbilityHelpers().initializePlayerAbilitiesOnSpawn(event.getEntity());
    }

    public void onLivingDeath(LivingDeathEvent event) {
        getAbilityHelpers().onEntityDeath(event.getEntity(), event.getSource());
    }

    public void onPlayerClone(net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone event) {
        getAbilityHelpers().onPlayerClone(event.getOriginal(), event.getEntity());
    }

    public void onLivingUpdate(EntityTickEvent.Post event) {
        getAbilityHelpers().onEntityTick(event.getEntity());
    }

    private void onDatapackRegistryCreate(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(AbilityTypes.REGISTRY_KEY, AbilityTypes.DIRECT_CODEC, AbilityTypes.DIRECT_CODEC);
    }

    private void onRegistriesCreate(NewRegistryEvent event) {
        AbilityTypeSerializers.REGISTRY = event.create(new RegistryBuilder<>(AbilityTypeSerializers.REGISTRY_KEY));
        AbilityTypeSerializers.NAME_CODEC = AbilityTypeSerializers.REGISTRY.byNameCodec();
    }

}
