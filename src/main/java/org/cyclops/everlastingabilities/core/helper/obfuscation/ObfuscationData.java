package org.cyclops.everlastingabilities.core.helper.obfuscation;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Entries used for getting private fields and methods by using it in
 * {@link ReflectionHelper#getPrivateValue(Class, Object, String...)}.
 * These MCP mappings should be updated with every MC update!
 * @author rubensworks
 */
public class ObfuscationData {

    /**
     * Field from {@link net.minecraft.entity.Entity}.
     */
    public static final String[] ENTITY_CAPABILITIES = new String[] { "capabilities" };

    /**
     * Field from net.minecraft.client.particle.EffectRenderer.
     */
    public static final String[] PARTICLE_TEXTURES = new String[] { "PARTICLE_TEXTURES", "field_110737_b", "b" };
	
}
