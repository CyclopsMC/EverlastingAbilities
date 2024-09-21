package org.cyclops.everlastingabilities.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.cyclops.cyclopscore.command.argument.ArgumentTypeEnum;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.command.argument.ArgumentTypeAbility;

/**
 * Command for modifying abilities to players.
 * @author rubensworks
 */
public class CommandModifyAbilities implements Command<CommandSourceStack> {

    private final boolean checkAbility;
    private final boolean checkLevel;

    public CommandModifyAbilities(boolean checkAbility, boolean checkLevel) {
        this.checkAbility = checkAbility;
        this.checkLevel = checkLevel;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        Action action = ArgumentTypeEnum.getValue(context, "action", Action.class);
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        IMutableAbilityStore abilityStore = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getEntityAbilityStore(player).get();

        if (action == Action.LIST) {
            sender.sendSystemMessage(abilityStore.getTextComponent());
        } else {
            if (!this.checkAbility) {
                throw new SimpleCommandExceptionType(Component.translatable(
                        "chat.everlastingabilities.command.invalidAbility", "null")).create();
            }
            // Determine the ability
            Holder<IAbilityType> abilityType = context.getArgument("ability", ArgumentTypeAbility.Input.class).abilityType();
            // Determine level to add or remove
            int level = this.checkLevel ? context.getArgument("level", Integer.class) : 1;

            if (action == Action.ADD) {
                level = Math.max(1, Math.min(abilityType.value().getMaxLevelInfinitySafe(), level));
                Ability ability = new Ability(abilityType, level);

                Ability addedAbility = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().addPlayerAbility(player, ability, true, false);
                Ability newAbility = abilityStore.getAbility(abilityType);

                sender.sendSystemMessage(Component.translatable("chat.everlastingabilities.command.addedAbility", addedAbility.getTextComponent(), newAbility.getTextComponent()));
            } else {
                level = Math.max(1, level);
                Ability ability = new Ability(abilityType, level);

                Ability removedAbility = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().removePlayerAbility(player, ability, true, false);
                Ability newAbility = abilityStore.getAbility(abilityType);

                sender.sendSystemMessage(Component.translatable("chat.everlastingabilities.command.removedAbility", removedAbility.getTextComponent(), newAbility.getTextComponent()));
            }
        }
        return 0;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> make(CommandBuildContext context) {
        return Commands.literal("abilities")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("action", new ArgumentTypeEnum<>(Action.class))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(new CommandModifyAbilities(false, false))
                                .then(Commands.argument("ability", new ArgumentTypeAbility(context))
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
