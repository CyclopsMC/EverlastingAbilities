package org.cyclops.everlastingabilities.ability;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityCondition;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Map;
import java.util.Objects;

/**
 * Ability type for flight.
 * @author rubensworks
 */
@Deprecated // TODO: Remove in the next major version, and rewrite as attribute_modifier
public class AbilityTypeSpecialStepAssist extends AbilityTypeAdapter {

    private final Map<Integer, AttributeModifier> attributeModifiers;

    public AbilityTypeSpecialStepAssist(IAbilityCondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                        boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.attributeModifiers = Maps.newHashMap();
        for (int i = 1; i <= maxLevel; i++) {
            this.attributeModifiers.put(i, new AttributeModifier(ResourceLocation.parse(Reference.MOD_ID + ":step_height_modifier" + i), i, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public MapCodec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_SPECIAL_STEP_ASSIST.get());
    }

    @Override
    public void onTick(Player player, int level) {
        super.onTick(player, level);

        // On world re-join, ensure the modifier is in place.
        AttributeInstance attribute = player.getAttribute(Attributes.STEP_HEIGHT);
        if (attribute != null) {
            AttributeModifier modifier = this.attributeModifiers.get(level);
            if (!attribute.hasModifier(modifier.id())) {
                attribute.addTransientModifier(modifier);
            }
        }
    }

    @Override
    public void onChangedLevel(Player player, int oldLevel, int newLevel) {
        AttributeInstance attribute = player.getAttribute(Attributes.STEP_HEIGHT);
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
