package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.common.collect.Range;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Key;
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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class Take implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Take())
            .arguments(GenericArguments.user(Text.of("user")),
                    Arguments.map("key", Storage.keys, ValueElement.next()),
                    Arguments.range("quantity", Range.greaterThan(0), Arguments.intt("int")))
            .permission("teslacrate.command.key.take.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("user").get();
        Key key = args.<Key>getOne("key").get();
        Integer quantity = args.<Integer>getOne("quantity").get();
        if (key.check(user) < quantity) {
            if (key.take(user, quantity)) {
                TeslaCrate.sendMessage(src, "teslacrate.command.key.take.success", "user", user.getName(), "key", key.getName(), "quantity", quantity);
                return CommandResult.success();
            } else {
                throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.key.take.failure", "user", user.getName(), "key", key.getName(), "quantity", quantity));
            }
        } else {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.key.take.not-enough", "user", user.getName(), "key", key.getName(), "quantity", quantity));
        }
    }

}