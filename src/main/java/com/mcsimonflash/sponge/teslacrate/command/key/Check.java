package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;

@Aliases("check")
@Permission("teslacrate.command.key.check.base")
public final class Check extends Command {

    @Inject
    private Check(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate key check ", "Gets the number of keys a user has.", CmdUtils.OPT_USER_ARG, CmdUtils.KEY_ARG))
                .elements(CmdUtils.OPT_USER_ELEM, CmdUtils.KEY_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        User user = args.<User>getOne("user").get();
        Key key = args.<Key>getOne("key").get();
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.key.check.success", "user", user.getName(), "key", key.getId(), "quantity", key.get(user)));
        return CommandResult.success();
    }

}
