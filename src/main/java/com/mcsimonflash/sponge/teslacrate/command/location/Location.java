package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Permission;

@Singleton
@Aliases({"location", "loc"})
@Permission("teslacrate.command.location.base")
@Children({Delete.class, Menu.class, Set.class})
public class Location extends TeslaCommand {

    @Inject
    private Location() {
        super(CmdUtils.usage("/teslacrate location ", "The base command for locations.", SUBCOMMAND_ARG), settings());
    }

}