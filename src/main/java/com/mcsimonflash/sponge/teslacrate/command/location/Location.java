package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"location"})
@Permission("teslacrate.command.location.base")
@Children({Delete.class, Set.class})
public final class Location extends Command {

    @Inject
    private Location(Settings settings) {
        super(settings.usage(usage("teslacrate location ", "The base command for locations.", SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
