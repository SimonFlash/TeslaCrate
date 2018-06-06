package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.MoreObjects;

public abstract class Referenceable<T extends Referenceable<T, V>, V> extends Component<T> {

    private final Type<T, V, ?, ?> type;

    protected Referenceable(Builder<T, V, ?> builder) {
        super(builder);
        type = builder.type;
    }

    public Type<T, V, ?, ?> getType() {
        return type;
    }

    public Reference.Builder<T, V, ?> createRef(String id) {
        return type.createRef(id, (T) this);
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("type", type.getId());
    }

    public static abstract class Builder<T extends Referenceable<T, V>, V, B extends Builder<T, V, B>> extends Component.Builder<T, B> {

        protected final Type<T, V, ?, ?> type;

        protected Builder(String id, Type<T, V, ?, ?> type) {
            super(id);
            this.type = type;
        }

    }

}
