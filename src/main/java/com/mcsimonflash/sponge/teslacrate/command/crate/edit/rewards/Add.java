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
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Add implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Add())
            .arguments(Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    Arguments.map("reward", Storage.rewards, Arguments.string("string")),
                    GenericArguments.optional(Arguments.doublee("weight")))
            .permission("teslacrate.command.crate.edit.rewards.add.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Crate crate = args.<Crate>getOne("crate").get();
        Reward reward = args.<Reward>getOne("reward").get();
        Double weight = args.<Double>getOne("weight").orElse(reward.getWeight());
        crate.addReward(reward, weight);
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.edit.rewards.add.success", "crate", crate.getName(), "reward", reward.getName(), "weight", weight);
        return CommandResult.success();
    }

}