package com.mcsimonflash.sponge.teslacrate.command.reward.edit;

import com.google.common.collect.Range;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Reward;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Weight implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new com.mcsimonflash.sponge.teslacrate.command.reward.edit.Description())
            .arguments(Arguments.map("reward", Storage.rewards, Arguments.string("string")),
                    Arguments.range("weight", Range.greaterThan(0.0), Arguments.doublee("double")))
            .permission("teslacrate.command.reward.edit.weight.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Reward reward = args.<Reward>getOne("reward").get();
        Double newWeight = args.<Double>getOne("weight").get();
        Double oldWeight = reward.getWeight();
        reward.setWeight(newWeight);
        TeslaCrate.sendMessage(src, "teslacrate.command.reward.edit.weight.success", "reward", reward.getName(), "new-weight", newWeight, "old-weight", oldWeight);
        return CommandResult.success();
    }

}