package com.mcsimonflash.sponge.teslacrate.api.component;

public abstract class Effect<T extends Effect<T, V>, V> extends Referenceable<T, V> {

    protected Effect(Builder<T, V, ?> builder) {
        super(builder);
    }

    public static abstract class Builder<T extends Effect<T, V>, V, B extends Builder<T, V, B>> extends Referenceable.Builder<T, V, B> {

        protected Builder(String id, Type<T, V, B, ?> type) {
            super(id, type);
        }

    }

}
