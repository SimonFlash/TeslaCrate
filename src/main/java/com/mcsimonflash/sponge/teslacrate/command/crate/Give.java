package com.mcsimonflash.sponge.teslacrate.command.crate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacrate.component.Reward;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

@Singleton
@Aliases("give")
@Permission("teslacrate.command.crate.give.base")
public class Give extends TeslaCommand {

    @Inject
    private Give() {
        super(CmdUtils.usage("/teslacrate crate give ", "Gives a crate to a player.", PLAYER_ARG, CRATE_ARG, CmdUtils.arg(false, "reward", "Name of a reward to give through this crate. Defaults to a selection from the crate's rewards.")),
                settings().arguments(PLAYER_ELEM, CRATE_ELEM, REWARD_ELEM.getParser().optional().toElement("reward")));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player player = args.<Player>getOne("player").get();
        Crate crate = args.<Crate>getOne("crate").get();
        Reward reward = args.<Reward>getOne("reward").orElse(crate.getRandomReward());
        crate.give(player, reward, player.getLocation());
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.give.success", "player", player.getName(), "crate", crate.getName());
        return CommandResult.success();
    }

}