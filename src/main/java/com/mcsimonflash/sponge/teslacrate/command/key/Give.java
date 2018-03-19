package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.component.Key;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;

@Singleton
@Aliases("give")
@Permission("teslacrate.command.key.give.base")
public class Give extends TeslaCommand {

    @Inject
    private Give() {
        super(CmdUtils.usage("/teslacrate key give ", "Gives a number of keys to a user.", USER_ARG, KEY_ARG, QUANTITY_ARG),
                settings().arguments(USER_ELEM, KEY_ELEM, QUANTITY_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("user").get();
        Key key = args.<Key>getOne("key").get();
        Integer quantity = args.<Integer>getOne("quantity").get();
        if (key.give(user, quantity)) {
            TeslaCrate.sendMessage(src, "teslacrate.command.key.give.success", "user", user.getName(), "key", key.getName(), "quantity", quantity);
            return CommandResult.success();
        } else {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.key.give.failure", "user", user.getName(), "key", key.getDisplayName(), "quantity", quantity));
        }
    }

}