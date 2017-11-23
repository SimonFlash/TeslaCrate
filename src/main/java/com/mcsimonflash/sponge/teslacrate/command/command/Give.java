package com.mcsimonflash.sponge.teslacrate.command.command;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.command.command.edit.Value;
import com.mcsimonflash.sponge.teslacrate.component.Command;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class Give implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Value())
            .arguments(GenericArguments.player(Text.of("player")),
                    Arguments.map("command", Storage.commands, Arguments.string("string")),
                    GenericArguments.optional(Arguments.remainingStrings("value")))
            .permission("teslacrate.command.command.give.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        Command command = args.<Command>getOne("command").get();
        String value = args.<String>getOne("value").orElse(command.getValue());
        command.give(player, value);
        TeslaCrate.sendMessage(src, "teslacrate.command.command.give.success", "player", player.getName(), "command", command.getName(), "value", value);
        return CommandResult.success();
    }

}