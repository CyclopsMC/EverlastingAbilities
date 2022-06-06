package org.cyclops.everlastingabilities.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * An argument type for an ability.
 * @author rubensworks
 */
public class ArgumentTypeAbility implements ArgumentType<IAbilityType> {

    @Override
    public IAbilityType parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        IAbilityType abilityType = AbilityTypes.REGISTRY.getValue(id);
        if (abilityType == null) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("chat.everlastingabilities.command.invalidAbility", id)).create();
        }
        return abilityType;
    }

    @Override
    public Collection<String> getExamples() {
        return AbilityTypes.REGISTRY.getValues().stream()
                .map(IForgeRegistryEntry::getRegistryName)
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(getExamples(), builder);
    }

}