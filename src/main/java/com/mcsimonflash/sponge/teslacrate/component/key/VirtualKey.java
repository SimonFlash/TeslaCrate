package com.mcsimonflash.sponge.teslacrate.component.key;

import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Reference;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;

public final class VirtualKey extends Key<VirtualKey> {

    public static final Type<VirtualKey, Integer, Builder, RefBuilder> TYPE = Type.create("Virtual", Builder::new, RefBuilder::new).build();

    private VirtualKey(Builder builder) {
        super(builder);
    }

    public static final class Builder extends Key.Builder<VirtualKey, Builder> {

        private Builder(String id) {
            super(id, TYPE);
        }

        @Override
        public final VirtualKey build() {
            return new VirtualKey(this);
        }

    }

    public static final class RefBuilder extends Reference.Builder<VirtualKey, Integer, RefBuilder> {

        private RefBuilder(String id, VirtualKey component) {
            super(id, component);
        }

        @Override
        public final RefBuilder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).value(node.getInt(component.getQuantity()));
        }

    }

}
