package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

@Aliases("take")
@Permission("teslacrate.command.key.take.base")
public final class Take extends Command {

    @Inject
    private Take(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate key take ", "Takes a number of keys from a user.", CmdUtils.USER_ARG, CmdUtils.KEY_ARG, CmdUtils.QUANTITY_ARG))
                .elements(CmdUtils.USER_ELEM, CmdUtils.KEY_ELEM, CmdUtils.QUANTITY_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("user").get();
        Key key = args.<Key>getOne("key").get();
        Integer quantity = args.<Integer>getOne("quantity").get();
        if (key.get(user) < quantity) {
            throw new CommandException(getMessage(src, "not-enough", user, key, quantity));
        } else if (!key.take(user, quantity)) {
            throw new CommandException(getMessage(src, "failure", user, key, quantity));
        }
        src.sendMessage(getMessage(src, "success", user, key, quantity));
        return CommandResult.success();
    }

    private static Text getMessage(CommandSource src, String name, User user, Key key, Integer quantity) {
        return TeslaCrate.getMessage(src, "teslacrate.command.key.take." + name, "user", user.getName(), "key", key.getId(), "quantity", quantity);
    }

}
