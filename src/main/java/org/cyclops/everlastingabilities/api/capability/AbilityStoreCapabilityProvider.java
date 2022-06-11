package org.cyclops.everlastingabilities.api.capability;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.cyclopscore.modcompat.capabilities.ICapabilityTypeGetter;
import org.cyclops.cyclopscore.modcompat.capabilities.SerializableCapabilityProvider;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Map;

/**
 * NBT storage for the {@link IAbilityStore} capability.
 * @author rubensworks
 */
public class AbilityStoreCapabilityProvider<T extends IMutableAbilityStore> extends SerializableCapabilityProvider<T> {

    public AbilityStoreCapabilityProvider(ICapabilityTypeGetter<T> capabilityGetter, T capability) {
        super(capabilityGetter, capability);
    }



    @Override
    protected Tag serializeNBT(IMutableAbilityStore capability) {
        return serializeNBTStatic(capability);
    }

    @Override
    protected void deserializeNBT(IMutableAbilityStore capability, Tag nbt) {
        deserializeNBTStatic(capability, nbt);
    }

    public static Tag serializeNBTStatic(IMutableAbilityStore capability) {
        ListTag list = new ListTag();
        for (Ability ability : capability.getAbilities()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", AbilityTypes.REGISTRY.getKey(ability.getAbilityType()).toString());
            tag.putInt("level", ability.getLevel());
            list.add(tag);
        }
        return list;
    }

    public static void deserializeNBTStatic(IMutableAbilityStore capability, Tag nbt) {
        Map<IAbilityType, Integer> abilityTypes = Maps.newHashMap();
        if (nbt instanceof ListTag) {
            if (((ListTag) nbt).getElementType() == Tag.TAG_COMPOUND) {
                ListTag list = (ListTag) nbt;
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag tag = list.getCompound(i);
                    String name = tag.getString("name");
                    int level = tag.getInt("level");
                    IAbilityType abilityType = AbilityTypes.REGISTRY.getValue(new ResourceLocation(name));
                    if (abilityType != null) {
                        abilityTypes.put(abilityType, level);
                    } else {
                        // Don't spam the player...
                        //EverlastingAbilities.clog(Level.WARN, "Skipped loading unknown ability by name: " + unlocalizedName);
                    }
                }
            }
        } else {
            EverlastingAbilities.clog(org.apache.logging.log4j.Level.WARN, "Resetting a corrupted ability storage.");
        }
        capability.setAbilities(abilityTypes);
    }
}
