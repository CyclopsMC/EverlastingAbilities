package org.cyclops.everlastingabilities.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.cyclopscore.item.ItemGui;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
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
    public void addInformation(ItemStack itemStack, World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        IAbilityStore abilityStore = itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);

        abilityStore.getDisplayType().addInformation(abilityStore, itemStack, world, list, flag);
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
