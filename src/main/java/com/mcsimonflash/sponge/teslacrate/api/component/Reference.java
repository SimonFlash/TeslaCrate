package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import ninja.leaping.configurate.ConfigurationNode;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Reference<T extends Referenceable<V>, V> extends Component {

    private final T component;
    private V value;

    Reference(String id, T component) {
        super(id);
        this.component = component;
        value = component.getRefValue();
        setName(component.getName());
        setDescription(component.getDescription());
        setDisplayItem(component.getDisplayItem());
    }

    public final T getComponent() {
        return component;
    }

    public final V getValue() {
        return value;
    }

    public final void setValue(V value) {
        this.value = value;
    }

    public abstract V deserializeValue(ConfigurationNode node);

    @Override
    public final void deserialize(ConfigurationNode node) {
        if (!node.hasMapChildren()) {
            setValue(deserializeValue(node));
        }
        setDisplayItem(component.createDisplayItem(value).build().createSnapshot());
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "component", component.getId())
                .add(indent + "value", getValue());
    }

}