package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import ninja.leaping.configurate.ConfigurationNode;

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

    @Override
    public void deserialize(ConfigurationNode node) {
        setDisplayItem(value != null ? component.createDisplayItem(value).build().createSnapshot() : component.getDisplayItem());
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "component", component.getId())
                .add(indent + "value", getValue());
    }

}