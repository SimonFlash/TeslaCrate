package com.mcsimonflash.sponge.teslacrate.api.component;

import java.util.function.Function;

public final class Type<T extends Component, B extends Component.Builder<T, B>> extends Component<Type<T, B>> {

    private Function<String, B> function;

    private Type(Builder<T, B> builder) {
        super(builder);
        function = builder.function;
    }

    public final B create(String id) {
        return function.apply(id);
    }

    public static <T extends Component, B extends Component.Builder<T, B>> Type<T, B> of(String id, Function<String, B> function) {
        return new Builder<>(id, function).build();
    }

    private static final class Builder<T extends Component, B extends Component.Builder<T, B>> extends Component.Builder<Type<T, B>, Builder<T, B>> {

        private final Function<String, B> function;

        private Builder(String id, Function<String, B> function) {
            super(id, null);
            this.function = function;
        }

        @Override
        public Type<T, B> build() {
            return new Type<>(this);
        }

    }

}
