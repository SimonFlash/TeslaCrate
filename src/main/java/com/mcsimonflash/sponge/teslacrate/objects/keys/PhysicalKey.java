package com.mcsimonflash.sponge.teslacrate.objects.keys;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;

public class PhysicalKey extends Key {

    public ItemStack Item;

    public PhysicalKey(String name, ItemStack item) {
        super(name);
        Item = item;
    }

    @Override
    public boolean hasKeys(User user, int quantity) {
        return user.isOnline() && user.getInventory().query(Item).size() >= quantity;
    }

    @Override
    public boolean giveKeys(User user, int quantity) {
        return user.isOnline() && !user.getInventory().query(Hotbar.class, GridInventory.class).offer(ItemStack.builder().fromItemStack(Item).quantity(quantity).build()).equals(InventoryTransactionResult.failNoTransactions());
    }

    @Override
    public boolean takeKeys(User user, int quantity) {
        return user.isOnline() && user.getInventory().query(Item).poll(quantity).isPresent();
    }
}
