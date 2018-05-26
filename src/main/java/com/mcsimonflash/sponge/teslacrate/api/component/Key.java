package com.mcsimonflash.sponge.teslacrate.api.component;

import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class Key<T extends Key<T>> extends Referenceable<T, Integer> {

    private final int quantity;

    protected Key(Builder<T, ?> builder) {
        super(builder);
        quantity = builder.quantity;

    }

    public final int getQuantity() {
        return quantity;
    }

    public static abstract class Builder<T extends Key<T>, B extends Builder<T, B>> extends Referenceable.Builder<T, Integer, B> {

        private int quantity;

        protected Builder(String id, Type<T, Integer, B, ?> type) {
            super(id, type);
        }

        public final B quantity(int quantity) {
            this.quantity = quantity;
            return (B) this;
        }

        @Override
        public B deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).quantity(node.getNode("quantity").getInt(1));
        }

    }

}
