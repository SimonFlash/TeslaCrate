package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;

public class Reference<T extends Referenceable<V>, V> extends Component {

    private final T component;
    private V value;

    public Reference(String name, T component) {
        super(name);
        this.component = component;
        this.value = component.getRefValue();
        setDisplayName(component.getDisplayName());
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
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("component", component.getName())
                .add("value", value);
    }

}