package com.mcsimonflash.sponge.teslacrate.command;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Aliases("menu")
@Permission("teslacrate.command.menu.base")
public final class Menu extends Command {

    @Inject
    private Menu(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate menu", "Opens the TeslaCrate menu.")));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Inventory.openMenu(CmdUtils.requirePlayer(src));
        return CommandResult.success();
    }

}
