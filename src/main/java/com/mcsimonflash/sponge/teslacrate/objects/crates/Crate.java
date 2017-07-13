package com.mcsimonflash.sponge.teslacrate.objects.crates;

import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.keys.Key;
import com.mcsimonflash.sponge.teslacrate.objects.rewards.Reward;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;

public class Crate {

    public Crate(String name) {
        Name = name;
    }

    public double ChanceSum;
    public double Cooldown;
    public String Name;
    public String DisplayName;
    public String AnnounceMsg;
    public String PlayerMsg;
    public Map<Key, Integer> Keys = Maps.newHashMap();
    public Map<Reward, Double> Rewards = Maps.newHashMap();

    public void process(Player player) {
        Reward reward = Util.getReward(this);
        if (!reward.DisplayName.isEmpty()) {
            if (!AnnounceMsg.isEmpty() && reward.Announce) {
                Sponge.getServer().getBroadcastChannel().send(Config.displayPrefix.concat(Util.toText(AnnounceMsg.replace("<crate>", DisplayName).replace("<player>", player.getName()).replace("<reward>", reward.DisplayName))));
            } else if (!PlayerMsg.isEmpty()) {
                player.sendMessage(Config.displayPrefix.concat(Util.toText(PlayerMsg.replace("<crate>", DisplayName).replace("<player>", player.getName()).replace("<reward>", reward.DisplayName))));
            }
        }
        reward.process(player);
    }
}
