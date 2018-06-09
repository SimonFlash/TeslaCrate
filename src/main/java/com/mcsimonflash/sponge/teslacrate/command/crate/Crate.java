package com.mcsimonflash.sponge.teslacrate.command.crate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"crate"})
@Permission("teslacrate.command.crate.base")
@Children(Open.class)
public final class Crate extends Command {

    @Inject
    private Crate(Settings settings) {
        super(settings.usage(usage("teslacrate crate ", "The base command for crates.", SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
