package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;

public abstract class Reference<T extends Referenceable<V>, V> extends Component {

    private final T component;
    @Nullable private V value;

    Reference(String id, T component) {
        super(id);
        this.component = component;
        setName(component.getName());
        setDescription(component.getDescription());
        setDisplayItem(component.getDisplayItem());
    }

    public final T getComponent() {
        return component;
    }

    public final V getValue() {
        return value != null ? value : component.getRefValue();
    }

    public final void setValue(@Nullable V value) {
        this.value = value;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "component", component.getId())
                .add(indent + "value", getValue());
    }

}