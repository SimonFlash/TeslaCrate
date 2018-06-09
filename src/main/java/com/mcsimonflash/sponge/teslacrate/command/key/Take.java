package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.key.Key;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"take"})
@Permission("teslacrate.command.key.take.base")
public final class Take extends Command {

    @Inject
    private Take(Settings settings) {
        super(settings.usage(usage("/teslacrate key take ", "Takes a number of keys from a user.", USER_ARG, KEY_ARG, QUANTITY_ARG)).elements(USER_ELEM, KEY_ELEM, QUANTITY_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("user").get();
        Key key = args.<Key>getOne("key").get();
        Integer quantity = args.<Integer>getOne("quantity").get();
        if (!key.check(user, quantity)) {
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
