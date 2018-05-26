package com.mcsimonflash.sponge.teslacrate.api.component;

public abstract class Referenceable<T extends Referenceable<T, V>, V> extends Component<T> {

    private final Type<T, V, ?, ?> type;

    Referenceable(Builder<T, V, ?> builder) {
        super(builder);
        type = builder.type;
    }

    public Type<T, V, ?, ?> getType() {
        return type;
    }

    public Reference.Builder<T, V, ?> createRef(String id) {
        return type.createRef(id, (T) this);
    }

    public static abstract class Builder<T extends Referenceable<T, V>, V, B extends Builder<T, V, B>> extends Component.Builder<T, B> {

        private final Type<T, V, ?, ?> type;

        Builder(String id, Type<T, V, ?, ?> type) {
            super(id);
            this.type = type;
        }

    }
}