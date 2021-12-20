package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.fml.config.ModConfig;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectSelf;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityAbsorbtionConfig extends AbilityConfig<AbilityTypePotionEffectSelf> {

    @ConfigurableProperty(category = "ability", comment = "Rarity of this ability.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static int rarity = Rarity.RARE.ordinal();

    @ConfigurableProperty(category = "ability", comment = "The maximum ability level.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static int maxLevel = 5;

    @ConfigurableProperty(category = "ability", comment = "The xp required per level.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static int xpPerLevel = 75;

    @ConfigurableProperty(category = "ability", comment = "If this can be obtained by initially spawning players.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean obtainableOnPlayerSpawn = true;

    @ConfigurableProperty(category = "ability", comment = "If this can be obtained by spawning mobs.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean obtainableOnMobSpawn = true;

    @ConfigurableProperty(category = "ability", comment = "If this can be obtained by combining totems in a crafting grid.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean obtainableOnCraft = true;

    @ConfigurableProperty(category = "ability", comment = "If this can be obtained in loot chests.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean obtainableOnLoot = true;

    public AbilityAbsorbtionConfig() {
        super("absorbtion",
                eConfig -> new AbilityTypePotionEffectSelf(eConfig.getNamedId(), () -> rarity, () -> maxLevel, () -> xpPerLevel,
                        () -> obtainableOnPlayerSpawn, () -> obtainableOnMobSpawn, () -> obtainableOnCraft, () -> obtainableOnLoot, MobEffects.ABSORPTION) {
                    @Override
                    protected int getDuration(int tickModulus, int level) {
                        int maxLevel = getMaxLevel() == -1 ? 5 : getMaxLevel();
                        return (int) (MinecraftHelpers.SECOND_IN_TICKS * ((float) level / maxLevel * 20F));
                    }

                    @Override
                    protected int getTickModulus(int level) {
                        return MinecraftHelpers.SECOND_IN_TICKS * 10;
                    }

                    @Override
                    protected int getAmplifier(int level) {
                        return super.getAmplifier(level) / 2;
                    }
                });
    }

}
