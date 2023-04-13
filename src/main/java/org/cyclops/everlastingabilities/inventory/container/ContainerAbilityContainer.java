package org.cyclops.everlastingabilities.inventory.container;

import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.cyclopscore.inventory.ItemLocation;
import org.cyclops.cyclopscore.inventory.container.ItemInventoryContainer;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStoreRegistryAccess;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.client.gui.ContainerScreenAbilityContainer;
import org.cyclops.everlastingabilities.item.ItemGuiAbilityContainer;

import java.util.Collections;
import java.util.List;

/**
 * Container for the labeller.
 * @author rubensworks
 */
public class ContainerAbilityContainer extends ItemInventoryContainer<ItemGuiAbilityContainer> {

    @OnlyIn(Dist.CLIENT)
    private ContainerScreenAbilityContainer gui;

    public ContainerAbilityContainer(int id, Inventory inventory, FriendlyByteBuf packetBuffer) {
        this(id, inventory, ItemLocation.readFromPacketBuffer(packetBuffer));
    }

    public ContainerAbilityContainer(int id, Inventory inventory, ItemLocation itemLocation) {
        super(RegistryEntries.CONTAINER_ABILITYCONTAINER, id, inventory, itemLocation);
        addInventory(inventory, 0, 8, 195, 1, 9);

        // If level is not consistent with total experience count, fix it.
        // This can be caused by vanilla's xp command that adds levels but doesn't change the total xp count.
        // If other mods mess things up, this will resolve it as well.
        int level = AbilityHelpers.getLevelForExperience(player.totalExperience);
        if (player.experienceLevel != level) {
            player.totalExperience = AbilityHelpers.getExperienceForLevel(player.experienceLevel);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setGui(ContainerScreenAbilityContainer gui) {
        this.gui = gui;
    }

    @OnlyIn(Dist.CLIENT)
    public ContainerScreenAbilityContainer getGui() {
        return this.gui;
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    public LazyOptional<IMutableAbilityStore> getPlayerAbilityStore() {
        return player.getCapability(MutableAbilityStoreConfig.CAPABILITY);
    }

    public LazyOptional<IMutableAbilityStore> getItemAbilityStore() {
        ItemStack itemStack = getItemStack(player);
        if (itemStack.isEmpty()) {
            return LazyOptional.empty();
        }
        return itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY)
                .lazyMap(store -> {
                    ((IMutableAbilityStoreRegistryAccess) store).setRegistryAccess(this.player.level.registryAccess());
                    return store;
                });
    }

    public List<Ability> getPlayerAbilities() {
        return getPlayerAbilityStore()
                .map(abilityStore -> (List<Ability>) Lists.newArrayList(abilityStore.getAbilities()))
                .orElseGet(Collections::emptyList);
    }

    public List<Ability> getItemAbilities() {
        return getItemAbilityStore()
                .map(abilityStore -> (List<Ability>) Lists.newArrayList(abilityStore.getAbilities()))
                .orElseGet(Collections::emptyList);
    }

    public void moveFromPlayer(Ability playerAbility) {
        getItemAbilityStore().ifPresent(abilityStore -> {
            Ability insertedAbility = AbilityHelpers.insert(playerAbility, abilityStore);
            if (!insertedAbility.isEmpty()) {
                AbilityHelpers.removePlayerAbility(player, insertedAbility, true, true);
            }
        });
    }

    public void moveToPlayer(Ability itemAbility) {
        getItemAbilityStore().ifPresent(abilityStore -> {
            Ability insertedAbility = AbilityHelpers.addPlayerAbility(player, itemAbility, true, true);
            if (!insertedAbility.isEmpty()) {
                AbilityHelpers.extract(insertedAbility, abilityStore);

                if(getItemAbilities().isEmpty() && !getItem().canMoveFromPlayer()) {
                    this.itemLocation.setItemStack(player, ItemStack.EMPTY);
                }
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return !getItemStack(player).isEmpty() && (getItem().canMoveFromPlayer() || !getItemAbilities().isEmpty());
    }

}
