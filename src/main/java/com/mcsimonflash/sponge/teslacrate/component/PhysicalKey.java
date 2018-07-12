package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryTransformations;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

public final class PhysicalKey extends Key {

    public static final Type<PhysicalKey, Integer> TYPE = new Type<>("Physical", PhysicalKey::new, n -> !n.getNode("item").isVirtual(), TeslaCrate.get().getContainer());

    private ItemStackSnapshot item = ItemStackSnapshot.NONE;

    private PhysicalKey(String id) {
        super(id);
    }

    public final ItemStackSnapshot getItem() {
        return item;
    }

    public final void setItem(ItemStackSnapshot item) {
        this.item = ItemStack.builder()
                .fromContainer(item.toContainer().set(DataQuery.of("UnsafeData", "TeslaCrate", "Key"), getId()))
                .build().createSnapshot();
    }

    @Override
    public final int get(User user) {
        return user.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item.createStack())).totalItems();
    }

    @Override
    public final boolean check(User user, int quantity) {
        return get(user) >= quantity;
    }

    @Override
    public final boolean give(User user, int quantity) {
        ItemStack stack = item.createStack();
        stack.setQuantity(quantity);
        return user.getInventory().transform(InventoryTransformations.PLAYER_MAIN_HOTBAR_FIRST).offer(stack).getType().equals(InventoryTransactionResult.Type.SUCCESS);
    }

    @Override
    public final boolean take(User user, int quantity) {
        Inventory inv = user.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item.createStack()));
        return inv.totalItems() >= quantity && inv.poll(quantity).isPresent();
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        setItem(Serializers.deserializeItem(node.getNode("item")));
        if (getDisplayItem() == ItemStackSnapshot.NONE) {
            setDisplayItem(getItem());
        }
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent).add(indent + "item", item);
    }

}
