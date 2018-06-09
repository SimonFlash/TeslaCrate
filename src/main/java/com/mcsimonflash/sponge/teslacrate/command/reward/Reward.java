package com.mcsimonflash.sponge.teslacrate.command.reward;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"reward"})
@Permission("teslacrate.command.reward.base")
@Children(Give.class)
public final class Reward extends Command {

    @Inject
    private Reward(Settings settings) {
        super(settings.usage(usage("teslacrate reward ", "The base command for reward.", SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
