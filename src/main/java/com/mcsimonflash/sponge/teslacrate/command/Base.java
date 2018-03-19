package com.mcsimonflash.sponge.teslacrate.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.command.Command;
import com.mcsimonflash.sponge.teslacrate.command.crate.Crate;
import com.mcsimonflash.sponge.teslacrate.command.item.Item;
import com.mcsimonflash.sponge.teslacrate.command.key.Key;
import com.mcsimonflash.sponge.teslacrate.command.location.Location;
import com.mcsimonflash.sponge.teslacrate.command.reward.Reward;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Permission;

@Singleton
@Aliases({"teslacrate", "crate", "tc"})
@Permission("teslacrate.command.base")
@Children({Command.class, Crate.class, Item.class, Key.class, Location.class, Reward.class, Menu.class})
public class Base extends TeslaCommand {

    @Inject
    private Base() {
        super(CmdUtils.usage("/teslacrate ", "The base command for TeslaCrate.", SUBCOMMAND_ARG), settings());
    }

}