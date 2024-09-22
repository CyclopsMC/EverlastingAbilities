package org.cyclops.everlastingabilities.ability;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityCondition;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Ability type for attribute modifiers.
 * @author rubensworks
 */
public class AbilityTypeAttributeModifier extends AbilityTypeAdapter {

    private final String attributeId;
    private final Holder<Attribute> attribute;
    private final double amountFactor;
    private final AttributeModifier.Operation operation;
    private final Map<Integer, AttributeModifier> attributeModifiers;

    public AbilityTypeAttributeModifier(IAbilityCondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                        boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot,
                                        String attributeId, double amountFactor, AttributeModifier.Operation operation) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.attributeId = attributeId;
        Optional<Holder.Reference<Attribute>> attributeOptional = BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(attributeId));
        if (attributeOptional.isEmpty()) {
            EverlastingAbilitiesInstance.MOD.log(org.apache.logging.log4j.Level.INFO, "No attribute was found with id: " + attributeId + ". Marking as disabled.");
            this.setCondition(EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAbilityConditionFalse());
            this.attribute = null;
        } else {
            this.attribute = attributeOptional.get();
        }
        this.amountFactor = amountFactor;
        this.operation = operation;
        this.attributeModifiers = Maps.newHashMap();
        for (int i = 1; i <= maxLevel; i++) {
            this.attributeModifiers.put(i, new AttributeModifier(ResourceLocation.parse(Reference.MOD_ID + ":modifier_" + name + "_" + i), this.amountFactor * i, this.operation));
        }
    }

    public String getAttributeId() {
        return attributeId;
    }

    public double getAmountFactor() {
        return amountFactor;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    @Override
    public MapCodec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_ATTRIBUTE_MODIFIER.value());
    }

    @Override
    public void onTick(Player player, int level) {
        super.onTick(player, level);

        // On world re-join, ensure the modifier is in place.
        AttributeInstance attribute = player.getAttribute(this.attribute);
        if (attribute != null) {
            AttributeModifier modifier = this.attributeModifiers.get(level);
            if (!attribute.hasModifier(modifier.id())) {
                attribute.addTransientModifier(modifier);
            }
        }
    }

    @Override
    public void onChangedLevel(Player player, int oldLevel, int newLevel) {
        AttributeInstance attribute = player.getAttribute(this.attribute);
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
