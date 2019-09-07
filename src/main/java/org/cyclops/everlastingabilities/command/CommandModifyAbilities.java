package org.cyclops.everlastingabilities.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

/**
 * Command for modifying abilities to players.
 * @author rubensworks
 */
public class CommandModifyAbilities implements Command<CommandSource> {

    private final boolean checkAbility;
    private final boolean checkLevel;

    public CommandModifyAbilities(boolean checkAbility, boolean checkLevel) {
        this.checkAbility = checkAbility;
        this.checkLevel = checkLevel;
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sender = context.getSource().asPlayer();
        Action action = ArgumentTypeEnum.getValue(context, "action", Action.class);
        ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
        IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).orElse(null);

        if (action == Action.LIST) {
            sender.sendMessage(new StringTextComponent(abilityStore.getAbilities().toString()));
        } else {
            if (!this.checkAbility) {
                throw new SimpleCommandExceptionType(new TranslationTextComponent(
                        "chat.everlastingabilities.command.invalidAbility", "null")).create();
            }
            // Determine the ability
            IAbilityType abilityType = context.getArgument("ability", IAbilityType.class);
            // Determine level to add or remove
            int level = this.checkLevel ? context.getArgument("level", Integer.class) : 1;

            if (action == Action.ADD) {
                level = Math.max(1, Math.min(abilityType.getMaxLevelInfinitySafe(), level));
                Ability ability = new Ability(abilityType, level);

                Ability addedAbility = AbilityHelpers.addPlayerAbility(player, ability, true, false);
                Ability newAbility = abilityStore.getAbility(abilityType);

                sender.sendMessage(new TranslationTextComponent("chat.everlastingabilities.command.addedAbility", addedAbility, newAbility));
            } else {
                level = Math.max(1, level);
                Ability ability = new Ability(abilityType, level);

                Ability removedAbility = AbilityHelpers.removePlayerAbility(player, ability, true, false);
                Ability newAbility = abilityStore.getAbility(abilityType);

                sender.sendMessage(new TranslationTextComponent("chat.everlastingabilities.command.removedAbility", removedAbility, newAbility));
            }
        }
        return 0;
    }

    public static LiteralArgumentBuilder<CommandSource> make() {
        return Commands.literal("abilities")
                .requires((commandSource) -> commandSource.hasPermissionLevel(2))
                .then(Commands.argument("action", new ArgumentTypeEnum<>(Action.class))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(new CommandModifyAbilities(false, false))
                                .then(Commands.argument("ability", new ArgumentTypeAbility())
                                        .executes(new CommandModifyAbilities(true, false))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                                .executes(new CommandModifyAbilities(true, true))))));
    }

    public static enum Action {
        ADD,
        REMOVE,
        LIST
    }

}
