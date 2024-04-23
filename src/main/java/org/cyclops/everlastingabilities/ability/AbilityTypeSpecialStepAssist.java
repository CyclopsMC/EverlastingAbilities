package org.cyclops.everlastingabilities.ability;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Map;
import java.util.Objects;

/**
 * Ability type for flight.
 * @author rubensworks
 */
public class AbilityTypeSpecialStepAssist extends AbilityTypeAdapter {

    @Deprecated
    private final boolean forceDefaultStepHeight; // TODO: rm in next MC version
    private final Map<Integer, AttributeModifier> attributeModifiers;

    public AbilityTypeSpecialStepAssist(ICondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                        boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot,
                                        boolean forceDefaultStepHeight) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.forceDefaultStepHeight = forceDefaultStepHeight;
        this.attributeModifiers = Maps.newHashMap();
        for (int i = 1; i <= maxLevel; i++) {
            this.attributeModifiers.put(i, new AttributeModifier(Reference.MOD_ID + ":stepHeightModifier" + i, i, AttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public Codec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_SPECIAL_STEP_ASSIST);
    }

    public boolean isForceDefaultStepHeight() {
        return forceDefaultStepHeight;
    }

    @Override
    public void onTick(Player player, int level) {
        super.onTick(player, level);

        // On world re-join, ensure the modifier is in place.
        AttributeInstance attribute = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (attribute != null) {
            AttributeModifier modifier = this.attributeModifiers.get(level);
            if (!attribute.hasModifier(modifier)) {
                attribute.addTransientModifier(modifier);
            }
        }
    }

    @Override
    public void onChangedLevel(Player player, int oldLevel, int newLevel) {
        AttributeInstance attribute = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (attribute != null) {
            if (oldLevel > 0) {
                attribute.removeModifier(this.attributeModifiers.get(oldLevel));
            }
            if (newLevel > 0) {
                attribute.addTransientModifier(this.attributeModifiers.get(newLevel));
            }
        }
    }
}
