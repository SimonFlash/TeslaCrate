package com.mcsimonflash.sponge.teslacrate.api.component;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Key extends Referenceable<Integer> {

    private int quantity = 1;

    protected Key(String name) {
        super(name);
    }

    public final int getQuantity() {
        return quantity;
    }

    public final void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public abstract int get(User user);

    public abstract boolean check(User user, int quantity);

    public abstract boolean give(User user, int quantity);

    public abstract boolean take(User user, int quantity);

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        setQuantity(node.getNode("quantity").getInt(1));
    }

    @Override
    public final Integer getRefValue() {
        return getQuantity();
    }

    @Override
    public final Ref createRef(String name) {
        return new Ref(name, this);
    }

    public final static class Ref extends Reference<Key, Integer> {

        protected Ref(String name, Key component) {
            super(name, component);
        }

        @Override
        public final void deserialize(ConfigurationNode node) {
            setValue(node.getInt(getValue()));
        }

    }

}