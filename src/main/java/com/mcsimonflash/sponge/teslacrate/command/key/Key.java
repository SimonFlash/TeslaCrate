package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Aliases("key")
@Permission("teslacrate.command.key.base")
@Children({Check.class, Give.class, Take.class})
public final class Key extends Command {

    @Inject
    private Key(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate key ", "The base command for keys.")));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        CmdUtils.getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
