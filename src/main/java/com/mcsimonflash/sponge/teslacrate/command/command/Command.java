package com.mcsimonflash.sponge.teslacrate.command.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Permission;

@Singleton
@Aliases("command")
@Permission("teslacrate.command.command.base")
@Children(Give.class)
public class Command extends TeslaCommand {

    @Inject
    private Command() {
        super(CmdUtils.usage("/teslacrate command ", "The base command for commands.", SUBCOMMAND_ARG), settings());
    }

}