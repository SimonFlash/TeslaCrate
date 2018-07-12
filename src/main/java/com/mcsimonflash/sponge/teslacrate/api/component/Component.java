package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Component {

    private final String id;
    private Text name;
    private Text description = Text.EMPTY;
    private ItemStackSnapshot displayItem = ItemStackSnapshot.NONE;

    Component(String id) {
        this.id = id;
        this.name = Text.of(id);
    }

    public final String getId() {
        return id;
    }

    public final Text getName() {
        return name;
    }

    public final void setName(Text displayName) {
        this.name = displayName;
    }

    public final Text getDescription() {
        return description;
    }

    public final void setDescription(Text description) {
        this.description = description;
    }

    public final ItemStackSnapshot getDisplayItem() {
        return displayItem;
    }

    public final void setDisplayItem(ItemStackSnapshot displayItem) {
        this.displayItem = displayItem;
    }

    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        NodeUtils.ifAttached(node.getNode("name"), n -> setName(Serializers.deserializeText(n)));
        NodeUtils.ifAttached(node.getNode("description"), n -> setDescription(Serializers.deserializeText(n)));
        NodeUtils.ifAttached(node.getNode("display-item"), n -> setDisplayItem(Serializers.deserializeItem(n)));
    }

    @OverridingMethodsMustInvokeSuper
    public void serialize(ConfigurationNode node) {
        //TODO: serialization
    }

    @OverridingMethodsMustInvokeSuper
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return MoreObjects.toStringHelper(this)
                .add(indent + "id", id)
                .add(indent + "name", name)
                .add(indent + "description", description)
                .add(indent + "display-item", displayItem);
    }

    public final String toString() {
        return toStringHelper("\n    ").addValue("\n").toString();
    }

}