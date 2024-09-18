package org.cyclops.everlastingabilities.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.RegistryEntriesCommon;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public class LootModifierInjectAbilityTotem extends LootModifier {
    public static final Supplier<MapCodec<LootModifierInjectAbilityTotem>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).and(
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
                List<Holder<IAbilityType>> abilityTypes = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAbilityTypesLoot(EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getRegistry(context.getLevel().registryAccess()));
                EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getRandomRarity(abilityTypes, context.getRandom()).ifPresent(rarity -> {
                    Holder<IAbilityType> abilityType = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getRandomAbility(abilityTypes, context.getRandom(), rarity).get(); // Should always be present, as the method above guarantees that

                    ItemStack stack = new ItemStack(RegistryEntriesCommon.ITEM_ABILITY_TOTEM);
                    Optional.ofNullable(stack.getCapability(Capabilities.MutableAbilityStore.ITEM))
                            .ifPresent(mutableAbilityStore -> {
                                mutableAbilityStore.addAbility(new Ability(abilityType, 1), true);
                                generatedLoot.add(stack);
                            });
                });
            } catch (IllegalStateException e) {
                // Do nothing on empty ability registry
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
