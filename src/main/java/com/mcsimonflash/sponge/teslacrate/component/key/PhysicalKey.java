package com.mcsimonflash.sponge.teslacrate.component.key;

import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Reference;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.api.configuration.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

//TODO: Data registration to identify keys
public final class PhysicalKey extends Key<PhysicalKey> {

    public static final Type<PhysicalKey, Integer, Builder, RefBuilder> TYPE = Type.create("Physical", Builder::new, RefBuilder::new).build();

    private final ItemStackSnapshot item;

    private PhysicalKey(Builder builder) {
        super(builder);
        item = builder.item;
    }

    public final ItemStackSnapshot getItem() {
        return item;
    }

    public static final class Builder extends Key.Builder<PhysicalKey, Builder> {

        private ItemStackSnapshot item;

        private Builder(String id) {
            super(id, TYPE);
        }

        public final Builder item(ItemStackSnapshot item) {
            this.item = item;
            return this;
        }

        @Override
        public final Builder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node)
                    .item(Serializers.deserializeItem(node.getNode("item")));
        }

        @Override
        public final PhysicalKey build() {
            return new PhysicalKey(this);
        }

    }

    public static final class RefBuilder extends Reference.Builder<PhysicalKey, Integer, RefBuilder> {

        private RefBuilder(String id, PhysicalKey component) {
            super(id, component);
        }

        @Override
        public final RefBuilder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).value(node.getInt(component.getQuantity()));
        }

    }

}
