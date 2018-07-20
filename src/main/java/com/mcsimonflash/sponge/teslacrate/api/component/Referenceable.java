package com.mcsimonflash.sponge.teslacrate.api.component;

import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Referenceable<V> extends Component {

    protected Referenceable(String id) {
        super(id);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        NodeUtils.ifVirtual(node.getNode("display-item"), n -> setDisplayItem(createDisplayItem(getRefValue()).build().createSnapshot()));
    }

    public abstract V getRefValue();

    public abstract Reference<? extends Referenceable<V>, V> createRef(String id);

    protected abstract ItemStack.Builder createDisplayItem(V value);

}