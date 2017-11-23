package com.mcsimonflash.sponge.teslacrate.command.command.edit;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Command implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Command())
            .arguments(Arguments.map("command", Storage.commands, Arguments.string("string")),
                    Arguments.remainingStrings("/command"))
            .permission("teslacrate.command.command.edit.command.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        com.mcsimonflash.sponge.teslacrate.component.Command command = args.<com.mcsimonflash.sponge.teslacrate.component.Command>getOne("command").get();
        String newCommand = args.<String>getOne("/command").get();
        String oldCommand = command.getCommand();
        command.setCommand(newCommand);
        TeslaCrate.sendMessage(src, "teslacrate.command.command.edit.command.success", "command", command.getName(), "new-command", newCommand, "old-command", oldCommand);
        return CommandResult.success();
    }

}