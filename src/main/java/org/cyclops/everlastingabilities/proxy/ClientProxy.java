package org.cyclops.everlastingabilities.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.IAnimals;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.client.particle.ParticleBlur;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.proxy.ClientProxyComponent;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;

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
		EntityLivingBase entity = event.getEntity();
		if (entity instanceof IAnimals && entity.worldObj.getTotalWorldTime() % 10 == 0
				&& entity.getDataManager().get(EverlastingAbilities.ENTITY_DATA_ABILITY_RARITY) >= 0) {
			int rarity = entity.getDataManager().get(EverlastingAbilities.ENTITY_DATA_ABILITY_RARITY);
			int color = AbilityHelpers.RARITY_COLORS[rarity];
			Triple<Float, Float, Float> colorObj = Helpers.intToRGB(color);
			float r = colorObj.getLeft();
			float g = colorObj.getMiddle();
			float b = colorObj.getRight();

			Random rand = entity.worldObj.rand;
			float scale = 0.5F - rand.nextFloat() * 0.3F;
			float red = Math.max(0, r - rand.nextFloat() * 0.1F);
			float green = Math.max(0, g - rand.nextFloat() * 0.1F);
			float blue = Math.max(0, b - rand.nextFloat() * 0.1F);
			float ageMultiplier = (float) (rand.nextDouble() * 10D + 20D);

			double x = entity.posX - 0.1D + rand.nextDouble() * 0.2D + (entity.width / 2 * (rand.nextBoolean() ? 1 : -1));
			double y = entity.posY + entity.height - 0.2D + rand.nextDouble() * 0.4D;
			double z = entity.posZ - 0.1D + rand.nextDouble() * 0.2D + (entity.width / 2 * (rand.nextBoolean() ? 1 : -1));

			double motionX = 0.02D - rand.nextDouble() * 0.04D;
			double motionY = 0.02D - rand.nextDouble() * 0.04D;
			double motionZ = 0.02D - rand.nextDouble() * 0.04D;

			ParticleBlur blur = new ParticleBlur(entity.worldObj, x, y, z, scale,
					motionX, motionY, motionZ,
					red, green, blue, ageMultiplier);
			Minecraft.getMinecraft().effectRenderer.addEffect(blur);
		}
	}
    
}
