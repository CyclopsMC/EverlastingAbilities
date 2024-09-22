package org.cyclops.everlastingabilities.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.client.particle.ParticleBlurData;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;

/**
 * @author rubensworks
 */
public class RenderLivingHandler {
    public static void onRenderLiving(LivingEntity entity) {
        if (((GeneralConfig.showEntityParticles && entity instanceof PathfinderMob)
                || (GeneralConfig.showPlayerParticles && entity instanceof Player))
                && !Minecraft.getInstance().isPaused()
                && entity.level().getGameTime() % 10 == 0) {
            IMutableAbilityStore abilityStore = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getEntityAbilityStore(entity).orElse(null);
            if (abilityStore != null) {
                if (!abilityStore.getAbilities().isEmpty()) {
                    Triple<Integer, Integer, Integer> abilityColors = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAverageRarityColor(abilityStore);
                    float r = abilityColors.getLeft() / 255F;
                    float g = abilityColors.getMiddle() / 255F;
                    float b = abilityColors.getRight() / 255F;

                    RandomSource rand = entity.level().random;
                    float scale = 0.5F - rand.nextFloat() * 0.3F;
                    float red = Math.max(0, r - rand.nextFloat() * 0.1F);
                    float green = Math.max(0, g - rand.nextFloat() * 0.1F);
                    float blue = Math.max(0, b - rand.nextFloat() * 0.1F);
                    float ageMultiplier = (float) (rand.nextDouble() * 10D + 20D);

                    double x = entity.getX() - 0.1D + rand.nextDouble() * 0.2D + (entity.getBbWidth() / 2 * (rand.nextBoolean() ? 1 : -1));
                    double y = entity.getY() + entity.getBbHeight() - 0.2D + rand.nextDouble() * 0.4D;
                    double z = entity.getZ() - 0.1D + rand.nextDouble() * 0.2D + (entity.getBbWidth() / 2 * (rand.nextBoolean() ? 1 : -1));

                    double motionX = 0.02D - rand.nextDouble() * 0.04D;
                    double motionY = 0.02D - rand.nextDouble() * 0.04D;
                    double motionZ = 0.02D - rand.nextDouble() * 0.04D;

                    Minecraft.getInstance().levelRenderer.addParticle(
                            new ParticleBlurData(red, green, blue, scale, ageMultiplier), false,
                            x, y, z,
                            motionX, motionY, motionZ);
                }
            }
        }
    }

}
