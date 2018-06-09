package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"key"})
@Permission("teslacrate.command.key.base")
@Children({Get.class, Give.class, Take.class})
public final class Key extends Command {

    @Inject
    private Key(Settings settings) {
        super(settings.usage(usage("teslacrate key ", "The base command for keys.", SUBCOMMAND_ARG)));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        getUsages(this).sendTo(src);
        return CommandResult.success();
    }

}
