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
public class AbilityNightVisionConfig extends AbilityConfig<AbilityTypePotionEffectSelf> {

    @ConfigurableProperty(category = "ability", comment = "Rarity of this ability.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int rarity = Rarity.UNCOMMON.ordinal();

    @ConfigurableProperty(category = "ability", comment = "The maximum ability level.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int maxLevel = 1;

    @ConfigurableProperty(category = "ability", comment = "The xp required per level.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int xpPerLevel = 15;

    public AbilityNightVisionConfig() {
        super("night_vision",
                eConfig -> new AbilityTypePotionEffectSelf(eConfig.getNamedId(), rarity, maxLevel, xpPerLevel, Effects.NIGHT_VISION) {
                    @Override
                    protected int getDuration(int tickModulus, int level) {
                        return tickModulus * 27;
                    }
                });
    }

}
