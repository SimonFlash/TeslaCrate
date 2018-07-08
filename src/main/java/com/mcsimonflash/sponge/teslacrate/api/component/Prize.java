package com.mcsimonflash.sponge.teslacrate.api.component;

import org.spongepowered.api.entity.living.player.User;

public abstract class Prize<V> extends Referenceable<V> {

    protected Prize(String name) {
        super(name);
    }

    public abstract boolean give(User user, V value);

    @Override
    public Ref<? extends Prize<V>, V> createRef(String name) {
        return new Ref<>(name, this);
    }

    public static class Ref<T extends Prize<V>, V> extends Reference<T, V> {

        protected Ref(String name, T component) {
            super(name, component);
        }

        public void give(User user) {
            getComponent().give(user, getValue());
        }

    }

}