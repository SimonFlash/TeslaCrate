package com.mcsimonflash.sponge.teslacrate.command.crate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Permission;

@Singleton
@Aliases("crate")
@Permission("teslacrate.command.crate.base")
@Children({Give.class, Preview.class})
public class Crate extends TeslaCommand {

    @Inject
    private Crate() {
        super(CmdUtils.usage("/teslacrate crate ", "The base command for crates.", SUBCOMMAND_ARG), settings());
    }

}