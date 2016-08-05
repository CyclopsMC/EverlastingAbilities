package org.cyclops.everlastingabilities.ability;

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
    public static @Nullable Ability addPlayerAbility(EntityPlayerMP player, Ability ability, boolean doAdd) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        return abilityStore.addAbility(ability, doAdd);
    }

    /**
     * Remove the given ability.
     * @param ability The ability.
     * @param doRemove If the removal should actually be done.
     * @return The ability part that was removed.
     */
    public static @Nullable Ability removePlayerAbility(EntityPlayerMP player, Ability ability, boolean doRemove) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        return abilityStore.removeAbility(ability, doRemove);
    }

    public static void setPlayerAbilities(EntityPlayerMP player, Map<IAbilityType, Integer> abilityTypes) {
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        abilityStore.setAbilities(abilityTypes);
    }

}
