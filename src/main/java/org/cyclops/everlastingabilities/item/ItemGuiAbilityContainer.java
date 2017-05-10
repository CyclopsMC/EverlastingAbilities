package org.cyclops.everlastingabilities.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.item.ItemGui;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.ItemStackMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.client.gui.GuiAbilityContainer;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;

import java.util.List;

/**
 * Base class for items with abilities.
 * @author rubensworks
 */
public abstract class ItemGuiAbilityContainer extends ItemGui {
    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    protected ItemGuiAbilityContainer(ExtendedConfig eConfig) {
        super(eConfig);
        setMaxStackSize(1);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerAbilityContainer.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiAbilityContainer.class;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        IAbilityStore abilityStore = itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);

        switch(abilityStore.getDisplayType()) {
            case NORMAL: {
                // Normal display of abilities
                boolean empty = true;
                for (Ability ability : abilityStore.getAbilities()) {
                    empty = false;
                    String name = L10NHelpers.localize(ability.getAbilityType().getUnlocalizedName());
                    list.add(TextFormatting.YELLOW + name + ": " + TextFormatting.RESET + ability.getLevel());
                }
                if (empty) {
                    list.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + L10NHelpers.localize("general.everlastingabilities.empty"));
                }
                break;
            }
            
            case OBFUSCATED: {
                // Ability display is obfuscated.  
                // This only blocks display in item tooltip.  Player can still view abilities by activating totem.
                list.add(TextFormatting.OBFUSCATED.toString() + "Obfuscated");
                break;
            }
        }
        
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        // TODO: restore when Forge fixed that bug (backwards compat is already taken care of, because data is stored twice (in stacktag and capdata))
        //return new SerializableCapabilityProvider<IMutableAbilityStore>(MutableAbilityStoreConfig.CAPABILITY,
        //        new DefaultMutableAbilityStore());
        return new DefaultCapabilityProvider<IMutableAbilityStore>(MutableAbilityStoreConfig.CAPABILITY,
                new ItemStackMutableAbilityStore(stack));
    }

    public abstract boolean canMoveFromPlayer();

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem();
    }
}
