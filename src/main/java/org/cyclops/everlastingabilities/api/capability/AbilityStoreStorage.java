package org.cyclops.everlastingabilities.api.capability;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
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
    public NBTBase writeNBT(Capability<IAbilityStore> capability, IAbilityStore instance, EnumFacing side) {
        NBTTagList list = new NBTTagList();
        for (Ability ability : instance.getAbilities()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("name", ability.getAbilityType().getUnlocalizedName());
            tag.setInteger("level", ability.getLevel());
            list.appendTag(tag);
        }
        return list;
    }

    @Override
    public void readNBT(Capability<IAbilityStore> capability, IAbilityStore instance, EnumFacing side, NBTBase nbt) {
        Map<IAbilityType, Integer> abilityTypes = Maps.newHashMap();
        if (nbt instanceof NBTTagList) {
            if (((NBTTagList) nbt).getTagType() == MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal()) {
                NBTTagList list = (NBTTagList) nbt;
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound tag = list.getCompoundTagAt(i);
                    String unlocalizedName = tag.getString("name");
                    int level = tag.getInteger("level");
                    IAbilityType abilityType = AbilityTypes.REGISTRY.getAbilityType(unlocalizedName);
                    if (abilityType != null) {
                        abilityTypes.put(abilityType, level);
                    } else {
                        EverlastingAbilities.clog(Level.WARN, "Skipped loading unknown ability by name: " + unlocalizedName);
                    }
                }
            }
        } else {
            EverlastingAbilities.clog(Level.WARN, "Resetting a corrupted ability storage.");
        }
        instance.setAbilities(abilityTypes);
    }
}
