package org.cyclops.everlastingabilities.api.capability;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Level;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Map;

/**
 * NBT storage for the {@link IAbilityStore} capability.
 * @author rubensworks
 */
public class AbilityStoreStorage implements Capability.IStorage<IAbilityStore> {

    @Override
    public INBT writeNBT(Capability<IAbilityStore> capability, IAbilityStore instance, Direction side) {
        ListNBT list = new ListNBT();
        for (Ability ability : instance.getAbilities()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("name", ability.getAbilityType().getRegistryName().toString());
            tag.putInt("level", ability.getLevel());
            list.add(tag);
        }
        return list;
    }

    @Override
    public void readNBT(Capability<IAbilityStore> capability, IAbilityStore instance, Direction side, INBT nbt) {
        Map<IAbilityType, Integer> abilityTypes = Maps.newHashMap();
        if (nbt instanceof ListNBT) {
            if (((ListNBT) nbt).getTagType() == Constants.NBT.TAG_COMPOUND) {
                ListNBT list = (ListNBT) nbt;
                for (int i = 0; i < list.size(); i++) {
                    CompoundNBT tag = list.getCompound(i);
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
            EverlastingAbilities.clog(Level.WARN, "Resetting a corrupted ability storage.");
        }
        instance.setAbilities(abilityTypes);
    }
}
