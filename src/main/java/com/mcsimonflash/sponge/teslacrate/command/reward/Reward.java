package com.mcsimonflash.sponge.teslacrate.command.reward;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Permission;

@Singleton
@Aliases("reward")
@Permission("teslacrate.command.reward.base")
@Children(Give.class)
public class Reward extends TeslaCommand {

    @Inject
    private Reward() {
        super(CmdUtils.usage("/teslacrate reward ", "The base command for rewards.", SUBCOMMAND_ARG), settings());
    }

}