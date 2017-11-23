package com.mcsimonflash.sponge.teslacrate.command.crate.edit;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Message implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Message())
            .arguments(Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    Arguments.remainingStrings("new-message"))
            .permission("teslacrate.command.crate.edit.message.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Crate crate = args.<Crate>getOne("crate").get();
        String newMessage = args.<String>getOne("new-message").get();
        String oldMessage = crate.getMessage();
        crate.setMessage(newMessage);
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.edit.message.success", "crate", crate.getName(), "new-message", newMessage, "old-message", oldMessage);
        return CommandResult.success();
    }

}