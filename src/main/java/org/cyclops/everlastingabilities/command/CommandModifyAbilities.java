package org.cyclops.everlastingabilities.command;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.cyclops.cyclopscore.command.CommandMod;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.ability.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Command for modifying abilities to players.
 * @author rubensworks
 *
 */
public class CommandModifyAbilities extends CommandMod {

    public static final String NAME = "abilities";

    public CommandModifyAbilities(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public String getFullCommand() {
        return super.getFullCommand() + " " + NAME;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] parts, BlockPos blockPos) {
        if (parts.length <= 1) {
            return Lists.newArrayList("add", "remove", "list");
        } else if (parts.length <= 2) {
            return CommandBase.getListOfStringsMatchingLastWord(parts, FMLCommonHandler.instance().getMinecraftServerInstance().getOnlinePlayerNames());
        } else if (parts.length <= 3 && ("add".equals(parts[0]) || "remove".equals(parts[0]))) {
            List<String> abilityIds = Lists.newArrayList();
            Collection<IAbilityType> abilityTypes = AbilityTypes.REGISTRY.getAbilityTypes();
            for (IAbilityType abilityType : abilityTypes) {
                abilityIds.add(abilityType.getUnlocalizedName());
            }
            return abilityIds;
        }
        return Collections.emptyList();
    }

    @Override
    public void processCommandHelp(ICommandSender icommandsender, String[] astring) throws CommandException {
        throw new WrongUsageException("/" + getFullCommand() + " <add|remove|list> <player> <abilityid> <level>");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] parts) throws CommandException {
        if (parts.length < 2 || parts[0].length() == 0) {
            processCommandHelp(sender, parts);
        } else {
            MinecraftServer minecraftserver = FMLCommonHandler.instance().getMinecraftServerInstance();
            GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(parts[1]);

            if (gameprofile == null) {
                sender.sendMessage(new TextComponentString(L10NHelpers.localize("chat.cyclopscore.command.invalidPlayer", parts[1])));
            } else {
                EntityPlayerMP player = minecraftserver.getPlayerList().getPlayerByUsername(parts[1]);
                IMutableAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);

                String command = parts[0];
                if ("list".equals(command)) {
                    sender.sendMessage(new TextComponentString(abilityStore.getAbilities().toString()));
                } else if ("add".equals(command) || "remove".equals(command)) {
                    if (parts.length >= 3 && parts.length < 5) {
                        String unlocalizedAbilityName = parts[2];
                        IAbilityType abilityType = AbilityTypes.REGISTRY.getAbilityType(unlocalizedAbilityName);
                        int level = 1;
                        if (parts.length > 3) {
                            try {
                                level = Integer.parseInt(parts[3]);
                            } catch (NumberFormatException e) {
                                // Invalid duration amount, ignore.
                            }
                        }
                        if (abilityType == null) {
                            sender.sendMessage(new TextComponentString(L10NHelpers.localize("chat.everlastingabilities.command.invalidAbility", abilityType)));
                        } else {
                            if ("add".equals(command)) {
                                level = Math.max(1, Math.min(abilityType.getMaxLevelInfinitySafe(), level));
                                Ability ability = new Ability(abilityType, level);

                                Ability addedAbility = AbilityHelpers.addPlayerAbility(player, ability, true, false);
                                Ability newAbility = abilityStore.getAbility(abilityType);

                                sender.sendMessage(new TextComponentString(L10NHelpers.localize("chat.everlastingabilities.command.addedAbility", addedAbility, newAbility)));
                            } else {
                                level = Math.max(1, level);
                                Ability ability = new Ability(abilityType, level);

                                Ability removedAbility = AbilityHelpers.removePlayerAbility(player, ability, true, false);
                                Ability newAbility = abilityStore.getAbility(abilityType);

                                sender.sendMessage(new TextComponentString(L10NHelpers.localize("chat.everlastingabilities.command.removedAbility", removedAbility, newAbility)));
                            }
                        }
                    }
                } else {
                    processCommandHelp(sender, parts);
                }
            }
        }
    }
}
