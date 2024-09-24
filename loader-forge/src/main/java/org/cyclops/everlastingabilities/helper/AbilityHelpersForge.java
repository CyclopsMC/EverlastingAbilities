package org.cyclops.everlastingabilities.helper;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.everlastingabilities.CapabilitiesForge;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityConditionForge;
import org.cyclops.everlastingabilities.api.IAbilityCondition;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.ItemDataMutableAbilityStore;
import org.cyclops.everlastingabilities.item.ItemGuiAbilityContainer;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author rubensworks
 */
public class AbilityHelpersForge extends AbilityHelpersCommon {

    protected static Predicate<Holder<IAbilityType>> PREDICATE_ABILITY_ENABLED = ability -> ((AbilityConditionForge) ability.value().getCondition()).getCondition().test(ICondition.IContext.EMPTY, NbtOps.INSTANCE);

    public AbilityHelpersForge(IModHelpers modHelpers) {
        super(modHelpers);
    }

    @Override
    public Predicate<Holder<IAbilityType>> getPredicateAbilityEnabled() {
        return PREDICATE_ABILITY_ENABLED;
    }

    @Override
    public Optional<IMutableAbilityStore> getEntityAbilityStore(Entity entity) {
        return entity.getCapability(CapabilitiesForge.CAPABILITY).resolve();
    }

    @Override
    public Optional<IMutableAbilityStore> getItemAbilityStore(ItemStack itemStack) {
        // TODO: use capabilities when Forge restores item capabilities
        if (itemStack.getItem() instanceof ItemGuiAbilityContainer) {
            return Optional.of(new ItemDataMutableAbilityStore(itemStack));
        }
        return Optional.empty();
    }

    @Override
    public IAbilityCondition getAbilityConditionTrue() {
        return new AbilityConditionForge(TrueCondition.INSTANCE);
    }

    @Override
    public IAbilityCondition getAbilityConditionFalse() {
        return new AbilityConditionForge(FalseCondition.INSTANCE);
    }

    @Override
    public Codec<IAbilityCondition> getAbilityConditionCodec() {
        return ICondition.CODEC.xmap(
                AbilityConditionForge::new,
                abilityCondition -> ((AbilityConditionForge) abilityCondition).getCondition()
        );
    }

    private static final String PLAYER_NBT_KEY_LAST_FLIGHT = Reference.MOD_ID + ":" + "lastFlight";

    @Override
    public boolean hasPlayerStateLastFlight(Player player) {
        return player.getPersistentData().contains(PLAYER_NBT_KEY_LAST_FLIGHT);
    }

    @Override
    public boolean isPlayerStateLastFlight(Player player) {
        return player.getPersistentData().getBoolean(PLAYER_NBT_KEY_LAST_FLIGHT);
    }

    @Override
    public void removePlayerStateLastFlight(Player player) {
        player.getPersistentData().remove(PLAYER_NBT_KEY_LAST_FLIGHT);
    }

    @Override
    public void setPlayerStateLastFlight(Player player, boolean lastFlight) {
        player.getPersistentData().putBoolean(PLAYER_NBT_KEY_LAST_FLIGHT, lastFlight);
    }

    private static final String NBT_TOTEM_SPAWNED = Reference.MOD_ID + ":totemSpawned";
    @Override
    public boolean isFirstTotemSpawn(Player player) {
        CompoundTag playerTag = player.getPersistentData();
        if (!playerTag.contains(NBT_TOTEM_SPAWNED)) {
            playerTag.putBoolean(NBT_TOTEM_SPAWNED, true);
            return true;
        }

        return false;
    }
}
