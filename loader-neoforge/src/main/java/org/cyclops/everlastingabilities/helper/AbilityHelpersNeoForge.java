package org.cyclops.everlastingabilities.helper;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.conditions.FalseCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.TrueCondition;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntriesCommon;
import org.cyclops.everlastingabilities.ability.AbilityConditionNeoForge;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityCondition;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author rubensworks
 */
public class AbilityHelpersNeoForge extends AbilityHelpersCommon {

    protected static Predicate<Holder<IAbilityType>> PREDICATE_ABILITY_ENABLED = ability -> ((AbilityConditionNeoForge) ability.value().getCondition()).getCondition().test(ICondition.IContext.EMPTY);

    public AbilityHelpersNeoForge(IModHelpers modHelpers) {
        super(modHelpers);
    }

    @Override
    public Predicate<Holder<IAbilityType>> getPredicateAbilityEnabled() {
        return PREDICATE_ABILITY_ENABLED;
    }

    @Override
    public Optional<IMutableAbilityStore> getEntityAbilityStore(Entity entity) {
        return Optional.ofNullable(entity.getCapability(Capabilities.MutableAbilityStore.ENTITY));
    }

    @Override
    public ItemStack getTotem(Ability ability) {
        ItemStack itemStack = new ItemStack(RegistryEntriesCommon.ITEM_ABILITY_TOTEM);
        Optional.ofNullable(itemStack.getCapability(Capabilities.MutableAbilityStore.ITEM))
                .ifPresent(mutableAbilityStore -> mutableAbilityStore.addAbility(ability, true));
        return itemStack;
    }

    @Override
    public IAbilityCondition getAbilityConditionTrue() {
        return new AbilityConditionNeoForge(TrueCondition.INSTANCE);
    }

    @Override
    public IAbilityCondition getAbilityConditionFalse() {
        return new AbilityConditionNeoForge(FalseCondition.INSTANCE);
    }

    @Override
    public Codec<IAbilityCondition> getAbilityConditionCodec() {
        return ICondition.CODEC.xmap(
                AbilityConditionNeoForge::new,
                abilityCondition -> ((AbilityConditionNeoForge) abilityCondition).getCondition()
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
}
