package com.mcsimonflash.sponge.teslacrate.command.reward.edit.items;

import com.google.common.collect.Range;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Item;
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
            .arguments(Arguments.map("reward", Storage.rewards, Arguments.string("string")),
                    Arguments.map("item", Storage.items, Arguments.string("string")),
                    GenericArguments.optional(Arguments.range("quantity", Range.greaterThan(0), Arguments.intt("int"))))
            .permission("teslacrate.command.reward.edit.items.add.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Reward reward = args.<Reward>getOne("reward").get();
        Item item = args.<Item>getOne("item").get();
        Integer quantity = args.<Integer>getOne("quantity").orElse(item.getItem().getQuantity());
        reward.addItem(item, quantity);
        TeslaCrate.sendMessage(src, "teslacrate.command.reward.edit.items.add.success", "reward", reward.getName(), "item", item.getName(), "quantity", quantity);
        return CommandResult.success();
    }

}