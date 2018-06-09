package com.mcsimonflash.sponge.teslacrate.command;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.command.crate.Crate;
import com.mcsimonflash.sponge.teslacrate.command.effect.Effect;
import com.mcsimonflash.sponge.teslacrate.command.key.Key;
import com.mcsimonflash.sponge.teslacrate.command.prize.Prize;
import com.mcsimonflash.sponge.teslacrate.command.reward.Reward;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.SUBCOMMAND_ARG;
import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.getUsages;
import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.usage;

@Aliases({"teslacrate", "tcrate", "crate", "tc"})
@Permission("teslacrate.command.base")
@Children({Crate.class, Effect.class, Key.class, Prize.class, Reward.class})
public final class Base extends Command {

    @Inject
    private Base(Settings settings) {
        super(settings.usage(usage("teslacrate", "The base command for &eTesla&6Crate&f.", SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
