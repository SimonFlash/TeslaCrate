package com.mcsimonflash.sponge.teslacrate.command.crate.edit.rewards;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
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
            .arguments(Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    Arguments.map("reward", Storage.rewards, Arguments.string("string")))
            .permission("teslacrate.command.crate.edit.rewards.remove.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Crate crate = args.<Crate>getOne("crate").get();
        Reward reward = args.<Reward>getOne("reward").get();
        crate.removeReward(reward);
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.edit.rewards.remove.success", "crate", crate.getName(), "reward", reward.getName());
        return CommandResult.success();
    }

}