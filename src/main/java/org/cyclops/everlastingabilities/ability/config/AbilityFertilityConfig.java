package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.Rarity;
import net.minecraftforge.fml.config.ModConfig;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.everlastingabilities.ability.AbilityTypeFertility;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityFertilityConfig extends AbilityConfig<AbilityTypeFertility> {

    @ConfigurableProperty(category = "ability", comment = "Rarity of this ability.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static int rarity = Rarity.UNCOMMON.ordinal();

    @ConfigurableProperty(category = "ability", comment = "The maximum ability level.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static int maxLevel = 3;

    @ConfigurableProperty(category = "ability", comment = "The xp required per level.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static int xpPerLevel = 30;

    @ConfigurableProperty(category = "ability", comment = "If this can be obtained by initially spawning players.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean obtainableOnPlayerSpawn = true;

    @ConfigurableProperty(category = "ability", comment = "If this can be obtained by spawning mobs.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean obtainableOnMobSpawn = true;

    @ConfigurableProperty(category = "ability", comment = "If this can be obtained by combining totems in a crafting grid.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean obtainableOnCraft = true;

    @ConfigurableProperty(category = "ability", comment = "If this can be obtained in loot chests.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean obtainableOnLoot = true;

    public AbilityFertilityConfig() {
        super("fertility",
                eConfig -> new AbilityTypeFertility(eConfig.getNamedId(), () -> rarity, () -> maxLevel, () -> xpPerLevel,
                        () -> obtainableOnPlayerSpawn, () -> obtainableOnMobSpawn, () -> obtainableOnCraft, () -> obtainableOnLoot));
    }

}
