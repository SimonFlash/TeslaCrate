package com.mcsimonflash.sponge.teslacrate.command.crate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Aliases({"crate"})
@Permission("teslacrate.command.crate.base")
@Children(Open.class)
public final class Crate extends Command {

    @Inject
    private Crate(Settings settings) {
        super(settings.usage(CmdUtils.usage("teslacrate crate ", "The base command for crates.", CmdUtils.SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        CmdUtils.getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
