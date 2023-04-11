package org.cyclops.everlastingabilities.api.capability;

import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.modcompat.capabilities.ICapabilityTypeGetter;
import org.cyclops.cyclopscore.modcompat.capabilities.SerializableCapabilityProvider;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Map;

/**
 * NBT storage for the {@link IAbilityStore} capability.
 * @author rubensworks
 */
public class AbilityStoreCapabilityProvider<T extends IMutableAbilityStore> extends SerializableCapabilityProvider<T> {

    // TODO: remove this in the next major MC update, this is only needed for backwards-compat in EA 1.x->2.x
    public static Map<String, String> BACKWARDS_COMPATIBLE_MAPPING = Maps.newHashMap();
    static {
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:absorbtion", "everlastingabilities:effect/absorption");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:bad_omen", "everlastingabilities:effect/bad_omen");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:blindness", "everlastingabilities:effect/blindness");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:conduit_power", "everlastingabilities:effect/conduit_power");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:dolphins_grace", "everlastingabilities:effect/dolphins_grace");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:fire_resistance", "everlastingabilities:effect/fire_resistance");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:glowing", "everlastingabilities:effect/glowing");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:haste", "everlastingabilities:effect/haste");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:hunger", "everlastingabilities:effect/hunger");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:invisibility", "everlastingabilities:effect/invisibility");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:jump_boost", "everlastingabilities:effect/jump_boost");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:levitation", "everlastingabilities:effect/levitation");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:luck", "everlastingabilities:effect/luck");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:mining_fatigue", "everlastingabilities:effect/mining_fatigue");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:nausea", "everlastingabilities:effect/nausea");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:night_vision", "everlastingabilities:effect/night_vision");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:poison", "everlastingabilities:effect/poison");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:regeneration", "everlastingabilities:effect/regeneration");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:resistance", "everlastingabilities:effect/resistance");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:saturation", "everlastingabilities:effect/saturation");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:slow_falling", "everlastingabilities:effect/slow_falling");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:slowness", "everlastingabilities:effect/slowness");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:speed", "everlastingabilities:effect/speed");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:strength", "everlastingabilities:effect/strength");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:unluck", "everlastingabilities:effect/unluck");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:water_breathing", "everlastingabilities:effect/water_breathing");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:weakness", "everlastingabilities:effect/weakness");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:wither", "everlastingabilities:effect/wither");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:bonemealer", "everlastingabilities:special/bonemealer");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:fertility", "everlastingabilities:special/fertility");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:flight", "everlastingabilities:special/flight");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:magnetize", "everlastingabilities:special/magnetize");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:power_stare", "everlastingabilities:special/power_stare");
        BACKWARDS_COMPATIBLE_MAPPING.put("everlastingabilities:step_assist", "everlastingabilities:special/step_assist");
    }

    public AbilityStoreCapabilityProvider(ICapabilityTypeGetter<T> capabilityGetter, T capability) {
        super(capabilityGetter, capability);
    }

    @Override
    protected Tag serializeNBT(IMutableAbilityStore capability) {
        return serializeNBTStatic(AbilityHelpers.getRegistryServer(), capability);
    }

    @Override
    protected void deserializeNBT(IMutableAbilityStore capability, Tag nbt) {
        deserializeNBTStatic(AbilityHelpers.getRegistryServer(), capability, nbt);
    }

    public static Tag serializeNBTStatic(Registry<IAbilityType> registry, IMutableAbilityStore capability) {
        ListTag list = new ListTag();
        for (Ability ability : capability.getAbilities()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", registry.getKey(ability.getAbilityType()).toString());
            tag.putInt("level", ability.getLevel());
            list.add(tag);
        }
        return list;
    }

    public static void deserializeNBTStatic(Registry<IAbilityType> registry, IMutableAbilityStore capability, Tag nbt) {
        Map<IAbilityType, Integer> abilityTypes = Maps.newHashMap();
        if (nbt instanceof ListTag) {
            if (((ListTag) nbt).getElementType() == Tag.TAG_COMPOUND) {
                ListTag list = (ListTag) nbt;
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag tag = list.getCompound(i);
                    String name = tag.getString("name");
                    int level = tag.getInt("level");
                    IAbilityType abilityType = getAbilityBackwardsCompatible(registry, name);
                    if (abilityType != null) {
                        abilityTypes.put(abilityType, level);
                    } else {
                        EverlastingAbilities.clog(Level.WARN, "Skipped loading unknown ability by name: " + name);
                    }
                }
            }
        } else {
            EverlastingAbilities.clog(Level.WARN, "Resetting a corrupted ability storage.");
        }
        capability.setAbilities(abilityTypes);
    }

    public static IAbilityType getAbilityBackwardsCompatible(Registry<IAbilityType> registry, String name) {
        IAbilityType abilityType = registry.get(new ResourceLocation(name));
        if (abilityType != null) {
            return abilityType;
        }
        return registry.get(new ResourceLocation(BACKWARDS_COMPATIBLE_MAPPING.get(name)));
    }
}
