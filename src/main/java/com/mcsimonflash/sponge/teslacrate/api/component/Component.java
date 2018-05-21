package com.mcsimonflash.sponge.teslacrate.api.component;

import ninja.leaping.configurate.ConfigurationNode;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Component<T extends Component> {

    private final String id;
    private final Type<T, ?> type;

    Component(Builder<T, ?> builder) {
        id = builder.id;
        type = this instanceof Type ? (Type) this : builder.type;
    }

    public final String getId() {
        return id;
    }

    public final Type<T, ?> getType() {
        return type;
    }

    protected static abstract class Builder<T extends Component, B extends Builder<T, B>> {

        private final String id;
        private final Type<T, B> type;

        Builder(String id, Type<T, B> type) {
            this.id = id;
            this.type = type;
        }

        @OverridingMethodsMustInvokeSuper
        public B deserialize(ConfigurationNode node) {
            return (B) this;
        }

        public abstract T build();

    }

}
