package org.cyclops.everlastingabilities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.helper.EntityHelpers;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.init.ModBaseVersionable;
import org.cyclops.cyclopscore.modcompat.capabilities.SimpleCapabilityConstructor;
import org.cyclops.cyclopscore.proxy.IClientProxy;
import org.cyclops.cyclopscore.proxy.ICommonProxy;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeEffectSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialBonemealerSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialFertilitySerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialFlightSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialMagnetizeSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialPowerStareSerializerConfig;
import org.cyclops.everlastingabilities.ability.serializer.AbilityTypeSpecialStepAssistSerializerConfig;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.AbilityStoreCapabilityProvider;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStoreRegistryAccess;
import org.cyclops.everlastingabilities.capability.AbilityStoreConfig;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.command.CommandModifyAbilities;
import org.cyclops.everlastingabilities.command.argument.ArgumentTypeAbilityConfig;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainerConfig;
import org.cyclops.everlastingabilities.item.ItemAbilityBottleConfig;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfig;
import org.cyclops.everlastingabilities.loot.functions.LootFunctionSetRandomAbility;
import org.cyclops.everlastingabilities.network.packet.RequestAbilityStorePacket;
import org.cyclops.everlastingabilities.proxy.ClientProxy;
import org.cyclops.everlastingabilities.proxy.CommonProxy;
import org.cyclops.everlastingabilities.recipe.TotemRecycleRecipeConfig;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * The main mod class of this mod.
 * @author rubensworks (aka kroeserr)
 *
 */
@Mod(Reference.MOD_ID)
public class EverlastingAbilities extends ModBaseVersionable<EverlastingAbilities> {

    /**
     * The unique instance of this mod.
     */
    public static EverlastingAbilities _instance;

    public EverlastingAbilities() {
        super(Reference.MOD_ID, (instance) -> _instance = instance);

        AbilityTypes.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> constructBaseCommand() {
        LiteralArgumentBuilder<CommandSourceStack> root = super.constructBaseCommand();

        root.then(CommandModifyAbilities.make());

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
    protected void setup(FMLCommonSetupEvent event) {
        super.setup(event);

        // Register loot functions
        LootFunctionSetRandomAbility.load();

        // Register capabilities
        getCapabilityConstructorRegistry().registerInheritableEntity(Player.class, new SimpleCapabilityConstructor<IMutableAbilityStore, Player>() {
            @Nullable
            @Override
            public ICapabilityProvider createProvider(Player host) {
                return new AbilityStoreCapabilityProvider<>(this, new DefaultMutableAbilityStore());
            }

            @Override
            public Capability<IMutableAbilityStore> getCapability() {
                return MutableAbilityStoreConfig.CAPABILITY;
            }
        });
        getCapabilityConstructorRegistry().registerInheritableEntity(PathfinderMob.class, new SimpleCapabilityConstructor<IMutableAbilityStore, PathfinderMob>() { // TODO: AnimalEntity was IAnimal
            @Nullable
            @Override
            public ICapabilityProvider createProvider(PathfinderMob host) { // TODO: CreatureEntity was IAnimal
                if (host instanceof Entity) {
                    Entity entity = (Entity) host;
                    IMutableAbilityStore store = new DefaultMutableAbilityStore();
                    if (!entity.getCommandSenderWorld().isClientSide && host instanceof LivingEntity) {
                        if (GeneralConfig.mobAbilityChance > 0
                                && entity.getId() % GeneralConfig.mobAbilityChance == 0
                                && canMobHaveAbility((LivingEntity) host)) {
                            RandomSource rand = RandomSource.create();
                            rand.setSeed(entity.getId());
                            Registry<IAbilityType> registry = AbilityHelpers.getRegistry(host.level().registryAccess());
                            List<IAbilityType> abilityTypes = AbilityHelpers.getAbilityTypesMobSpawn(registry);
                            Rarity rarity = AbilityHelpers.getRandomRarity(abilityTypes, rand);
                            AbilityHelpers.getRandomAbility(abilityTypes, rand, rarity).ifPresent(
                                    abilityType -> store.addAbility(new Ability(abilityType, 1), true));
                        }
                    }
                    return new AbilityStoreCapabilityProvider<>(this, store);
                }
                return null;
            }

            @Override
            public Capability<IMutableAbilityStore> getCapability() {
                return MutableAbilityStoreConfig.CAPABILITY;
            }
        });
    }

    private static boolean canMobHaveAbility(LivingEntity mob) {
        ResourceLocation mobName = ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
        return mobName != null && GeneralConfig.mobDropBlacklist.stream().noneMatch(mobName.toString()::matches);
    }

    @Override
    protected CreativeModeTab.Builder constructDefaultCreativeModeTab(CreativeModeTab.Builder builder) {
        return super.constructDefaultCreativeModeTab(builder)
                .icon(() -> new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE));
    }

    @Override
    protected void onConfigsRegister(ConfigHandler configHandler) {
        super.onConfigsRegister(configHandler);
        configHandler.addConfigurable(new GeneralConfig());

        // Argument types
        configHandler.addConfigurable(new ArgumentTypeAbilityConfig());

        // Capabilities
        configHandler.addConfigurable(new AbilityStoreConfig());
        configHandler.addConfigurable(new MutableAbilityStoreConfig());

        // Guis
        configHandler.addConfigurable(new ContainerAbilityContainerConfig());

        // Recipes
        configHandler.addConfigurable(new TotemRecycleRecipeConfig());

        // Items
        configHandler.addConfigurable(new ItemAbilityTotemConfig());
        configHandler.addConfigurable(new ItemAbilityBottleConfig());

        // Ability serializers
        configHandler.addConfigurable(new AbilityTypeEffectSerializerConfig());
        configHandler.addConfigurable(new AbilityTypeSpecialBonemealerSerializerConfig());
        configHandler.addConfigurable(new AbilityTypeSpecialFertilitySerializerConfig());
        configHandler.addConfigurable(new AbilityTypeSpecialFlightSerializerConfig());
        configHandler.addConfigurable(new AbilityTypeSpecialMagnetizeSerializerConfig());
        configHandler.addConfigurable(new AbilityTypeSpecialPowerStareSerializerConfig());
        configHandler.addConfigurable(new AbilityTypeSpecialStepAssistSerializerConfig());
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

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide && event.getEntity().getCapability(MutableAbilityStoreConfig.CAPABILITY).isPresent()) {
            getPacketHandler().sendToServer(new RequestAbilityStorePacket(event.getEntity().getUUID().toString()));
        }
    }

