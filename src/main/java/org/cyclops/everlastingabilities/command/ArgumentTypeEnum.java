package org.cyclops.everlastingabilities.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * An argument type for a certain enum.
 * TODO: move to cyclopscore
 * @author rubensworks
 */
public class ArgumentTypeEnum<T extends Enum<T>> implements ArgumentType<T> {

    private final Class<T> enumClass;

    public ArgumentTypeEnum(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        try {
            return Enum.valueOf(this.enumClass, reader.getString().toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(new StringTextComponent("Unknown value")).create();
        }
    }

    @Override
    public Collection<String> getExamples() {
        // Alternative to SharedSecrets.getJavaLangAccess().getEnumConstantsShared(this.enumClass)
        T[] values = null;
        try {
            Method method = this.enumClass.getDeclaredMethod("values");
            values = (T[]) method.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Arrays.stream(values)
                .map(Enum::name)
                .map(name -> name.toLowerCase(Locale.ENGLISH))
                .collect(Collectors.toList());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(getExamples(), builder);
    }

    public static <T extends Enum<T>> T getValue(CommandContext<CommandSource> context, String name, Class<T> enumClass) {
        return context.getArgument(name, enumClass);
    }

}
