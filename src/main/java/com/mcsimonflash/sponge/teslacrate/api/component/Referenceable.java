package com.mcsimonflash.sponge.teslacrate.api.component;

import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;

public abstract class Referenceable<V> extends Component {

    protected Referenceable(String id) {
        super(id);
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        NodeUtils.ifVirtual(node.getNode("display-item"), n -> setDisplayItem(createDisplayItem(getRefValue()).build().createSnapshot()));
        super.deserialize(node);
    }

    public abstract V getRefValue();

    public abstract Reference<? extends Referenceable<V>, V> createRef(String id);

    protected abstract ItemStack.Builder createDisplayItem(V value);

}