    private static final String NBT_TOTEM_SPAWNED = Reference.MOD_ID + ":totemSpawned";
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (GeneralConfig.totemMaximumSpawnRarity >= 0) {
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
                AbilityHelpers.getRandomAbilityUntilRarity(AbilityHelpers.getAbilityTypesPlayerSpawn(AbilityHelpers.getRegistry(world.registryAccess())), world.random, rarity, true).ifPresent(abilityType -> {
                    ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
                    itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                            .ifPresent(mutableAbilityStore -> {
                                ((IMutableAbilityStoreRegistryAccess) mutableAbilityStore).setRegistryAccess(world.registryAccess());
                                mutableAbilityStore.addAbility(new Ability(abilityType, 1), true);
                            });

                    ItemStackHelpers.spawnItemStackToPlayer(world, player.blockPosition(), itemStack, player);
                    EntityHelpers.spawnXpAtPlayer(world, player, abilityType.getXpPerLevelScaled());
                });
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        boolean doMobLoot = event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        if (!event.getEntity().level().isClientSide
                && (event.getEntity() instanceof Player
                    ? (GeneralConfig.dropAbilitiesOnPlayerDeath > 0
                        && (GeneralConfig.alwaysDropAbilities || event.getSource().getEntity() instanceof Player))
                    : (doMobLoot && event.getSource().getEntity() instanceof Player))) {
            LivingEntity entity = event.getEntity();
            entity.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(mutableAbilityStore -> {
                int toDrop = 1;
                if (event.getEntity() instanceof Player
                        && (GeneralConfig.alwaysDropAbilities || event.getSource().getEntity() instanceof Player)) {
                    toDrop = GeneralConfig.dropAbilitiesOnPlayerDeath;
                }

                ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_TOTEM);
                IMutableAbilityStore itemStackStore = itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).orElse(null);
                ((IMutableAbilityStoreRegistryAccess) itemStackStore).setRegistryAccess(event.getEntity().level().registryAccess());

                Collection<Ability> abilities = Lists.newArrayList(mutableAbilityStore.getAbilities());
                for (Ability ability : abilities) {
                    if (toDrop > 0) {
                        Ability toRemove = new Ability(ability.getAbilityType(), toDrop);
                        Ability removed = mutableAbilityStore.removeAbility(toRemove, true);
                        if (removed != null) {
                            toDrop -= removed.getLevel();
                            itemStackStore.addAbility(removed, true);
                            entity.sendSystemMessage(Component.translatable("chat.everlastingabilities.playerLostAbility",
                                    entity.getName(),
                                    Component.translatable(removed.getAbilityType().getTranslationKey())
                                            .setStyle(Style.EMPTY
                                                    .withColor(TextColor.fromLegacyFormat(removed.getAbilityType().getRarity().color))
                                                    .withBold(true)),
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

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps(); // This is needed to enable capability retrieval
        IMutableAbilityStore oldStore = event.getOriginal().getCapability(MutableAbilityStoreConfig.CAPABILITY, null).orElse(null);
        IMutableAbilityStore newStore = event.getEntity().getCapability(MutableAbilityStoreConfig.CAPABILITY, null).orElse(null);
        if (oldStore != null && newStore != null) {
            newStore.setAbilities(Maps.newHashMap(oldStore.getAbilitiesRaw()));
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (GeneralConfig.tickAbilities && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(abilityStore -> {
                for (Ability ability : abilityStore.getAbilities()) {
                    if (AbilityHelpers.PREDICATE_ABILITY_ENABLED.test(ability.getAbilityType())) {
                        if (event.getEntity().level().getGameTime() % 20 == 0 && GeneralConfig.exhaustionPerAbilityTick > 0) {
                            player.causeFoodExhaustion((float) GeneralConfig.exhaustionPerAbilityTick);
                        }
                        ability.getAbilityType().onTick(player, ability.getLevel());
                    }
                }
            });
        }
    }

}
