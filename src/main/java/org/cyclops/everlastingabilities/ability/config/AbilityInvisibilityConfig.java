package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.Rarity;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.config.ModConfig;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectSelf;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityInvisibilityConfig extends AbilityConfig<AbilityTypePotionEffectSelf> {

    @ConfigurableProperty(category = "ability", comment = "Rarity of this ability.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int rarity = Rarity.UNCOMMON.ordinal();

    @ConfigurableProperty(category = "ability", comment = "The maximum ability level.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int maxLevel = 1;

    @ConfigurableProperty(category = "ability", comment = "The xp required per level.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int xpPerLevel = 20;

    public AbilityInvisibilityConfig() {
        super("invisibility",
                eConfig -> new AbilityTypePotionEffectSelf(eConfig.getNamedId(), rarity, maxLevel, xpPerLevel, Effects.INVISIBILITY));
    }

}
