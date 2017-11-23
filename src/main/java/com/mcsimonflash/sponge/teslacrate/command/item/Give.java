package com.mcsimonflash.sponge.teslacrate.command.item;

import com.google.common.collect.Range;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Item;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import com.mcsimonflash.sponge.teslacore.command.element.core.ValueElement;
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
            .executor(new Give())
            .arguments(GenericArguments.player(Text.of("player")),
                    Arguments.map("item", Storage.items, ValueElement.next()),
                    GenericArguments.optional(Arguments.range("quantity", Range.greaterThan(0), Arguments.intt("int"))))
            .permission("teslacrate.command.item.give.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        Item item = args.<Item>getOne("item").get();
        Integer quantity = args.<Integer>getOne("quantity").orElse(item.getItem().getQuantity());
        item.give(player, quantity);
        TeslaCrate.sendMessage(src, "teslacrate.command.item.give.success", "player", player.getName(), "item", item.getName(), "quantity", quantity);
        return CommandResult.success();
    }

}