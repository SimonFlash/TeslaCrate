package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Children;
import com.mcsimonflash.sponge.teslalibs.command.Permission;

@Singleton
@Aliases("key")
@Permission("teslacrate.command.key.base")
@Children({Check.class, Give.class, List.class, Take.class})
public class Key extends TeslaCommand {

    @Inject
    private Key() {
        super(CmdUtils.usage("/teslacrate key ", "The base command for keys.", SUBCOMMAND_ARG), settings());
    }

}