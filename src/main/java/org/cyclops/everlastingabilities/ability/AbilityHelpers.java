package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.core.helper.obfuscation.ObfuscationHelpers;
import org.cyclops.everlastingabilities.network.packet.SendPlayerCapabilitiesPacket;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * General ability helpers.
 * @author rubensworks
 */
public class AbilityHelpers {

    public static void sendPlayerUpdateCapabilities(EntityPlayerMP player) {
        EverlastingAbilities._instance.getPacketHandler().sendToPlayer(
                new SendPlayerCapabilitiesPacket(ObfuscationHelpers.getEntityCapabilities(player)), player);
    }

    /**
     * Add the given ability.
     * @param ability The ability.
     * @param doAdd If the addition should actually be done.
     * @return The ability part that was added.
     */
    public static @Nullable Ability addPlayerAbility(EntityPlayer player, Ability ability, boolean doAdd, boolean modifyXp) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        Ability result = abilityStore.addAbility(ability, doAdd);
        int currentXp = player.experienceTotal;
        if (result != null && modifyXp && getExperience(result) > currentXp) {
            int maxLevels = player.experienceTotal / result.getAbilityType().getBaseXpPerLevel();
            if (maxLevels == 0) {
                result = null;
            } else {
                result = new Ability(result.getAbilityType(), maxLevels);
            }
        }
        if (doAdd && result != null) {
            player.addExperience(-getExperience(result));
        }
        if (player instanceof EntityPlayerMP) {
            sendPlayerUpdateCapabilities((EntityPlayerMP) player);
        }
        return result;
    }

    /**
     * Remove the given ability.
     * @param ability The ability.
     * @param doRemove If the removal should actually be done.
     * @return The ability part that was removed.
     */
    public static @Nullable Ability removePlayerAbility(EntityPlayer player, Ability ability, boolean doRemove, boolean modifyXp) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        Ability result = abilityStore.removeAbility(ability, doRemove);
        if (modifyXp && result != null) {
            player.addExperience(getExperience(result));
        }
        if (player instanceof EntityPlayerMP) {
            sendPlayerUpdateCapabilities((EntityPlayerMP) player);
        }
        return result;
    }

    public static int getExperience(Ability ability) {
        if (ability == null) {
            return 0;
        }
        return ability.getAbilityType().getBaseXpPerLevel() * ability.getLevel();
    }

    public static void setPlayerAbilities(EntityPlayerMP player, Map<IAbilityType, Integer> abilityTypes) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        abilityStore.setAbilities(abilityTypes);
    }

    public static boolean canInsert(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        Ability added = mutableAbilityStore.addAbility(ability, false);
        return added != null && added.getLevel() == ability.getLevel();
    }

    public static boolean canExtract(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        Ability added = mutableAbilityStore.removeAbility(ability, false);
        return added != null && added.getLevel() == ability.getLevel();
    }

    public static boolean canInsertToPlayer(Ability ability, EntityPlayer player) {
        Ability added = addPlayerAbility(player, ability, false, true);
        return added != null && added.getLevel() == ability.getLevel();
    }

    public static Ability insert(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        return mutableAbilityStore.addAbility(ability, true);
    }

    public static Ability extract(Ability ability, IMutableAbilityStore mutableAbilityStore) {
        return mutableAbilityStore.removeAbility(ability, true);
    }

}
