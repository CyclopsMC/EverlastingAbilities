package org.cyclops.everlastingabilities;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.config.ModConfig;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.extendedconfig.DummyConfig;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.tracking.Analytics;
import org.cyclops.cyclopscore.tracking.Versions;

import java.util.List;

/**
 * A config with general options for this mod.
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    @ConfigurableProperty(category = "core", comment = "Set 'true' to enable development debug mode. This will result in a lower performance!", requiresMcRestart = true)
    public static boolean debug = false;

    @ConfigurableProperty(category = "core", comment = "If the recipe loader should crash when finding invalid recipes.", requiresMcRestart = true)
    public static boolean crashOnInvalidRecipe = false;

    @ConfigurableProperty(category = "core", comment = "If mod compatibility loader should crash hard if errors occur in that process.", requiresMcRestart = true)
    public static boolean crashOnModCompatCrash = false;

    @ConfigurableProperty(category = "core", comment = "If an anonymous mod startup analytics request may be sent to our analytics service.")
    public static boolean analytics = true;

    @ConfigurableProperty(category = "core", comment = "If the version checker should be enabled.")
    public static boolean versionChecker = true;

    @ConfigurableProperty(category = "general", comment = "The maximum rarity of totems to spawn when a player first logs in. [0-3], -1 disables totem spawning.", configLocation = ModConfig.Type.SERVER)
    public static int totemMaximumSpawnRarity = 1;

    @ConfigurableProperty(category = "general", comment = "How many abilities should be dropped on player death.", configLocation = ModConfig.Type.SERVER, isCommandable = true)
    public static int dropAbilitiesOnPlayerDeath = 1;

    @ConfigurableProperty(category = "general", comment = "true: Abilities drop when players die; false: Abilities drop when players die by the hand of other players.", configLocation = ModConfig.Type.SERVER, isCommandable = true)
    public static boolean alwaysDropAbilities = false;

    @ConfigurableProperty(category = "general", comment = "1/x chance for mobs to have abilities.", configLocation = ModConfig.Type.SERVER, isCommandable = true)
    public static int mobAbilityChance = 50;

    @ConfigurableProperty(category = "general", comment = "The multiplier for ability XP requirement", configLocation = ModConfig.Type.SERVER, isCommandable = true)
    public static int abilityXpMultiplier = 10;

    @ConfigurableProperty(category = "general", comment = "If players should have particle effects for the abilities they carry.", configLocation = ModConfig.Type.CLIENT, isCommandable = true)
    public static boolean showPlayerParticles = false;

    @ConfigurableProperty(category = "general", comment = "If on-player entities should have particle effects for the abilities they carry.", configLocation = ModConfig.Type.CLIENT, isCommandable = true)
    public static boolean showEntityParticles = true;

    @ConfigurableProperty(category = "general", comment = "The maximum amount of abilities a player can have, -1 is infinite.", configLocation = ModConfig.Type.SERVER, isCommandable = true)
    public static int maxPlayerAbilities = -1;

    @ConfigurableProperty(category = "general", comment = "The amount of exhaustion that should by applied to the player per active ability per second.", configLocation = ModConfig.Type.SERVER, isCommandable = true)
    public static double exhaustionPerAbilityTick = 0.01;

    @ConfigurableProperty(category = "general", comment = "These mobs will not be affected by hostile area potion effects such as poison or weakness. (Java regular expressions are allowed)", configLocation = ModConfig.Type.SERVER)
    public static List<String> friendlyMobs = Lists.newArrayList("minecraft:armor_stand",
            "minecraft:bat",
            "minecraft:bee",
            "minecraft:boat",
            "minecraft:cat",
            "minecraft:chicken",
            "minecraft:cod",
            "minecraft:cow",
            "minecraft:donkey",
            "minecraft:dolphin",
            "minecraft:fox",
            "minecraft:horse",
            "minecraft:llama",
            "minecraft:mule",
            "minecraft:mooshroom",
            "minecraft:ocelot",
            "minecraft:panda",
            "minecraft:parrot",
            "minecraft:pig",
            "minecraft:pufferfish",
            "minecraft:polarbear",
            "minecraft:rabbit",
            "minecraft:salmon",
            "minecraft:sheep",
            "minecraft:squid",
            "minecraft:trader_llama",
            "minecraft:tropical_fish",
            "minecraft:turtle",
            "minecraft:egg",
            "minecraft:bee",
            "minecraft:villager",
            "minecraft:iron_golem",
            "minecraft:wandering_trader",
            "minecraft:wolf",
            "minecolonies:.*"
    );

    @ConfigurableProperty(category = "general", comment = "Mobs that won't have abilities that can be dropped via totems. You can add things like 'minecraft:pig'.", configLocation = ModConfig.Type.SERVER)
    public static List<String> mobDropBlacklist = Lists.newArrayList(
            // Empty by default
    );

    @ConfigurableProperty(category = "general", comment = "If potion abilities should show particle effects. This will also make them appear as in-game symbols.", configLocation = ModConfig.Type.CLIENT, isCommandable = true)
    public static boolean showPotionEffectParticles = false;

    @ConfigurableProperty(category = "general", comment = "If ability effects should apply. Disable this if you don't want any abilities to work. Useful for debugging.", configLocation = ModConfig.Type.SERVER, isCommandable = true)
    public static boolean tickAbilities = true;

    /**
     * Create a new instance.
     */
    public GeneralConfig() {
        super(EverlastingAbilities._instance, "general");
    }

    @Override
    public void onRegistered() {
        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_INVALID_RECIPE, GeneralConfig.crashOnInvalidRecipe);
        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_MODCOMPAT_CRASH, GeneralConfig.crashOnModCompatCrash);

        if(analytics) {
            Analytics.registerMod(getMod(), Reference.GA_TRACKING_ID);
        }
        if(versionChecker) {
            Versions.registerMod(getMod(), EverlastingAbilities._instance, Reference.VERSION_URL);
        }
    }

}
