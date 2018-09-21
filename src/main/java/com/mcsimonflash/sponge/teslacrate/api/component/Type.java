package com.mcsimonflash.sponge.teslacrate.api.component;

import org.spongepowered.api.plugin.PluginContainer;

import java.util.function.Function;

public final class Type<T extends Component<T, V>, V> {

    private final String name;
    private final Function<String, T> constructor;
    private final PluginContainer container;

    public Type(String name, Function<String, T> constructor, PluginContainer container) {
        this.name = name;
        this.constructor = constructor;
        this.container = container;
    }

    public final String getName() {
        return name;
    }

    public final PluginContainer getContainer() {
        return container;
    }

    public final T create(String id) {
        return constructor.apply(id);
    }

}