package com.mcsimonflash.sponge.teslacrate.api.component;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public class Reference<T extends Component<T, V>, V> {

    private final String id;
    private final T component;
    private V value;
    private ItemStackSnapshot displayItem;

    protected Reference(String id, T component, V value) {
        this.id = id;
        this.component = component;
        this.value = value;
        this.displayItem = component.defaultItem ? component.createDisplayItem(value) : component.getDisplayItem();
    }

    public final String getId() {
        return id;
    }

    public final T getComponent() {
        return component;
    }

    public V getValue() {
        return value;
    }

    public ItemStackSnapshot getDisplayItem() {
        return displayItem;
    }

}