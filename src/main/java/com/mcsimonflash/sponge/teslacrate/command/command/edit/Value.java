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

public class Value implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Value())
            .arguments(Arguments.map("command", Storage.commands, Arguments.string("string")),
                    Arguments.remainingStrings("new-value"))
            .permission("teslacrate.command.command.edit.value.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Command command = args.<Command>getOne("command").get();
        String newValue = args.<String>getOne("new-value").get();
        String oldValue = command.getValue();
        command.setValue(newValue);
        TeslaCrate.sendMessage(src, "teslacrate.command.command.edit.value.success", "command", command.getName(), "new-value", newValue, "old-value", oldValue);
        return CommandResult.success();
    }

}