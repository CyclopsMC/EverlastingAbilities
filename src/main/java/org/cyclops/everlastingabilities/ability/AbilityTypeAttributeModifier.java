package org.cyclops.everlastingabilities.ability;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Map;
import java.util.Objects;

/**
 * Ability type for attribute modifiers.
 * @author rubensworks
 */
public class AbilityTypeAttributeModifier extends AbilityTypeAdapter {

    private final String attributeId;
    private final Attribute attribute;
    private final double amountFactor;
    private final AttributeModifier.Operation operation;
    private final Map<Integer, AttributeModifier> attributeModifiers;

    public AbilityTypeAttributeModifier(ICondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                        boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot,
                                        String attributeId, double amountFactor, AttributeModifier.Operation operation) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.attributeId = attributeId;
        this.attribute = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(attributeId));
        if (attribute == null) {
            EverlastingAbilities.clog(org.apache.logging.log4j.Level.INFO, "No attribute was found with id: " + attributeId + ". Marking as disabled.");
            this.setCondition(FalseCondition.INSTANCE);
        }
        this.amountFactor = amountFactor;
        this.operation = operation;
        this.attributeModifiers = Maps.newHashMap();
        for (int i = 1; i <= maxLevel; i++) {
            this.attributeModifiers.put(i, new AttributeModifier(Reference.MOD_ID + ":modifier_" + name + "_" + i, this.amountFactor * i, this.operation));
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
    public Codec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_ATTRIBUTE_MODIFIER);
    }

    @Override
    public void onTick(Player player, int level) {
        super.onTick(player, level);

        // On world re-join, ensure the modifier is in place.
        AttributeInstance attribute = player.getAttribute(this.attribute);
        if (attribute != null) {
            AttributeModifier modifier = this.attributeModifiers.get(level);
            if (!attribute.hasModifier(modifier)) {
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
