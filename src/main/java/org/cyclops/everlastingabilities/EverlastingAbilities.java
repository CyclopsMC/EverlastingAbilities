package org.cyclops.everlastingabilities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
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
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.helper.EntityHelpers;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.init.ItemGroupMod;
import org.cyclops.cyclopscore.init.ModBaseVersionable;
import org.cyclops.cyclopscore.modcompat.capabilities.SerializableCapabilityProvider;
import org.cyclops.cyclopscore.modcompat.capabilities.SimpleCapabilityConstructor;
import org.cyclops.cyclopscore.proxy.IClientProxy;
import org.cyclops.cyclopscore.proxy.ICommonProxy;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.ability.config.*;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
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
    protected LiteralArgumentBuilder<CommandSource> constructBaseCommand() {
        LiteralArgumentBuilder<CommandSource> root = super.constructBaseCommand();

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
        LootFunctionManager.registerFunction(new LootFunctionSetRandomAbility.Serializer());

        // Register argument types
        ArgumentTypes.register(Reference.MOD_ID + ":" + "ability",
                ArgumentTypeAbility.class,
                new ArgumentSerializer<>(ArgumentTypeAbility::new));

        // Register capabilities
        getCapabilityConstructorRegistry().registerInheritableEntity(PlayerEntity.class, new SimpleCapabilityConstructor<IMutableAbilityStore, PlayerEntity>() {
            @Nullable
            @Override
            public ICapabilityProvider createProvider(PlayerEntity host) {
                return new SerializableCapabilityProvider<>(this, new DefaultMutableAbilityStore());
            }

            @Override
            public Capability<IMutableAbilityStore> getCapability() {
                return MutableAbilityStoreConfig.CAPABILITY;
            }
        });
        getCapabilityConstructorRegistry().registerInheritableEntity(CreatureEntity.class, new SimpleCapabilityConstructor<IMutableAbilityStore, CreatureEntity>() { // TODO: AnimalEntity was IAnimal
            @Nullable
            @Override
            public ICapabilityProvider createProvider(CreatureEntity host) { // TODO: CreatureEntity was IAnimal
                if (host instanceof Entity) {
                    Entity entity = (Entity) host;
                    IMutableAbilityStore store = new DefaultMutableAbilityStore();
                    if (!entity.getEntityWorld().isRemote && host instanceof LivingEntity) {
                        if (GeneralConfig.mobAbilityChance > 0
                                && entity.getEntityId() % GeneralConfig.mobAbilityChance == 0
                                && canMobHaveAbility((LivingEntity) host)) {
                            Random rand = new Random();
                            rand.setSeed(entity.getEntityId());
                            List<IAbilityType> abilityTypes = AbilityHelpers.getAbilityTypesMobSpawn();
                            Rarity rarity = AbilityHelpers.getRandomRarity(abilityTypes, rand);
                            AbilityHelpers.getRandomAbility(abilityTypes, rand, rarity).ifPresent(
                                    abilityType -> store.addAbility(new Ability(abilityType, 1), true));
                        }
                    }
                    return new SerializableCapabilityProvider<>(this, store);
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
    public ItemGroup constructDefaultItemGroup() {
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
    }

    /**
     * Log a new info message for this mod.
     * @param message The message to show.
     */
    public static void clog(String message) {
        clog(Level.INFO, message);
    }
    
    /**
     * Log a new message of the given level for this mod.
     * @param level The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void clog(Level level, String message) {
        EverlastingAbilities._instance.getLoggerHelper().log(level, message);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote && event.getEntity().getCapability(MutableAbilityStoreConfig.CAPABILITY).isPresent()) {
            getPacketHandler().sendToServer(new RequestAbilityStorePacket(event.getEntity().getUniqueID().toString()));
        }
    }

    private static final String NBT_TOTEM_SPAWNED = Reference.MOD_ID + ":totemSpawned";
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (GeneralConfig.totemMaximumSpawnRarity >= 0) {
            CompoundNBT tag = event.getPlayer().getPersistantData();
            if (!tag.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
                tag.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
            }
            CompoundNBT playerTag = tag.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
            if (!playerTag.contains(NBT_TOTEM_SPAWNED)) {
                playerTag.putBoolean(NBT_TOTEM_SPAWNED, true);

                World world = event.getPlayer().world;
                PlayerEntity player = event.getPlayer();
                Rarity rarity = Rarity.values()[GeneralConfig.totemMaximumSpawnRarity];
                AbilityHelpers.getRandomAbilityUntilRarity(AbilityHelpers.getAbilityTypesPlayerSpawn(), world.rand, rarity, true).ifPresent(abilityType -> {
                    ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
                    itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                            .ifPresent(mutableAbilityStore -> mutableAbilityStore.addAbility(new Ability(abilityType, 1), true));

                    ItemStackHelpers.spawnItemStackToPlayer(world, player.getPosition(), itemStack, player);
                    EntityHelpers.spawnXpAtPlayer(world, player, abilityType.getBaseXpPerLevel());
                });
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        boolean doMobLoot = event.getEntityLiving().world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
        if (!event.getEntityLiving().world.isRemote
                && (event.getEntityLiving() instanceof PlayerEntity
                    ? (GeneralConfig.dropAbilitiesOnPlayerDeath > 0
                        && (GeneralConfig.alwaysDropAbilities || (event.getSource() instanceof EntityDamageSource)
                        && event.getSource().getTrueSource() instanceof PlayerEntity))
                    : (doMobLoot && (event.getSource() instanceof EntityDamageSource)
                        && event.getSource().getTrueSource() instanceof PlayerEntity))) {
            LivingEntity entity = event.getEntityLiving();
            entity.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(mutableAbilityStore -> {
                int toDrop = 1;
                if (event.getEntityLiving() instanceof PlayerEntity
                        && (GeneralConfig.alwaysDropAbilities || (event.getSource() instanceof EntityDamageSource)
                        && event.getSource().getTrueSource() instanceof PlayerEntity)) {
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
                            entity.sendMessage(new TranslationTextComponent("chat.everlastingabilities.playerLostAbility",
                                    entity.getName(),
                                    new TranslationTextComponent(removed.getAbilityType().getTranslationKey())
                                            .setStyle(new Style()
                                                    .setColor(removed.getAbilityType().getRarity().color)
                                                    .setBold(true)),
                                    removed.getLevel()));
                        }
                    }
                }

                if (itemStack != null && !itemStackStore.getAbilities().isEmpty()) {
                    ItemStackHelpers.spawnItemStack(entity.world, entity.getPosition(), itemStack);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        IMutableAbilityStore oldStore = event.getOriginal().getCapability(MutableAbilityStoreConfig.CAPABILITY, null).orElse(null);
        IMutableAbilityStore newStore = event.getPlayer().getCapability(MutableAbilityStoreConfig.CAPABILITY, null).orElse(null);
        newStore.setAbilities(Maps.newHashMap(oldStore.getAbilitiesRaw()));
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(abilityStore -> {
                for (Ability ability : abilityStore.getAbilities()) {
                    if (event.getEntity().world.getGameTime() % 20 == 0 && GeneralConfig.exhaustionPerAbilityTick > 0) {
                        player.addExhaustion((float) GeneralConfig.exhaustionPerAbilityTick);
                    }
                    ability.getAbilityType().onTick(player, ability.getLevel());
                }
            });
        }
    }
    
}
