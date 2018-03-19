package com.mcsimonflash.sponge.teslacrate.command.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.component.Command;
import com.mcsimonflash.sponge.teslalibs.argument.Arguments;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

@Singleton
@Aliases("give")
@Permission("teslacrate.command.command.give.base")
public class Give extends TeslaCommand {

    @Inject
    private Give() {
        super(CmdUtils.usage("/teslacrate command give ", "Gives this command to the player, executing the command for them.", PLAYER_ARG, COMMAND_ARG, CmdUtils.arg(false, "value", "Value to insert into the command. Defaults to the default value of the command component.")),
                settings().arguments(PLAYER_ELEM, COMMAND_ELEM, Arguments.remainingStrings().optional().toElement("value")));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player player = args.<Player>getOne("player").get();
        Command command = args.<Command>getOne("command").get();
        String value = args.<String>getOne("value").orElse(command.getValue());
        command.give(player, value);
        TeslaCrate.sendMessage(src, "teslacrate.command.command.give.success", "player", player.getName(), "command", command.getName(), "value", value);
        return CommandResult.success();
    }

}