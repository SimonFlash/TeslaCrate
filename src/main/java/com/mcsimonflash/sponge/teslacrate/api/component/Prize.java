package com.mcsimonflash.sponge.teslacrate.api.component;

import org.spongepowered.api.entity.living.player.User;

public abstract class Prize<V> extends Referenceable<V> {

    protected Prize(String id) {
        super(id);
    }

    public abstract boolean give(User user, V value);

    @Override
    public Ref<? extends Prize<V>, V> createRef(String id) {
        return new Ref<>(id, this);
    }

    public static class Ref<T extends Prize<V>, V> extends Reference<T, V> {

        protected Ref(String id, T component) {
            super(id, component);
        }

        public final void give(User user) {
            getComponent().give(user, getValue());
        }

    }

}