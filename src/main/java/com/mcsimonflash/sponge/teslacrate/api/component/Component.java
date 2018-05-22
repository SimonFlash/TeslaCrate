package com.mcsimonflash.sponge.teslacrate.api.component;

import ninja.leaping.configurate.ConfigurationNode;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Component<T extends Component<T>> {

    private final String id;

    Component(Builder<T, ?> builder) {
        id = builder.id;
    }

    public final String getId() {
        return id;
    }

    protected static abstract class Builder<T extends Component<T>, B extends Builder<T, B>> {

        private final String id;

        Builder(String id) {
            this.id = id;
        }

        @OverridingMethodsMustInvokeSuper
        public B deserialize(ConfigurationNode node) {
            return (B) this;
        }

        public abstract T build();

    }

}
