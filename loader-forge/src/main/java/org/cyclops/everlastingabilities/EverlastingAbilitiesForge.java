package org.cyclops.everlastingabilities;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.MapCodec;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.cyclops.cyclopscore.config.ConfigHandlerCommon;
import org.cyclops.cyclopscore.init.ModBaseForge;
import org.cyclops.cyclopscore.proxy.IClientProxyCommon;
import org.cyclops.cyclopscore.proxy.ICommonProxyCommon;
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
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.CompoundTagMutableAbilityStore;
import org.cyclops.everlastingabilities.command.CommandModifyAbilities;
import org.cyclops.everlastingabilities.command.argument.ArgumentTypeAbilityConfig;
import org.cyclops.everlastingabilities.component.DataComponentAbilityStoreConfig;
import org.cyclops.everlastingabilities.core.config.ExtendedConfigurableType;
import org.cyclops.everlastingabilities.core.config.configurabletypeaction.AbilitySerializerActionForge;
import org.cyclops.everlastingabilities.helper.AbilityHelpersForge;
import org.cyclops.everlastingabilities.helper.IAbilityHelpers;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainerConfig;
import org.cyclops.everlastingabilities.item.ItemAbilityBottleConfigForge;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfigForge;
import org.cyclops.everlastingabilities.loot.modifier.LootModifierInjectAbilityTotemConfig;
import org.cyclops.everlastingabilities.network.packet.RequestAbilityStorePacket;
import org.cyclops.everlastingabilities.proxy.ClientProxyForge;
import org.cyclops.everlastingabilities.proxy.CommonProxyForge;
import org.cyclops.everlastingabilities.recipe.TotemRecycleRecipeConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main mod class of this mod.
 * @author rubensworks
 *
 */
@Mod(Reference.MOD_ID)
public class EverlastingAbilitiesForge extends ModBaseForge<EverlastingAbilitiesForge> implements IEverlastingAbilitiesModBase {

    /**
     * The unique instance of this mod.
     */
    public static EverlastingAbilitiesForge _instance;
    private final IAbilityHelpers abilityHelpers;

    public static IForgeRegistry<MapCodec<? extends IAbilityType>> REGISTRY_ABILITY_SERIALIZERS;

    public EverlastingAbilitiesForge() {
        super(Reference.MOD_ID, (instance) -> {
            _instance = instance;
            EverlastingAbilitiesInstance.MOD = instance;
        });

        ExtendedConfigurableType.ABILITY_SERIALIZER.setAction(new AbilitySerializerActionForge<>());

        MinecraftForge.EVENT_BUS.addListener(this::onEntityJoinWorld);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerClone);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingUpdate);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::onAttachCapabilities);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegistriesCreate);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onDatapackRegistryCreate);
        this.abilityHelpers = new AbilityHelpersForge(getModHelpers());
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

    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Mob entity) {
            attachEntityCapability(event, entity);
        } else if (event.getObject() instanceof Player entity) {
            attachEntityCapability(event, entity);
        }
    }

    protected void attachEntityCapability(AttachCapabilitiesEvent<?> event, Entity entity) {
        event.addCapability(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "ability_store"), new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
                if (!entity.level().registryAccess().registry(AbilityTypes.REGISTRY_KEY).isEmpty()) {
                    if (capability == CapabilitiesForge.CAPABILITY) {
                        return LazyOptional.of(() -> {
                            CompoundTagMutableAbilityStore store = new CompoundTagMutableAbilityStore(entity::getPersistentData, entity.level().registryAccess());
                            if (entity instanceof Mob mob) {
                                getAbilityHelpers().initializeEntityAbilities(mob, store);
                            }
                            return store;
                        }).cast();
                    }
                }
                return LazyOptional.empty();
            }
        });
    }

    @Override
    protected IClientProxyCommon constructClientProxy() {
        return new ClientProxyForge();
    }

    @Override
    protected ICommonProxyCommon constructCommonProxy() {
        return new CommonProxyForge();
    }

    @Override
    protected boolean hasDefaultCreativeModeTab() {
        return true;
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
        configHandler.addConfigurable(new ItemAbilityTotemConfigForge());
        configHandler.addConfigurable(new ItemAbilityBottleConfigForge());

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

    public void onPlayerClone(PlayerEvent.Clone event) {
        getAbilityHelpers().onPlayerClone(event.getOriginal(), event.getEntity());
    }

    public void onLivingUpdate(TickEvent.PlayerTickEvent event) {
        getAbilityHelpers().onEntityTick(event.player);
    }

    private void onDatapackRegistryCreate(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(AbilityTypes.REGISTRY_KEY, AbilityTypes.DIRECT_CODEC, AbilityTypes.DIRECT_CODEC);
    }

    private void onRegistriesCreate(NewRegistryEvent event) {
         event.create(new RegistryBuilder<MapCodec<? extends IAbilityType>>()
                .setName(AbilityTypeSerializers.REGISTRY_KEY.location())
                .disableSaving()
                .disableSync(),
                registry -> {
                    EverlastingAbilitiesForge.REGISTRY_ABILITY_SERIALIZERS = registry;
                    AbilityTypeSerializers.NAME_CODEC = registry.getCodec();
                });
    }
}
