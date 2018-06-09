package com.mcsimonflash.sponge.teslacrate.command.effect;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"effect"})
@Permission("teslacrate.command.effect.base")
@Children(Run.class)
public final class Effect extends Command {

    @Inject
    private Effect(Settings settings) {
        super(settings.usage(usage("teslacrate effect ", "The base command for effects.", SUBCOMMAND_ARG)));
    }

        @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
