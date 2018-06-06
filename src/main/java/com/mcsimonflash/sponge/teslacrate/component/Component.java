package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Component<T extends Component<T>> {

    private final String id;
    private final Text name;
    private final Text description;
    private final ItemStackSnapshot icon;

    protected Component(Builder<T, ?> builder) {
        builder.resolve();
        id = builder.id;
        name = builder.name;
        description = builder.description != null ? builder.description : name;
        icon = builder.icon != null ? builder.icon : ItemStack.builder()
                .itemType(ItemTypes.WRITABLE_BOOK)
                .add(Keys.DISPLAY_NAME, name)
                .build().createSnapshot();
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

    @OverridingMethodsMustInvokeSuper
    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("description", description)
                .add("icon", icon);
    }

    @Override
    public final String toString() {
        return toStringHelper().toString();
    }

    public static abstract class Builder<T extends Component<T>, B extends Builder<T, B>> {

        protected final String id;
        protected Text name;
        @Nullable protected Text description;
        @Nullable protected ItemStackSnapshot icon;

        Builder(String id) {
            this.id = id;
            name =  Text.of(id.contains(":") ? id.substring(id.lastIndexOf(':')) : id);
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

        @OverridingMethodsMustInvokeSuper
        protected void resolve() throws IllegalStateException {
            if (description == null) description = name;
            if (icon == null) icon = ItemStack.builder()
                    .itemType(ItemTypes.WRITABLE_BOOK)
                    .add(Keys.DISPLAY_NAME, name)
                    .build().createSnapshot();
        }

        public abstract T build();

    }

}
