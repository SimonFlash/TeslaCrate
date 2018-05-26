package com.mcsimonflash.sponge.teslacrate.api.component;

import com.mcsimonflash.sponge.teslacrate.api.configuration.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.*;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Component<T extends Component<T>> {

    private final String id;
    private final Text name;
    private final Text description;
    private final ItemStackSnapshot icon;

    Component(Builder<T, ?> builder) {
        id = builder.id;
        name = builder.name;
        description = builder.description;
        icon = builder.icon;
    }

    public final String getId() {
        return id;
    }

    public final Text getName() {
        return name;
    }

    public final Text getDescription() {
        return description;
    }

    public final ItemStackSnapshot getIcon() {
        return icon;
    }

    public static abstract class Builder<T extends Component<T>, B extends Builder<T, B>> {

        protected final String id;
        private Text name;
        private Text description;
        private ItemStackSnapshot icon;

        Builder(String id) {
            this.id = id;
        }

        public final B name(Text name) {
            this.name = name;
            return (B) this;
        }

        public final B description(Text description) {
            this.description = description;
            return (B) this;
        }

        public final B icon(ItemStackSnapshot icon) {
            this.icon = icon;
            return (B) this;
        }

        @OverridingMethodsMustInvokeSuper
        public B deserialize(ConfigurationNode node) throws ConfigurationException {
            NodeUtils.ifAttached(node.getNode("name"), n -> name(Serializers.deserializeText(n)));
            NodeUtils.ifAttached(node.getNode("description"), n -> description(Serializers.deserializeText(n)));
            NodeUtils.ifAttached(node.getNode("icon"), n -> icon(Serializers.deserializeItem(n)));
            return (B) this;
        }

        public abstract T build();

    }

}
