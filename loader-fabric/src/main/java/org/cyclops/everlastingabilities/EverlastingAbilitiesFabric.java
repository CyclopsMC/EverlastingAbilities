package org.cyclops.everlastingabilities;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.cyclops.cyclopscore.config.ConfigHandlerCommon;
import org.cyclops.cyclopscore.events.IEntityJoinLevelEvent;
import org.cyclops.cyclopscore.events.IEntityTickEvent;
import org.cyclops.cyclopscore.events.IPlayerLoggedInEvent;
import org.cyclops.cyclopscore.init.ModBaseFabric;
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
import org.cyclops.everlastingabilities.attachment.Attachments;
import org.cyclops.everlastingabilities.command.CommandModifyAbilities;
import org.cyclops.everlastingabilities.command.argument.ArgumentTypeAbilityConfig;
import org.cyclops.everlastingabilities.component.DataComponentAbilityStoreConfig;
import org.cyclops.everlastingabilities.helper.AbilityHelpersFabric;
import org.cyclops.everlastingabilities.helper.IAbilityHelpers;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainerConfig;
import org.cyclops.everlastingabilities.item.ItemAbilityBottleConfigFabric;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfigFabric;
import org.cyclops.everlastingabilities.network.packet.RequestAbilityStorePacket;
import org.cyclops.everlastingabilities.proxy.ClientProxyFabric;
import org.cyclops.everlastingabilities.proxy.CommonProxyFabric;
import org.cyclops.everlastingabilities.recipe.TotemRecycleRecipeConfig;

/**
 * The main mod class of CapabilityProxy.
 * @author rubensworks
 */
public class EverlastingAbilitiesFabric extends ModBaseFabric<EverlastingAbilitiesFabric> implements ModInitializer, IEverlastingAbilitiesModBase {

    /**
     * The unique instance of this mod.
     */
    public static EverlastingAbilitiesFabric _instance;
    private final IAbilityHelpers abilityHelpers;

    public EverlastingAbilitiesFabric() {
        super(Reference.MOD_ID, (instance) -> {
            _instance = instance;
            EverlastingAbilitiesInstance.MOD = instance;
        });
        this.abilityHelpers = new AbilityHelpersFabric(getModHelpers());

        IEntityJoinLevelEvent.EVENT.register(this::onEntityJoinWorld);
        IPlayerLoggedInEvent.EVENT.register(this::onPlayerLoggedIn);
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onLivingDeath);
        ServerPlayerEvents.COPY_FROM.register(this::onPlayerClone);
        IEntityTickEvent.EVENT.register(this::onLivingUpdate);
    }

    @Override
    public void onInitialize() {
        // Setup registries
        AbilityTypeSerializers.REGISTRY = FabricRegistryBuilder
                .createSimple(AbilityTypeSerializers.REGISTRY_KEY)
                .buildAndRegister();
        AbilityTypeSerializers.NAME_CODEC = AbilityTypeSerializers.REGISTRY.byNameCodec();
        DynamicRegistries.registerSynced(AbilityTypes.REGISTRY_KEY, AbilityTypes.DIRECT_CODEC, AbilityTypes.DIRECT_CODEC);

        super.onInitialize();

        // Register attachments
        Attachments.register();
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
    protected IClientProxyCommon constructClientProxy() {
        return new ClientProxyFabric();
    }

    @Override
    protected ICommonProxyCommon constructCommonProxy() {
        return new CommonProxyFabric();
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
        configHandler.addConfigurable(new ItemAbilityTotemConfigFabric());
        configHandler.addConfigurable(new ItemAbilityBottleConfigFabric());

        // Ability serializers
        configHandler.addConfigurable(new AbilityTypeEffectSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeAttributeModifierSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialBonemealerSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialFertilitySerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialFlightSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialMagnetizeSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialPowerStareSerializerConfig<>(this));
        configHandler.addConfigurable(new AbilityTypeSpecialStepAssistSerializerConfig<>(this));

        // Data components
        configHandler.addConfigurable(new DataComponentAbilityStoreConfig<>(this));
    }

    public void onEntityJoinWorld(Entity entity, Level level) {
        if (level.isClientSide && getAbilityHelpers().getEntityAbilityStore(entity).isPresent()) {
            getPacketHandlerCommon().sendToServer(new RequestAbilityStorePacket(entity.getUUID().toString()));
        }
    }

    public void onPlayerLoggedIn(ServerPlayer player) {
        getAbilityHelpers().initializePlayerAbilitiesOnSpawn(player);
    }

    public void onLivingDeath(LivingEntity entity, DamageSource damageSource) {
        getAbilityHelpers().onEntityDeath(entity, damageSource);
    }

    public void onPlayerClone(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        getAbilityHelpers().onPlayerClone(oldPlayer, newPlayer);
    }

    public void onLivingUpdate(Entity entity) {
        getAbilityHelpers().onEntityTick(entity);
    }
}
