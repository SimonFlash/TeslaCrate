package com.mcsimonflash.sponge.teslacrate.command.reward;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Aliases({"reward"})
@Permission("teslacrate.command.reward.base")
@Children(Give.class)
public final class Reward extends Command {

    @Inject
    private Reward(Settings settings) {
        super(settings.usage(CmdUtils.usage("teslacrate reward ", "The base command for reward.", CmdUtils.SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        CmdUtils.getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
