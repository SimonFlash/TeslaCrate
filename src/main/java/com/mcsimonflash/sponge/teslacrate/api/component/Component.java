package com.mcsimonflash.sponge.teslacrate.api.component;

import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Component<T extends Component<T, V>, V> {

    private final String id;
    private Text name;
    private Text description = Text.EMPTY;
    private ItemStackSnapshot displayItem = ItemStackSnapshot.NONE;
    boolean defaultItem = true;

    Component(String id) {
        this.id = id;
        this.name = Utils.toText("&r" + Arrays.stream(id.split("-")).map(StringUtils::capitalize).collect(Collectors.joining(" ")));
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

    public abstract V getValue();

    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        NodeUtils.ifAttached(node.getNode("name"), n -> name = (Utils.toText("&r" + n.getString(""))));
        NodeUtils.ifAttached(node.getNode("description"), n -> description = (Utils.toText("&r" + n.getString(""))));
        ConfigurationNode diNode = node.getNode("display-item");
        if (defaultItem = diNode.isVirtual()) {
            displayItem = createDisplayItem(getValue());
        } else if (diNode.hasMapChildren()) {
            NodeUtils.ifVirtual(diNode.getNode("name"), n -> n.setValue(node.getNode("name")));
            NodeUtils.ifVirtual(diNode.getNode("lore"), n -> n.setValue(node.getNode("description")));
            displayItem = Serializers.itemStack(diNode);
        } else {
            displayItem = Utils.createItem(Serializers.catalogType(diNode, ItemType.class), getName(), getDescription()).build().createSnapshot();
        }
    }

    protected abstract V deserializeValue(ConfigurationNode node);

    protected abstract ItemStackSnapshot createDisplayItem(V value);

    public final Reference<T, V> createReference(String id, ConfigurationNode node) {
        return new Reference<>(id, (T) this, node.hasMapChildren() ? getValue() : deserializeValue(node));
    }

}