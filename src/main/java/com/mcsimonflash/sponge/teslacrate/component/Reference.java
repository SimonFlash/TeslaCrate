package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.MoreObjects;

public final class Reference<T extends Referenceable<T, V>, V> extends Component<Reference<T, V>> {

    private final T component;
    private final V value;

    private Reference(Builder<T, V, ?> builder) {
        super(builder);
        component = builder.component;
        value = builder.value;
    }

    public final T getComponent() {
        return component;
    }

    public final V getValue() {
        return value;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("component", component.getId())
                .add("value", value);
    }

    public static abstract class Builder<T extends Referenceable<T, V>, V, R extends Builder<T, V, R>> extends Component.Builder<Reference<T, V>, R> {

        protected final T component;
        protected V value;

        protected Builder(String id, T component) {
            super(id);
            this.component = component;
        }

        public final R value(V value) {
            this.value = value;
            return (R) this;
        }

        @Override
        public final Reference<T, V> build() {
            return new Reference<>(this);
        }

    }

}
