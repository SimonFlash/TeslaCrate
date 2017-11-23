package com.mcsimonflash.sponge.teslacrate.command.command.edit;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Command;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Description implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Description())
            .arguments(Arguments.map("command", Storage.commands, Arguments.string("string")),
                    Arguments.remainingStrings("new-description"))
            .permission("teslacrate.command.command.edit.description.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Command command = args.<Command>getOne("command").get();
        String newDescription = args.<String>getOne("new-description").get();
        String oldDescription = command.getDescription();
        command.setDescription(newDescription);
        TeslaCrate.sendMessage(src, "teslacrate.command.command.edit.description.success", "command", command.getName(), "new-description", newDescription, "old-description", oldDescription);
        return CommandResult.success();
    }

}