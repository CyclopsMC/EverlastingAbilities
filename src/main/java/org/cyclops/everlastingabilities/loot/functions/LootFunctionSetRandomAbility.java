package org.cyclops.everlastingabilities.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.conditions.ILootCondition;
import org.cyclops.cyclopscore.helper.LootHelpers;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.List;

/**
 * A loot function that sets a random into an item.
 * @author rubensworks
 */
public class LootFunctionSetRandomAbility extends LootFunction {
    public static final LootFunctionType TYPE = LootHelpers.registerFunction(new ResourceLocation(Reference.MOD_ID, "set_random_ability"), new LootFunctionSetRandomAbility.Serializer());

    public LootFunctionSetRandomAbility(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        List<IAbilityType> abilityTypes = AbilityHelpers.getAbilityTypesLoot();
        Rarity rarity = AbilityHelpers.getRandomRarity(abilityTypes, context.getRandom());
        IAbilityType abilityType = AbilityHelpers.getRandomAbility(abilityTypes, context.getRandom(), rarity).get(); // Should always be present, as the method above guarantees that

        stack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                .ifPresent(mutableAbilityStore -> mutableAbilityStore.addAbility(new Ability(abilityType, 1), true));
        return stack;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return TYPE;
    }

    public static void load() {
        // Dummy call, to enforce class loading
    }

    public static class Serializer extends LootFunction.Serializer<LootFunctionSetRandomAbility> {
        @Override
        public void serialize(JsonObject object, LootFunctionSetRandomAbility functionClazz, JsonSerializationContext serializationContext) {

        }

        @Override
        public LootFunctionSetRandomAbility deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new LootFunctionSetRandomAbility(conditionsIn);
        }
    }

}
