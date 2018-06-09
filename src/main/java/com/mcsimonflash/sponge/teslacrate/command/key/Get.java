package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.key.Key;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"get"})
@Permission("teslacrate.command.key.get.base")
public final class Get extends Command {

    @Inject
    private Get(Settings settings) {
        super(settings.usage(usage("/teslacrate key get ", "Gets the number of keys a user has.", USER_ARG, KEY_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        User user = args.<User>getOne("user").get();
        Key key = args.<Key>getOne("key").get();
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.key.get.success", "user", user.getName(), "key", key.getId(), "quantity", key.get(user)));
        return CommandResult.success();
    }

}
