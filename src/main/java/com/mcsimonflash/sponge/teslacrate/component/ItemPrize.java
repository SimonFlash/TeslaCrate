package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.InventoryTransformations;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

public final class ItemPrize extends Prize<Integer> {

    public static final Type<ItemPrize, Integer> TYPE = Type.create("Item", ItemPrize::new, n -> !n.getNode("item").isVirtual());

    private ItemStackSnapshot item = ItemStackSnapshot.NONE;

    private ItemPrize(String name) {
        super(name);
    }

    public final ItemStackSnapshot getItem() {
        return item;
    }

    public final void setItem(ItemStackSnapshot item) {
        this.item = item;
    }

    public final int getQuantity() {
        return item.getQuantity();
    }

    @Override
    public final boolean give(User user, Integer value) {
        ItemStack item = getItem().createStack();
        item.setQuantity(value);
        return user.getInventory().transform(InventoryTransformations.PLAYER_MAIN_HOTBAR_FIRST).offer(item).getType() == InventoryTransactionResult.Type.SUCCESS;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        setItem(Serializers.deserializeItem(node.getNode("item")));
    }

    @Override
    public final Integer getRefValue() {
        return item.getQuantity();
    }

    @Override
    public final Ref createRef(String name) {
        return new Ref(name, this);
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("item", item);
    }

    public final static class Ref extends Prize.Ref<ItemPrize, Integer> {

        private Ref(String name, ItemPrize component) {
            super(name, component);
        }

        @Override
        public final void deserialize(ConfigurationNode node) {
            super.deserialize(node);
            setValue(node.getInt(getComponent().getQuantity()));
        }

    }

}
