package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.mcsimonflash.sponge.teslacrate.api.component.*;
import com.mcsimonflash.sponge.teslacrate.api.configuration.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.*;

public final class ItemPrize extends Prize<ItemPrize, Integer> {

    public static final Type<ItemPrize, Integer, Builder, RefBuilder> TYPE = Type.create("Item", Builder::new, RefBuilder::new).build();

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
            return super.deserialize(node)
                    .item(Serializers.deserializeItem(node.getNode("item")))
                    .quantity(item.getQuantity());
        }

        @Override
        public final ItemPrize build() {
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
