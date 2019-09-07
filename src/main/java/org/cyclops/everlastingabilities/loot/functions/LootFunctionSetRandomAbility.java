package org.cyclops.everlastingabilities.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

/**
 * A loot function that sets a random into an item.
 * @author rubensworks
 */
public class LootFunctionSetRandomAbility extends LootFunction {

    public LootFunctionSetRandomAbility(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        Rarity rarity = AbilityHelpers.getRandomRarity(context.getRandom());
        IAbilityType abilityType = AbilityHelpers.getRandomAbility(context.getRandom(), rarity).get(); // Should always be present, as the method above guarantees that

        stack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                .ifPresent(mutableAbilityStore -> mutableAbilityStore.addAbility(new Ability(abilityType, 1), true));
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<LootFunctionSetRandomAbility> {
        public Serializer() {
            super(new ResourceLocation(Reference.MOD_ID, "set_random_ability"), LootFunctionSetRandomAbility.class);
        }

        @Override
        public void serialize(JsonObject object, LootFunctionSetRandomAbility functionClazz, JsonSerializationContext serializationContext) {

        }

        @Override
        public LootFunctionSetRandomAbility deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new LootFunctionSetRandomAbility(conditionsIn);
        }
    }

}
