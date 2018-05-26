package com.mcsimonflash.sponge.teslacrate.api.component;

public abstract class Crate<T extends Crate<T, V>, V> extends Referenceable<T, V> {

    protected Crate(Builder<T, V, ?> builder) {
        super(builder);
    }

    public static abstract class Builder<T extends Crate<T, V>, V, B extends Builder<T, V, B>> extends Referenceable.Builder<T, V, B> {

        protected Builder(String id, Type<T, V, B, ?> type) {
            super(id, type);
        }

    }

}
