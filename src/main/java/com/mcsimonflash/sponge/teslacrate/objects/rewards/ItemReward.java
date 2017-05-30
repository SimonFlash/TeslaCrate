package com.mcsimonflash.sponge.teslacrate.objects.rewards;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;

public class ItemReward extends CrateReward {

    public ItemReward(String display, ItemStack item) {
        super(display);
        Item = item;
    }

    private ItemStack Item;

    @Override
    public void processReward(Player player) {
        InventoryTransactionResult result = player.getInventory().query(Hotbar.class, GridInventory.class).offer(ItemStack.builder().fromItemStack(Item).build());
        if (result.equals(InventoryTransactionResult.failNoTransactions())) {
            TeslaCrate.getPlugin().getLogger().error("Attempt to give player " + player.getName() + " " + Item.getItem().getId() + " failed!");
        }
    }
}
