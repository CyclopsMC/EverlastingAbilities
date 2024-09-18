package org.cyclops.everlastingabilities;

import com.google.common.collect.Lists;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.cyclopscore.config.ModConfigLocation;
import org.cyclops.cyclopscore.config.extendedconfig.DummyConfigCommon;
import org.cyclops.cyclopscore.helper.ModBaseCommon;

import java.util.List;

/**
 * A config with general options for this mod.
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfigCommon<ModBaseCommon<?>> {

    @ConfigurablePropertyCommon(category = "general", comment = "The maximum rarity of totems to spawn when a player first logs in. [0-3], -1 disables totem spawning.", configLocation = ModConfigLocation.SERVER)
    public static int totemMaximumSpawnRarity = 1;

    @ConfigurablePropertyCommon(category = "general", comment = "How many abilities should be dropped on player death.", configLocation = ModConfigLocation.SERVER, isCommandable = true)
    public static int dropAbilitiesOnPlayerDeath = 1;

    @ConfigurablePropertyCommon(category = "general", comment = "true: Abilities drop when players die; false: Abilities drop when players die by the hand of other players.", configLocation = ModConfigLocation.SERVER, isCommandable = true)
    public static boolean alwaysDropAbilities = false;

    @ConfigurablePropertyCommon(category = "general", comment = "1/x chance for mobs to have abilities.", configLocation = ModConfigLocation.SERVER, isCommandable = true)
    public static int mobAbilityChance = 50;

    @ConfigurablePropertyCommon(category = "general", comment = "The multiplier for ability XP requirement", configLocation = ModConfigLocation.SERVER, isCommandable = true)
    public static int abilityXpMultiplier = 10;

    @ConfigurablePropertyCommon(category = "general", comment = "If players should have particle effects for the abilities they carry.", configLocation = ModConfigLocation.CLIENT, isCommandable = true)
    public static boolean showPlayerParticles = false;

    @ConfigurablePropertyCommon(category = "general", comment = "If on-player entities should have particle effects for the abilities they carry.", configLocation = ModConfigLocation.CLIENT, isCommandable = true)
    public static boolean showEntityParticles = true;

    @ConfigurablePropertyCommon(category = "general", comment = "The maximum amount of abilities a player can have, -1 is infinite.", configLocation = ModConfigLocation.SERVER, requiresMcRestart = true)
    public static int maxPlayerAbilities = -1;

    @ConfigurablePropertyCommon(category = "general", comment = "The amount of exhaustion that should by applied to the player per active ability per second.", configLocation = ModConfigLocation.SERVER, isCommandable = true)
    public static double exhaustionPerAbilityTick = 0.01;

    @ConfigurablePropertyCommon(category = "general", comment = "These mobs will not be affected by hostile area potion effects such as poison or weakness. (Java regular expressions are allowed)", configLocation = ModConfigLocation.SERVER)
    public static List<String> friendlyMobs = Lists.newArrayList(
            "minecraft:armor_stand",
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

    @ConfigurablePropertyCommon(category = "general", comment = "Mobs that won't have abilities that can be dropped via totems. You can add things like 'minecraft:pig'.", configLocation = ModConfigLocation.SERVER)
    public static List<String> mobDropBlacklist = Lists.newArrayList(
            // Empty by default
    );

    @ConfigurablePropertyCommon(category = "general", comment = "If potion abilities should show particle effects. This will also make them appear as in-game symbols.", configLocation = ModConfigLocation.CLIENT, isCommandable = true)
    public static boolean showPotionEffectParticles = false;

    @ConfigurablePropertyCommon(category = "general", comment = "If ability effects should apply. Disable this if you don't want any abilities to work. Useful for debugging.", configLocation = ModConfigLocation.SERVER, isCommandable = true)
    public static boolean tickAbilities = true;

    /**
     * Create a new instance.
     */
    public GeneralConfig(ModBaseCommon<?> mod) {
        super(mod, "general");
    }

}
