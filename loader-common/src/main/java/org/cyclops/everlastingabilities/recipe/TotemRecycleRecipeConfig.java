package org.cyclops.everlastingabilities.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.cyclops.cyclopscore.config.extendedconfig.RecipeConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;

/**
 * Config for {@link TotemRecycleRecipe}.
 * @author rubensworks
 */
public class TotemRecycleRecipeConfig<M extends IModBase> extends RecipeConfigCommon<TotemRecycleRecipe, M> {

    public TotemRecycleRecipeConfig(M mod) {
        super(mod,
                "crafting_special_totem_recycle",
                eConfig -> new RecipeSerializer<TotemRecycleRecipe>() {
                    private final MapCodec<TotemRecycleRecipe> codec = RecordCodecBuilder.mapCodec(
                            builder -> builder.group(
                                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category)
                            ).apply(builder, TotemRecycleRecipe::new)
                    );
                    private final StreamCodec<RegistryFriendlyByteBuf, TotemRecycleRecipe> streamCodec =
                            StreamCodec.composite(CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category, TotemRecycleRecipe::new);

                    @Override
                    public MapCodec<TotemRecycleRecipe> codec() {
                        return this.codec;
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, TotemRecycleRecipe> streamCodec() {
                        return this.streamCodec;
                    }
                });
    }

}
