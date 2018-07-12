package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Aliases({"location", "loc"})
@Permission("teslacrate.command.location.base")
@Children({Delete.class, Set.class})
public final class Location extends Command {

    @Inject
    private Location(Settings settings) {
        super(settings.usage(CmdUtils.usage("teslacrate location ", "The base command for locations.", CmdUtils.SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        CmdUtils.getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
