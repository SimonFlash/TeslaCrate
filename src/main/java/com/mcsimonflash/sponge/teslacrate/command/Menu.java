package com.mcsimonflash.sponge.teslacrate.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Singleton
@Aliases("menu")
@Permission("teslacrate.command.menu.base")
public class Menu extends TeslaCommand {

    @Inject
    private Menu() {
        super(CmdUtils.usage("/teslacrate menu", "Opens the component menu"), settings());
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Inventory.MENU.open(CmdUtils.requirePlayer(src));
        return CommandResult.success();
    }

}