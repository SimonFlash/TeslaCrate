package com.mcsimonflash.sponge.teslacrate.command.item;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Permission;

@Singleton
@Aliases("item")
@Permission("teslacrate.command.item.base")
@Children(Give.class)
public class Item extends TeslaCommand {

    @Inject
    private Item() {
        super(CmdUtils.usage("/teslacrate item ", "The base command for items.", SUBCOMMAND_ARG), settings());
    }

}