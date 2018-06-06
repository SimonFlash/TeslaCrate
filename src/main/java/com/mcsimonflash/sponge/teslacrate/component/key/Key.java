package com.mcsimonflash.sponge.teslacrate.component.key;

import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;

public abstract class Key<T extends Key<T>> extends Referenceable<T, Integer> {

    private int quantity;

    protected Key(Builder<T, ?> builder) {
        super(builder);
        quantity = builder.quantity;
    }

    public abstract int get(User user);

    public abstract boolean check(User user, int quantity);

    public abstract boolean give(User user, int quantity);

    public abstract boolean take(User user, int quantity);

    public final int getQuantity() {
        return quantity;
    }

    public static abstract class Builder<T extends Key<T>, B extends Builder<T, B>> extends Referenceable.Builder<T, Integer, B> {

        protected int quantity = 1;

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

    public static abstract class RefBuilder<T extends Key<T>, B extends RefBuilder<T, B>> extends Reference.Builder<T, Integer, B> {

        protected RefBuilder(String id, T component) {
            super(id, component);
        }

        @Override
        protected void resolve() throws IllegalStateException {
            super.resolve();
        }

    }

}
