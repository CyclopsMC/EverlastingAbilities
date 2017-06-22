package org.cyclops.everlastingabilities.api.capability;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.everlastingabilities.api.Ability;

import java.util.List;

/**
 * Display type for ability stores.  Used to control how abilities should be displayed.
 * @author CplPibald
 */

 // DisplayTypes:
 //
 // NORMAL = Use when abilities are known
 // OBFUSCATED = Use when abilities are not known
 
public abstract class AbilityStoreDisplayType {

    public static AbilityStoreDisplayType NORMAL = new AbilityStoreDisplayType() {
        @Override
        public void addInformation(IAbilityStore abilityStore, ItemStack itemStack, World world, List<String> list, ITooltipFlag tooltipFlag) {
            // Display each ability in store, one line at a time
            // Or display "none" string if list is empty
            boolean empty = true;
            for (Ability ability : abilityStore.getAbilities()) {
                empty = false;
                String name = L10NHelpers.localize(ability.getAbilityType().getUnlocalizedName());
                list.add(TextFormatting.YELLOW + name + ": " + TextFormatting.RESET + ability.getLevel());
            }
            if (empty) {
                list.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + L10NHelpers.localize("general.everlastingabilities.empty"));
            }
        }
    };
    
    public static AbilityStoreDisplayType OBFUSCATED = new AbilityStoreDisplayType() {
        @Override
        public void addInformation(IAbilityStore abilityStore, ItemStack itemStack, World world, List<String> list, ITooltipFlag tooltipFlag) {
            // Ability display is obfuscated.  
            // This only blocks display in item tooltip.  Player can still view abilities by activating totem.
            list.add(TextFormatting.OBFUSCATED.toString() + "Obfuscated");
        }
    };

    //-------
    // Called when displaying tooltip for an ItemStack that has an ability store attached
    //-------
    public abstract void addInformation(IAbilityStore abilityStore, ItemStack itemStack, World world, List<String> list, ITooltipFlag tooltipFlag);
}