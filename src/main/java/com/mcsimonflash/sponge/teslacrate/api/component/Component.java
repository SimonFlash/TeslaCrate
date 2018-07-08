package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Component {

    private final String name;
    private Text displayName = Text.EMPTY;
    private Text description = Text.EMPTY;
    private ItemStackSnapshot displayItem = ItemStackSnapshot.NONE;

    protected Component(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final Text getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(Text displayName) {
        this.displayName = displayName;
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
        setDisplayName(Utils.toText(node.getNode("display-name").getString(getName())));
        NodeUtils.ifAttached(node.getNode("description"), n -> setDescription(Serializers.deserializeText(n)));
        NodeUtils.ifAttached(node.getNode("display-item"), n -> setDisplayItem(Serializers.deserializeItem(n)));
    }

    @OverridingMethodsMustInvokeSuper
    public void serialize(ConfigurationNode node) {
        //TODO: serialization
    }

    @OverridingMethodsMustInvokeSuper
    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("display-name", displayName)
                .add("description", description)
                .add("display-item", displayItem);
    }

    public final String toString() {
        return toStringHelper().toString();
    }

}