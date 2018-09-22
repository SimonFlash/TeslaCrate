package com.mcsimonflash.sponge.teslacrate.api.component;

import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Component<T extends Component<T, V>, V> {

    private final String id;
    private Text name;
    private Text description = Text.EMPTY;
    private ItemStackSnapshot displayItem = ItemStackSnapshot.NONE;
    boolean defaultItem = true;

    Component(String id) {
        this.id = id;
        this.name = Text.of(id);
    }

    public final String getId() {
        return id;
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public ItemStackSnapshot getDisplayItem() {
        return displayItem;
    }

    protected abstract V getValue();

    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        NodeUtils.ifAttached(node.getNode("name"), n -> name = (Utils.toText(n.getString(""))));
        NodeUtils.ifAttached(node.getNode("description"), n -> description = (Utils.toText(n.getString(""))));
        ConfigurationNode diNode = node.getNode("display-item");
        if (defaultItem = diNode.isVirtual()) {
            displayItem = createDisplayItem(getValue());
        } else if (diNode.hasMapChildren()) {
            displayItem = Serializers.itemStack(diNode);
        } else {
            displayItem = ItemStack.of(Serializers.catalogType(diNode, ItemType.class), 1).createSnapshot();
        }
    }

    protected abstract V deserializeValue(ConfigurationNode node);

    protected abstract ItemStackSnapshot createDisplayItem(V value);

    public final Reference<T, V> createReference(String id, ConfigurationNode node) {
        return new Reference<>(id, (T) this, node.hasMapChildren() ? getValue() : deserializeValue(node));
    }

}