package com.mcsimonflash.sponge.teslacrate.command;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.command.crate.Crate;
import com.mcsimonflash.sponge.teslacrate.command.effect.Effect;
import com.mcsimonflash.sponge.teslacrate.command.key.Key;
import com.mcsimonflash.sponge.teslacrate.command.location.Location;
import com.mcsimonflash.sponge.teslacrate.command.prize.Prize;
import com.mcsimonflash.sponge.teslacrate.command.reward.Reward;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Aliases({"teslacrate", "tcrate", "crate", "tc"})
@Permission("teslacrate.command.base")
@Children({Crate.class, Effect.class, Key.class, Location.class, Prize.class, Reward.class, Info.class})
public final class Base extends Command {

    @Inject
    private Base(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate ", "The base command for &eTesla&6Crate&f.", CmdUtils.SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        CmdUtils.getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
