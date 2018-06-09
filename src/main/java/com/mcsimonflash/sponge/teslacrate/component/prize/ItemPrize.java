package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

public final class ItemPrize extends Prize<ItemPrize, Integer> {

    public static final Type<ItemPrize, Integer, Builder, RefBuilder> TYPE = Type.create("Item", Builder::new, RefBuilder::new)
            .matcher(n -> !n.getNode("item").isVirtual())
            .build();

    private final ItemStackSnapshot item;

    private ItemPrize(Builder builder) {
        super(builder);
        item = builder.item;
    }

    @Override
    public final boolean give(User user, Integer value) {
        ItemStack item = getItem().createStack();
        item.setQuantity(value);
        return user.getInventory().transform(InventoryTransformations.PLAYER_MAIN_HOTBAR_FIRST).offer(item).getType() == InventoryTransactionResult.Type.SUCCESS;
    }

    public final ItemStackSnapshot getItem() {
        return item;
    }

    public final int getQuantity() {
        return getValue();
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("item", item);
    }

    public static final class Builder extends Prize.Builder<ItemPrize, Integer, Builder> {

        private ItemStackSnapshot item;

        private Builder(String id) {
            super(id, TYPE);
        }

        public final Builder item(ItemStackSnapshot item) {
            this.item = item;
            return this;
        }

        public final Builder quantity(int quantity) {
            return value(quantity);
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
            return super.deserialize(node).value(node.getInt(value));
        }

    }

}
