package com.mcsimonflash.sponge.teslacrate.command.key;

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

public class Check implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Check())
            .arguments(GenericArguments.user(Text.of("user")),
                    Arguments.map("key", Storage.keys, ValueElement.next()))
            .permission("teslacrate.command.key.check.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("user").get();
        Key key = args.<Key>getOne("key").get();
        TeslaCrate.sendMessage(src, "teslacrate.command.key.check.success", "user", user.getName(), "key", key.getName(), "quantity", key.check(user));
        return CommandResult.success();
    }

}