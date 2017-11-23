package com.mcsimonflash.sponge.teslacrate.command.reward.edit.items;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Item;
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
                    Arguments.map("item", Storage.items, Arguments.string("string")))
            .permission("teslacrate.command.reward.edit.items.remove.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Reward reward = args.<Reward>getOne("reward").get();
        Item item = args.<Item>getOne("item").get();
        reward.removeItem(item);
        TeslaCrate.sendMessage(src, "teslacrate.command.reward.edit.items.remove.success", "reward", reward.getName(), "item", item.getName());
        return CommandResult.success();
    }

}