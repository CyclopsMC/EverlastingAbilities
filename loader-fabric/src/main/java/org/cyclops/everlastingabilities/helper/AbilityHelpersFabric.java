package org.cyclops.everlastingabilities.helper;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.NotResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.TrueResourceCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.everlastingabilities.ability.AbilityConditionFabric;
import org.cyclops.everlastingabilities.api.IAbilityCondition;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.ItemDataMutableAbilityStore;
import org.cyclops.everlastingabilities.attachment.AttachableMutableAbilityStore;
import org.cyclops.everlastingabilities.attachment.Attachments;
import org.cyclops.everlastingabilities.item.ItemGuiAbilityContainer;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author rubensworks
 */
public class AbilityHelpersFabric extends AbilityHelpersCommon {

    protected static Predicate<Holder<IAbilityType>> PREDICATE_ABILITY_ENABLED = ability -> ((AbilityConditionFabric) ability.value().getCondition()).getCondition().test(null);

    public AbilityHelpersFabric(IModHelpers modHelpers) {
        super(modHelpers);
    }

    @Override
    public Predicate<Holder<IAbilityType>> getPredicateAbilityEnabled() {
        return PREDICATE_ABILITY_NOT_DISABLED.and(PREDICATE_ABILITY_ENABLED);
    }

    @Override
    public Optional<IMutableAbilityStore> getEntityAbilityStore(Entity entity) {
        AttachableMutableAbilityStore store = new AttachableMutableAbilityStore(entity);
        if (entity instanceof Mob mob) {
            this.initializeEntityAbilities(mob, store);
        }
        return Optional.of(store);
    }

    @Override
    public Optional<IMutableAbilityStore> getItemAbilityStore(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemGuiAbilityContainer) {
            return Optional.of(new ItemDataMutableAbilityStore(itemStack));
        }
        return Optional.empty();
    }

    @Override
    public IAbilityCondition getAbilityConditionTrue() {
        return new AbilityConditionFabric(new TrueResourceCondition());
    }

    @Override
    public IAbilityCondition getAbilityConditionFalse() {
        return new AbilityConditionFabric(new NotResourceCondition(new TrueResourceCondition()));
    }

    @Override
    public Codec<IAbilityCondition> getAbilityConditionCodec() {
        return ResourceCondition.CODEC.xmap(
                AbilityConditionFabric::new,
                abilityCondition -> ((AbilityConditionFabric) abilityCondition).getCondition()
        );
    }

    @Override
    public boolean hasPlayerStateLastFlight(Player player) {
        return player.hasAttached(Attachments.LAST_FLIGHT);
    }

    @Override
    public boolean isPlayerStateLastFlight(Player player) {
        return player.getAttachedOrSet(Attachments.LAST_FLIGHT, false);
    }

    @Override
    public void removePlayerStateLastFlight(Player player) {
        player.removeAttached(Attachments.LAST_FLIGHT);
    }

    @Override
    public void setPlayerStateLastFlight(Player player, boolean lastFlight) {
        player.setAttached(Attachments.LAST_FLIGHT, lastFlight);
    }

    @Override
    public boolean isFirstTotemSpawn(Player player) {
        if (!player.hasAttached(Attachments.TOTEM_SPAWNED)) {
            player.setAttached(Attachments.TOTEM_SPAWNED, true);
            return true;
        }
        return false;
    }
}
