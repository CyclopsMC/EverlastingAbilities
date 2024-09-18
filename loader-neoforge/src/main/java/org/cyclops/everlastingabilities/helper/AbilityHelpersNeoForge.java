package org.cyclops.everlastingabilities.helper;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.conditions.FalseCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.TrueCondition;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.RegistryEntriesCommon;
import org.cyclops.everlastingabilities.ability.AbilityConditionNeoForge;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityCondition;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Map;
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
    public Ability addPlayerAbility(Player player, Ability ability, boolean doAdd, boolean modifyXp) {
        return Optional.ofNullable(player.getCapability(Capabilities.MutableAbilityStore.ENTITY))
                .map(abilityStore -> {
                    int oldLevel = abilityStore.hasAbilityType(ability.getAbilityTypeHolder())
                            ? abilityStore.getAbility(ability.getAbilityTypeHolder()).getLevel() : 0;

                    // Check max ability count
                    if (getMaxPlayerAbilities(player.getCommandSenderWorld()) >= 0 && oldLevel == 0
                            && getMaxPlayerAbilities(player.getCommandSenderWorld()) <= abilityStore.getAbilities().size()) {
                        return Ability.EMPTY;
                    }

                    Ability result = abilityStore.addAbility(ability, doAdd);
                    int currentXp = player.totalExperience;
                    if (result != null && modifyXp && getExperience(result) > currentXp) {
                        int maxLevels = player.totalExperience / result.getAbilityType().getXpPerLevelScaled();
                        if (maxLevels == 0) {
                            result = Ability.EMPTY;
                        } else {
                            result = new Ability(result.getAbilityTypeHolder(), maxLevels);
                        }
                    }
                    if (doAdd && !result.isEmpty()) {
                        player.totalExperience -= getExperience(result);
                        // Fix xp bar
                        player.experienceLevel = getLevelForExperience(player.totalExperience);
                        int xpForLevel = getExperienceForLevel(player.experienceLevel);
                        player.experienceProgress = (float)(player.totalExperience - xpForLevel) / (float)player.getXpNeededForNextLevel();

                        int newLevel = abilityStore.getAbility(result.getAbilityTypeHolder()).getLevel();
                        onPlayerAbilityChanged(player, result.getAbilityType(), oldLevel, newLevel);
                    }
                    return result;
                })
                .orElse(Ability.EMPTY);
    }

    @Override
    public Ability removePlayerAbility(Player player, Ability ability, boolean doRemove, boolean modifyXp) {
        return Optional.ofNullable(player.getCapability(Capabilities.MutableAbilityStore.ENTITY))
                .map(abilityStore -> {
                    int oldLevel = abilityStore.hasAbilityType(ability.getAbilityTypeHolder())
                            ? abilityStore.getAbility(ability.getAbilityTypeHolder()).getLevel() : 0;
                    Ability result = abilityStore.removeAbility(ability, doRemove);
                    if (modifyXp && !result.isEmpty()) {
                        player.giveExperiencePoints(getExperience(result));
                        int newLevel = abilityStore.hasAbilityType(result.getAbilityTypeHolder())
                                ? abilityStore.getAbility(result.getAbilityTypeHolder()).getLevel() : 0;
                        onPlayerAbilityChanged(player, result.getAbilityType(), oldLevel, newLevel);
                    }
                    return result;
                })
                .orElse(Ability.EMPTY);
    }

    @Override
    public void setPlayerAbilities(ServerPlayer player, Map<Holder<IAbilityType>, Integer> abilityTypes) {
        Optional.ofNullable(player.getCapability(Capabilities.MutableAbilityStore.ENTITY))
                .ifPresent(abilityStore -> abilityStore.setAbilities(abilityTypes));
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
}
