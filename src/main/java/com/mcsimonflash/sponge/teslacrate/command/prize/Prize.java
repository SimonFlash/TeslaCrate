package com.mcsimonflash.sponge.teslacrate.command.prize;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"prize"})
@Permission("teslacrate.command.prize.base")
@Children(Give.class)
public final class Prize extends Command {

    @Inject
    private Prize(Settings settings) {
        super(settings.usage(usage("teslacrate prize ", "The base command for prizes.", SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
