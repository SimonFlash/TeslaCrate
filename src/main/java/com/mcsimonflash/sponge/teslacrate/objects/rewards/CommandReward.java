package com.mcsimonflash.sponge.teslacrate.objects.rewards;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public class CommandReward extends CrateReward {

    public CommandReward(String display, String command) {
        super(display);
        Command = command;
    }

    public String Command;

    @Override
    public void processReward(Player player) {
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), Command.replace("<player>", player.getName()));
    }
}
