package com.mcsimonflash.sponge.teslacrate.command.key.edit;

import com.google.common.collect.Range;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Key;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Quantity implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Quantity())
            .arguments(Arguments.map("key", Storage.commands, Arguments.string("string")),
                    Arguments.range("new-quantity", Range.greaterThan(0), Arguments.intt("int")))
            .permission("teslacrate.command.key.edit.quantity.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Key key = args.<Key>getOne("key").get();
        Integer newQuantity = args.<Integer>getOne("new-quantity").get();
        Integer oldQuantity = key.getQuantity();
        key.setQuantity(newQuantity);
        TeslaCrate.sendMessage(src, "teslacrate.command.key.edit.quantity.success", "key", key.getName(), "new-quantity", newQuantity, "old-quantity", oldQuantity);
        return CommandResult.success();
    }

}