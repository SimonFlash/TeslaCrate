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

public class DisplayName implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new DisplayName())
            .arguments(Arguments.map("key", Storage.commands, Arguments.string("string")),
                    Arguments.remainingStrings("new-display-name"))
            .permission("teslacrate.command.key.edit.display-name.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Key key = args.<Key>getOne("key").get();
        String newDisplayName = args.<String>getOne("new-display-name").get();
        String oldDisplayName = key.getDisplayName();
        key.setDisplayName(newDisplayName);
        TeslaCrate.sendMessage(src, "teslacrate.command.key.edit.display-name.success", "key", key.getName(), "new-display-name", newDisplayName, "old-display-name", oldDisplayName);
        return CommandResult.success();
    }

}