package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.component.Reference;
import com.mcsimonflash.sponge.teslacrate.component.Type;
import com.mcsimonflash.sponge.teslalibs.configuration.*;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.*;

public final class ItemPrize extends Prize<ItemPrize, Integer> {

    public static final Type<ItemPrize, Integer, Builder, RefBuilder> TYPE = Type.create("Item", Builder::new, RefBuilder::new)
            .matcher(n -> !n.getNode("item").isVirtual())
            .build();

    private final ItemStackSnapshot item;
    private final int quantity;

    private ItemPrize(Builder builder) {
        super(builder);
        item = builder.item;
        quantity = builder.quantity;
    }

    @Override
    public final void give(Player player, Integer value) {
        ItemStack item = getItem().createStack();
        item.setQuantity(value);
        player.getInventory().transform(InventoryTransformations.PLAYER_MAIN_HOTBAR_FIRST).offer(item);
    }

    public final ItemStackSnapshot getItem() {
        return item;
    }

    public final int getQuantity() {
        return quantity;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("item", item)
                .add("quantity", quantity);
    }

    public static final class Builder extends Prize.Builder<ItemPrize, Integer, Builder> {

        private ItemStackSnapshot item;
        private int quantity;

        private Builder(String id) {
            super(id, TYPE);
        }

        public final Builder item(ItemStackSnapshot item) {
            this.item = item;
            return this;
        }

        public final Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        @Override
        public final Builder deserialize(ConfigurationNode node) throws ConfigurationException {
            super.deserialize(node)
                .item(Serializers.deserializeItem(node.getNode("item")))
                .quantity(item.getQuantity());
            return this;
        }

        @Override
        public final ItemPrize build() {
            if (icon == null) icon(item);
            return new ItemPrize(this);
        }

    }

    public static final class RefBuilder extends Reference.Builder<ItemPrize, Integer, RefBuilder> {

        private RefBuilder(String id, ItemPrize component) {
            super(id, component);
        }

        @Override
        public final RefBuilder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).value(node.getInt(component.getQuantity()));
        }

    }

}
