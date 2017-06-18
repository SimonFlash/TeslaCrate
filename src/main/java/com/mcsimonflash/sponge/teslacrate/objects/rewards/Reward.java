package com.mcsimonflash.sponge.teslacrate.objects.rewards;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;

import java.util.List;

public class Reward {

    public boolean Announce;
    public String Name;
    public String DisplayName;
    public List<String> Commands = Lists.newArrayList();
    public List<ItemStack> Items = Lists.newArrayList();

    public Reward(String name) {
        Name = name;
    }

    public void process(Player player) {
        Commands.forEach(c -> Sponge.getCommandManager().process(Sponge.getServer().getConsole(), c.replace("<player>", player.getName())));
        Items.forEach(i -> {
            InventoryTransactionResult result = player.getInventory().query(Hotbar.class, GridInventory.class).offer(ItemStack.builder().fromItemStack(i).build());
            if (result.equals(InventoryTransactionResult.failNoTransactions())) {
                TeslaCrate.getPlugin().getLogger().error("Attempt to give player " + player.getName() + " " + i + " failed! Name:[" + Name + "]");
            }
        });
    }
}
