package com.mcsimonflash.sponge.teslacrate.component.key;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryTransformations;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

public final class PhysicalKey extends Key<PhysicalKey> {

    public static final Type<PhysicalKey, Integer> TYPE = new Type<>("Physical", PhysicalKey::new, TeslaCrate.get().getContainer());

    private ItemStackSnapshot item = ItemStackSnapshot.NONE;

    private PhysicalKey(String id) {
        super(id);
    }

    @Override
    public final int get(User user) {
        return user.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item.createStack())).totalItems();
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
        DataContainer container;
        if (node.getNode("item").hasMapChildren()) {
            container = Serializers.deserializeItem(node.getNode("item")).toContainer();
        } else {
            container = ItemStack.of(Serializers.deserializeCatalogType(node.getNode("item"), ItemType.class), 1).toContainer();
        }
        item = ItemStack.builder()
                .fromContainer(container.set(DataQuery.of("UnsafeData", "TeslaCrate", "Key"), getId()))
                .build().createSnapshot();
        super.deserialize(node);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Integer value) {
        return value == item.getQuantity() ? item : ItemStack.builder().fromSnapshot(item).quantity(value).build().createSnapshot();
    }

}