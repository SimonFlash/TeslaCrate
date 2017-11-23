package com.mcsimonflash.sponge.teslacrate.command.reward.edit.commands;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Command;
import com.mcsimonflash.sponge.teslacrate.component.Reward;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Remove implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Remove())
            .arguments(Arguments.map("reward", Storage.rewards, Arguments.string("string")),
                    Arguments.map("command", Storage.commands, Arguments.string("string")))
            .permission("teslacrate.command.reward.edit.commands.remove.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Reward reward = args.<Reward>getOne("reward").get();
        Command command = args.<Command>getOne("command").get();
        reward.removeCommand(command);
        TeslaCrate.sendMessage(src, "teslacrate.command.reward.edit.commands.remove.success", "reward", reward.getName(), "command", command.getName());
        return CommandResult.success();
    }

}