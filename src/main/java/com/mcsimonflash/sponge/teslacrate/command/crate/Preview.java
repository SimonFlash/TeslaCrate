package com.mcsimonflash.sponge.teslacrate.command.crate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Singleton
@Aliases("preview")
@Permission("teslacrate.command.crate.preview.base")
public class Preview extends TeslaCommand {

    @Inject
    private Preview() {
        super(CmdUtils.usage("/teslacrate crate preview ", "Previews the rewards in a crate.", CRATE_ARG),
                settings().arguments(CRATE_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.<Crate>getOne("crate").get().preview(CmdUtils.requirePlayer(src));
        return CommandResult.success();
    }

}