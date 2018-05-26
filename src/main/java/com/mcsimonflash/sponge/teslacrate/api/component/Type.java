package com.mcsimonflash.sponge.teslacrate.api.component;

import java.util.function.*;

public final class Type<T extends Referenceable<T, V>, V, B extends Component.Builder<T, B>, R extends Reference.Builder<T, V, R>> extends Component<Type<T, V, B, R>> {

    private final Function<String, B> component;
    private final BiFunction<String, T, R> reference;

    private Type(Builder<T, V, B, R> builder) {
        super(builder);
        component = builder.component;
        reference = builder.reference;
    }

    public final B create(String id) {
        return component.apply(id);
    }

    public final R createRef(String id, T component) {
        return reference.apply(id, component);
    }

    public static <T extends Referenceable<T, V>, V, B extends Component.Builder<T, B>, R extends Reference.Builder<T, V, R>> Builder<T, V, B, R> create(String id, Function<String, B> component, BiFunction<String, T, R> reference) {
        return new Builder<>(id, component, reference);
    }

    public static final class Builder<T extends Referenceable<T, V>, V, B extends Component.Builder<T, B>, R extends Reference.Builder<T, V, R>> extends Component.Builder<Type<T, V, B, R>, Builder<T, V, B, R>> {

        private final Function<String, B> component;
        private final BiFunction<String, T, R> reference;

        private Builder(String id, Function<String, B> component, BiFunction<String, T, R> reference) {
            super(id);
            this.component = component;
            this.reference = reference;
        }

        @Override
        public final Type<T, V, B, R> build() {
            return new Type<>(this);
        }

    }

}
