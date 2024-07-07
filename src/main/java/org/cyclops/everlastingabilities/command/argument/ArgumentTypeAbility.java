package org.cyclops.everlastingabilities.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * An argument type for an ability.
 * @author rubensworks
 */
public class ArgumentTypeAbility implements ArgumentType<ArgumentTypeAbility.Input> {

    private final CommandBuildContext context;

    public ArgumentTypeAbility(CommandBuildContext context) {
        this.context = context;
    }

    @Override
    public ArgumentTypeAbility.Input parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        Optional<Holder.Reference<IAbilityType>> abilityOptional = AbilityHelpers.getRegistryLookup(this.context).get(ResourceKey.create(AbilityTypes.REGISTRY_KEY, id));
        if (abilityOptional.isEmpty()) {
            throw new SimpleCommandExceptionType(Component.translatable("chat.everlastingabilities.command.invalidAbility", id)).create();
        }
        return new ArgumentTypeAbility.Input(abilityOptional.get());
    }

    @Override
    public Collection<String> getExamples() {
        return AbilityHelpers.getRegistryLookup(this.context).listElementIds()
                .map(k -> k.location().toString())
                .collect(Collectors.toList());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(getExamples(), builder);
    }

    public static record Input(Holder<IAbilityType> abilityType) {}

}
