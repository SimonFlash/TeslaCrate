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
@Aliases("check")
@Permission("teslacrate.command.key.check.base")
public class Check extends TeslaCommand {

    @Inject
    private Check() {
        super(CmdUtils.usage("/teslacrate key check ", "Checks the number of keys a user has.", OPTIONAL_USER_ARG, KEY_ARG),
                settings().arguments(USER_OR_SOURCE_ELEM, KEY_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        User user = args.<User>getOne("user").get();
        Key key = args.<Key>getOne("key").get();
        TeslaCrate.sendMessage(src, "teslacrate.command.key.check.success", "user", user.getName(), "key", key.getName(), "quantity", key.check(user));
        return CommandResult.success();
    }

}