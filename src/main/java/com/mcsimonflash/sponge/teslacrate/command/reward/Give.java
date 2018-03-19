package com.mcsimonflash.sponge.teslacrate.command.reward;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.component.Reward;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

@Singleton
@Aliases("give")
@Permission("teslacrate.command.reward.give.base")
public class Give extends TeslaCommand {

    @Inject
    private Give() {
        super(CmdUtils.usage("/teslacrate reward give ", "Gives a reward to a player.", PLAYER_ARG, REWARD_ARG),
                settings().arguments(PLAYER_ELEM, REWARD_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player player = args.<Player>getOne("player").get();
        Reward reward = args.<Reward>getOne("reward").get();
        reward.give(player);
        TeslaCrate.sendMessage(src, "teslacrate.command.reward.give.success", "player", player.getName(), "reward", reward.getName());
        return CommandResult.success();
    }

}