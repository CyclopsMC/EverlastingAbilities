package org.cyclops.everlastingabilities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.EntityDamageSource;
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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.helper.EntityHelpers;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.init.ItemGroupMod;
import org.cyclops.cyclopscore.init.ModBaseVersionable;
import org.cyclops.cyclopscore.modcompat.capabilities.SimpleCapabilityConstructor;
import org.cyclops.cyclopscore.proxy.IClientProxy;
import org.cyclops.cyclopscore.proxy.ICommonProxy;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.ability.config.*;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.AbilityStoreCapabilityProvider;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.AbilityStoreConfig;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.command.CommandModifyAbilities;
import org.cyclops.everlastingabilities.command.argument.ArgumentTypeAbility;
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
import java.util.Random;

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

        // Register argument types
        ArgumentTypes.register(Reference.MOD_ID + ":" + "ability",
                ArgumentTypeAbility.class,
                new EmptyArgumentSerializer<>(ArgumentTypeAbility::new));

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
                            Random rand = new Random();
                            rand.setSeed(entity.getId());
                            List<IAbilityType> abilityTypes = AbilityHelpers.getAbilityTypesMobSpawn();
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
        ResourceLocation mobName = ForgeRegistries.ENTITIES.getKey(mob.getType());
        return mobName != null && GeneralConfig.mobDropBlacklist.stream().noneMatch(mobName.toString()::matches);
    }

    @Override
    public CreativeModeTab constructDefaultCreativeModeTab() {
        return new ItemGroupMod(this, () -> RegistryEntries.ITEM_ABILITY_BOTTLE);
    }

    @Override
    protected void onConfigsRegister(ConfigHandler configHandler) {
        super.onConfigsRegister(configHandler);
        configHandler.addConfigurable(new GeneralConfig());

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

        // Abilities
        configHandler.addConfigurable(new AbilitySpeedConfig());
        configHandler.addConfigurable(new AbilityHasteConfig());
        configHandler.addConfigurable(new AbilityStrengthConfig());
        configHandler.addConfigurable(new AbilityJumpBoostConfig());
        configHandler.addConfigurable(new AbilityRegenerationConfig());
        configHandler.addConfigurable(new AbilityResistanceConfig());
        configHandler.addConfigurable(new AbilityFireResistanceConfig());
        configHandler.addConfigurable(new AbilityWaterBreathingConfig());
        configHandler.addConfigurable(new AbilityInvisibilityConfig());
        configHandler.addConfigurable(new AbilityNightVisionConfig());
        configHandler.addConfigurable(new AbilityAbsorbtionConfig());
        configHandler.addConfigurable(new AbilitySaturationConfig());
        configHandler.addConfigurable(new AbilityLuckConfig());
        configHandler.addConfigurable(new AbilitySlownessConfig());
        configHandler.addConfigurable(new AbilityMiningFatigueConfig());
        configHandler.addConfigurable(new AbilityNauseaConfig());
        configHandler.addConfigurable(new AbilityBlindnessConfig());
        configHandler.addConfigurable(new AbilityHungerConfig());
        configHandler.addConfigurable(new AbilityWeaknessConfig());
        configHandler.addConfigurable(new AbilityPoisonConfig());
        configHandler.addConfigurable(new AbilityWitherConfig());
        configHandler.addConfigurable(new AbilityGlowingConfig());
        configHandler.addConfigurable(new AbilityLevitationConfig());
        configHandler.addConfigurable(new AbilityUnluckConfig());
        configHandler.addConfigurable(new AbilityFlightConfig());
        configHandler.addConfigurable(new AbilityStepAssistConfig());
        configHandler.addConfigurable(new AbilityFertilityConfig());
        configHandler.addConfigurable(new AbilityBonemealerConfig());
        configHandler.addConfigurable(new AbilityPowerStareConfig());
        configHandler.addConfigurable(new AbilityMagnetizeConfig());
        configHandler.addConfigurable(new AbilityBadOmenConfig());
        configHandler.addConfigurable(new AbilitySlowFallingConfig());
        configHandler.addConfigurable(new AbilityConduitPowerConfig());
        configHandler.addConfigurable(new AbilityDolphinsGraceConfig());
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
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide && event.getEntity().getCapability(MutableAbilityStoreConfig.CAPABILITY).isPresent()) {
            getPacketHandler().sendToServer(new RequestAbilityStorePacket(event.getEntity().getUUID().toString()));
        }
    }

    private static final String NBT_TOTEM_SPAWNED = Reference.MOD_ID + ":totemSpawned";
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (GeneralConfig.totemMaximumSpawnRarity >= 0) {
            CompoundTag tag = event.getPlayer().getPersistentData();
            if (!tag.contains(Player.PERSISTED_NBT_TAG)) {
                tag.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
            }
            CompoundTag playerTag = tag.getCompound(Player.PERSISTED_NBT_TAG);
            if (!playerTag.contains(NBT_TOTEM_SPAWNED)) {
                playerTag.putBoolean(NBT_TOTEM_SPAWNED, true);

                Level world = event.getPlayer().level;
                Player player = event.getPlayer();
                Rarity rarity = Rarity.values()[GeneralConfig.totemMaximumSpawnRarity];
                AbilityHelpers.getRandomAbilityUntilRarity(AbilityHelpers.getAbilityTypesPlayerSpawn(), world.random, rarity, true).ifPresent(abilityType -> {
                    ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
                    itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                            .ifPresent(mutableAbilityStore -> mutableAbilityStore.addAbility(new Ability(abilityType, 1), true));

                    ItemStackHelpers.spawnItemStackToPlayer(world, player.blockPosition(), itemStack, player);
                    EntityHelpers.spawnXpAtPlayer(world, player, abilityType.getBaseXpPerLevel());
                });
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        boolean doMobLoot = event.getEntityLiving().level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        if (!event.getEntityLiving().level.isClientSide
                && (event.getEntityLiving() instanceof Player
                    ? (GeneralConfig.dropAbilitiesOnPlayerDeath > 0
                        && (GeneralConfig.alwaysDropAbilities || (event.getSource() instanceof EntityDamageSource)
                        && event.getSource().getEntity() instanceof Player))
                    : (doMobLoot && (event.getSource() instanceof EntityDamageSource)
                        && event.getSource().getEntity() instanceof Player))) {
            LivingEntity entity = event.getEntityLiving();
            entity.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(mutableAbilityStore -> {
                int toDrop = 1;
                if (event.getEntityLiving() instanceof Player
                        && (GeneralConfig.alwaysDropAbilities || (event.getSource() instanceof EntityDamageSource)
                        && event.getSource().getEntity() instanceof Player)) {
                    toDrop = GeneralConfig.dropAbilitiesOnPlayerDeath;
                }

                ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_TOTEM);
                IMutableAbilityStore itemStackStore = itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).orElse(null);;

                Collection<Ability> abilities = Lists.newArrayList(mutableAbilityStore.getAbilities());
                for (Ability ability : abilities) {
                    if (toDrop > 0) {
                        Ability toRemove = new Ability(ability.getAbilityType(), toDrop);
                        Ability removed = mutableAbilityStore.removeAbility(toRemove, true);
                        if (removed != null) {
                            toDrop -= removed.getLevel();
                            itemStackStore.addAbility(removed, true);
                            entity.sendMessage(new TranslatableComponent("chat.everlastingabilities.playerLostAbility",
                                    entity.getName(),
                                    new TranslatableComponent(removed.getAbilityType().getTranslationKey())
                                            .setStyle(Style.EMPTY
                                                    .withColor(TextColor.fromLegacyFormat(removed.getAbilityType().getRarity().color))
                                                    .withBold(true)),
                                    removed.getLevel()), Util.NIL_UUID);
                        }
                    }
                }

                if (!itemStackStore.getAbilities().isEmpty()) {
                    ItemStackHelpers.spawnItemStack(entity.level, entity.blockPosition(), itemStack);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps(); // This is needed to enable capability retrieval
        IMutableAbilityStore oldStore = event.getOriginal().getCapability(MutableAbilityStoreConfig.CAPABILITY, null).orElse(null);
        IMutableAbilityStore newStore = event.getPlayer().getCapability(MutableAbilityStoreConfig.CAPABILITY, null).orElse(null);
        if (oldStore != null && newStore != null) {
            newStore.setAbilities(Maps.newHashMap(oldStore.getAbilitiesRaw()));
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (GeneralConfig.tickAbilities && event.getEntityLiving() instanceof Player) {
            Player player = (Player) event.getEntityLiving();
            player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(abilityStore -> {
                for (Ability ability : abilityStore.getAbilities()) {
                    if (event.getEntity().level.getGameTime() % 20 == 0 && GeneralConfig.exhaustionPerAbilityTick > 0) {
                        player.causeFoodExhaustion((float) GeneralConfig.exhaustionPerAbilityTick);
                    }
                    ability.getAbilityType().onTick(player, ability.getLevel());
                }
            });
        }
    }

}
