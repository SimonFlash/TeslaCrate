package com.mcsimonflash.sponge.teslacrate.command.effect;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Aliases({"effect"})
@Permission("teslacrate.command.effect.base")
@Children(Run.class)
public final class Effect extends Command {

    @Inject
    private Effect(Settings settings) {
        super(settings.usage(CmdUtils.usage("teslacrate effect ", "The base command for effects.", CmdUtils.SUBCOMMAND_ARG)));
    }

        @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        CmdUtils.getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
