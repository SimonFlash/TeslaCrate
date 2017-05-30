package com.mcsimonflash.sponge.teslacrate.objects;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.rewards.CrateReward;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Map;

public class Crate {

    public Crate(String name, String display, Map<CrateReward, Double> rewards, CrateKeydata keydata, CrateMetadata metadata) {
        Name = name;
        Display = display;
        Rewards = rewards;
        Keydata = keydata;
        Metadata = metadata;
    }

    public String Name;
    public String Display;
    public CrateKeydata Keydata;
    public CrateMetadata Metadata;
    public Map<CrateReward, Double> Rewards;

    public void process(Player player) {
        if (Keydata.Physical) {
            if (player.getInventory().query(Keydata.Item).poll(1).orElse(null) == null) {
                TeslaCrate.getPlugin().getLogger().error("Unable to retrieve key! | Crate:[" + Name + "] Player:[" + player.getName() + "]");
            }
        } else {
            Config.setKeys(player, this, Config.getKeys(player, this) - 1);
        }
        double randSelec = Metadata.ChanceSum * Math.random();
        for (CrateReward reward : Rewards.keySet()) {
            randSelec -= Rewards.get(reward);
            if (randSelec <= 0) {
                reward.processReward(player);
                if (!reward.Display.isEmpty()) {
                    if (!Metadata.MsgPlayer.isEmpty()) {
                        player.sendMessage(Util.prefix.concat(Util.toText(Metadata.MsgPlayer.replace("<crate>", Display).replace("<player>", player.getName()).replace("<reward>", reward.Display))));
                    }
                    if (!Metadata.MsgAnnounce.isEmpty()) {
                        Sponge.getServer().getBroadcastChannel().send(Util.prefix.concat(Util.toText(Metadata.MsgAnnounce.replace("<crate>", Display).replace("<player>", player.getName()).replace("<reward>", reward.Display))));
                    }
                }
                return;
            }
        }
        player.sendMessage(Util.toText("&cAn unexpected error occured! Error code 5417 | Crate:[" + Name + "]"));
        TeslaCrate.getPlugin().getLogger().error("Reached end of crate process without selection! Error code 5417 | Crate:[" + Name + "]");
    }
}
