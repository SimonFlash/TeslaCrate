package com.mcsimonflash.sponge.teslacrate.objects.rewards;

import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

public class CompoundReward extends CrateReward {

    public CompoundReward(String display, List<CrateReward> rewards) {
        super(display);
        Rewards = rewards;
    }

    public List<CrateReward> Rewards;

    @Override
    public void processReward(Player player) {
        for (CrateReward crateReward : Rewards) {
            crateReward.processReward(player);
        }

    }
}
