package org.cyclops.everlastingabilities.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStoreRegistryAccess;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public class LootModifierInjectAbilityTotem extends LootModifier {
    public static final Supplier<Codec<LootModifierInjectAbilityTotem>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).and(
            Codec.list(Codec.STRING).fieldOf("loot_tables").forGetter(LootModifierInjectAbilityTotem::getLootTables)
    ).apply(inst, LootModifierInjectAbilityTotem::new)));

    private final List<String> lootTables;

    public LootModifierInjectAbilityTotem(LootItemCondition[] conditionsIn, List<String> lootTables) {
        super(conditionsIn);
        this.lootTables = lootTables;
    }

    public List<String> getLootTables() {
        return lootTables;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (getLootTables().contains(context.getQueriedLootTableId().toString())) {
            try {
                List<IAbilityType> abilityTypes = AbilityHelpers.getAbilityTypesLoot(AbilityHelpers.getRegistry(context.getLevel().registryAccess()));
                Rarity rarity = AbilityHelpers.getRandomRarity(abilityTypes, context.getRandom());
                IAbilityType abilityType = AbilityHelpers.getRandomAbility(abilityTypes, context.getRandom(), rarity).get(); // Should always be present, as the method above guarantees that

                ItemStack stack = new ItemStack(RegistryEntries.ITEM_ABILITY_TOTEM);
                stack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                        .ifPresent(mutableAbilityStore -> {
                            ((IMutableAbilityStoreRegistryAccess) mutableAbilityStore).setRegistryAccess(context.getLevel().registryAccess());
                            mutableAbilityStore.addAbility(new Ability(abilityType, 1), true);
                        });
                generatedLoot.add(stack);
            } catch (IllegalStateException e) {
                // Do nothing on empty ability registry
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
