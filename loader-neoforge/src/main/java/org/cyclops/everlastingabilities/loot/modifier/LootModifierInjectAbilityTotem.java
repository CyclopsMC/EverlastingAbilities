package org.cyclops.everlastingabilities.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
            EverlastingAbilitiesInstance.MOD.getAbilityHelpers().injectLootTotem(generatedLoot::add, context);
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
