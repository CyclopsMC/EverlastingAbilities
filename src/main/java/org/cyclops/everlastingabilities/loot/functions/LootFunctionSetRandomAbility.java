package org.cyclops.everlastingabilities.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.cyclops.cyclopscore.helper.LootHelpers;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStoreRegistryAccess;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.List;

/**
 * A loot function that sets a random into an item.
 * @author rubensworks
 */
@Deprecated // Deprecated in favor of LootModifierInjectAbilityTotem
public class LootFunctionSetRandomAbility extends LootItemConditionalFunction {
    public static final LootItemFunctionType TYPE = LootHelpers.registerFunction(new ResourceLocation(Reference.MOD_ID, "set_random_ability"), new LootFunctionSetRandomAbility.Serializer());

    public LootFunctionSetRandomAbility(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        try {
            List<IAbilityType> abilityTypes = AbilityHelpers.getAbilityTypesLoot(AbilityHelpers.getRegistry(context.getLevel().registryAccess()));
            AbilityHelpers.getRandomRarity(abilityTypes, context.getRandom())
                    .ifPresent(rarity -> {
                        IAbilityType abilityType = AbilityHelpers.getRandomAbility(abilityTypes, context.getRandom(), rarity).get(); // Should always be present, as the method above guarantees that

                        stack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                                .ifPresent(mutableAbilityStore -> {
                                    ((IMutableAbilityStoreRegistryAccess) mutableAbilityStore).setRegistryAccess(context.getLevel().registryAccess());
                                    mutableAbilityStore.addAbility(new Ability(abilityType, 1), true);
                                });
                    });
            return stack;
        } catch (IllegalStateException e) {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public LootItemFunctionType getType() {
        return TYPE;
    }

    public static void load() {
        // Dummy call, to enforce class loading
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<LootFunctionSetRandomAbility> {
        @Override
        public void serialize(JsonObject object, LootFunctionSetRandomAbility functionClazz, JsonSerializationContext serializationContext) {

        }

        @Override
        public LootFunctionSetRandomAbility deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
            return new LootFunctionSetRandomAbility(conditionsIn);
        }
    }

}
