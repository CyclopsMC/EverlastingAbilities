package org.cyclops.everlastingabilities.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.client.particle.ParticleBlurData;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.proxy.ClientProxyComponent;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.Random;

/**
 * Proxy for the client side.
 * 
 * @author rubensworks
 * 
 */
public class ClientProxy extends ClientProxyComponent {

	public ClientProxy() {
		super(new CommonProxy());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public ModBase getMod() {
		return EverlastingAbilities._instance;
	}

	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Post event) {
		LivingEntity entity = event.getEntity();
		if (((GeneralConfig.showEntityParticles && entity instanceof CreatureEntity) // TODO CreatureEntity was IAnimal
				|| (GeneralConfig.showPlayerParticles && entity instanceof PlayerEntity))
				&& !Minecraft.getInstance().isPaused()
				&& entity.level.getGameTime() % 10 == 0) {
			entity.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent((abilityStore) -> {
				if (!abilityStore.getAbilities().isEmpty()) {
					Triple<Integer, Integer, Integer> abilityColors = AbilityHelpers.getAverageRarityColor(abilityStore);
					float r = abilityColors.getLeft() / 255F;
					float g = abilityColors.getMiddle() / 255F;
					float b = abilityColors.getRight() / 255F;

					Random rand = entity.level.random;
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
			});
		}
	}
    
}
