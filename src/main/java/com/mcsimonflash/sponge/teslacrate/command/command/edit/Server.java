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

public class Server implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Server())
            .arguments(Arguments.map("command", Storage.commands, Arguments.string("string")),
                    Arguments.booleann("new-server"))
            .permission("teslacrate.command.command.edit.server.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Command command = args.<Command>getOne("command").get();
        Boolean newServer = args.<Boolean>getOne("new-server").get();
        Boolean oldServer = command.isServer();
        command.setServer(newServer);
        TeslaCrate.sendMessage(src, "teslacrate.command.command.edit.server.success", "command", command.getName(), "new-server", newServer, "old-server", oldServer);
        return CommandResult.success();
    }

}