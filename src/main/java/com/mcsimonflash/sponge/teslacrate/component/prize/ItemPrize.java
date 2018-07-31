package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.InventoryTransformations;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

public final class ItemPrize extends Prize<Integer> {

    public static final Type<ItemPrize> TYPE = new Type<>("Item", ItemPrize::new, n -> !n.getNode("item").isVirtual(), TeslaCrate.get().getContainer());

    private ItemStackSnapshot item = ItemStackSnapshot.NONE;

    private ItemPrize(String id) {
        super(id);
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

    public final void setQuantity(int quantity) {
        setItem(ItemStack.builder().fromSnapshot(item).quantity(quantity).build().createSnapshot());
    }

    @Override
    public final boolean give(User user, Integer value) {
        ItemStack item = getItem().createStack();
        item.setQuantity(value);
        return user.getInventory().transform(InventoryTransformations.PLAYER_MAIN_HOTBAR_FIRST).offer(item).getType() == InventoryTransactionResult.Type.SUCCESS;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        if (node.getNode("item").hasMapChildren()) {
            setItem(Serializers.deserializeItem(node.getNode("item")));
        } else {
            setItem(ItemStack.of(Serializers.deserializeCatalogType(node.getNode("item"), ItemType.class), 1).createSnapshot());
        }
        super.deserialize(node);
    }

    @Override
    public final Integer getRefValue() {
        return item.getQuantity();
    }

    @Override
    public final Ref createRef(String id) {
        return new Ref(id, this);
    }

    @Override
    protected ItemStack.Builder createDisplayItem(Integer value) {
        return ItemStack.builder().fromSnapshot(item);
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent).add(indent + "item", item);
    }

    public final static class Ref extends Prize.Ref<ItemPrize, Integer> {

        private Ref(String id, ItemPrize component) {
            super(id, component);
        }

        @Override
        public final Integer deserializeValue(ConfigurationNode node) {
            return node.getInt(getComponent().getQuantity());
        }

    }

}
