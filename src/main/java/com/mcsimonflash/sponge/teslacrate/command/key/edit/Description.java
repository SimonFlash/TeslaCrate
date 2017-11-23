package com.mcsimonflash.sponge.teslacrate.command.key.edit;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Key;
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
            .arguments(Arguments.map("key", Storage.keys, Arguments.string("string")),
                    Arguments.remainingStrings("new-description"))
            .permission("teslacrate.command.key.edit.description.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Key key = args.<Key>getOne("key").get();
        String newDescription = args.<String>getOne("new-description").get();
        String oldDescription = key.getDescription();
        key.setDescription(newDescription);
        TeslaCrate.sendMessage(src, "teslacrate.command.key.edit.description.success", "key", key.getName(), "new-description", newDescription, "old-description", oldDescription);
        return CommandResult.success();
    }

}