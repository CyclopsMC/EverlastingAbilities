package org.cyclops.everlastingabilities.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.Random;

/**
 * A loot function that sets a random into an item.
 * @author rubensworks
 */
public class LootFunctionSetRandomAbility extends LootFunction {

    public LootFunctionSetRandomAbility(LootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        EnumRarity rarity = AbilityHelpers.getRandomRarity(rand);
        IAbilityType abilityType = AbilityHelpers.getRandomAbility(rand, rarity).get(); // Should always be present, as the method above guarantees that

        IMutableAbilityStore mutableAbilityStore = stack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        mutableAbilityStore.addAbility(new Ability(abilityType, 1), true);
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<LootFunctionSetRandomAbility> {
        public Serializer() {
            super(new ResourceLocation(Reference.MOD_ID, "set_random_ability"), LootFunctionSetRandomAbility.class);
        }

        public void serialize(JsonObject object, LootFunctionSetRandomAbility functionClazz, JsonSerializationContext serializationContext) {

        }

        public LootFunctionSetRandomAbility deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
            return new LootFunctionSetRandomAbility(conditionsIn);
        }
    }

}
