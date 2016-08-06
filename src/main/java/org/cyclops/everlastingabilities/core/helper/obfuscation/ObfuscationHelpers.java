package org.cyclops.everlastingabilities.core.helper.obfuscation;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

/**
 * Helper for getting private fields or methods.
 * @author rubensworks
 *
 */
public class ObfuscationHelpers {

    /**
     * Get the capabilities of the given entity.
     * @param entity The entity.
     * @return The capability dispatcher.
     */
    public static CapabilityDispatcher getEntityCapabilities(Entity entity) {
        Field field = ReflectionHelper.findField(Entity.class, ObfuscationData.ENTITY_CAPABILITIES);
        try {
            return (CapabilityDispatcher) field.get(entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the private 'particleTextures' field from {@link net.minecraft.client.particle.ParticleManager}.
     * @return The private 'particleTextures' field.
     */
    public static ResourceLocation getParticleTexture() {
        return ReflectionHelper.getPrivateValue(ParticleManager.class, null, ObfuscationData.PARTICLE_TEXTURES);
    }
	
}
