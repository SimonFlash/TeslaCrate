package com.mcsimonflash.sponge.teslacrate.api.component;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.function.Function;
import java.util.function.Predicate;

public final class Type<T extends Component<T, V>, V> {

    private final String name;
    private final Function<String, T> constructor;
    private final Predicate<ConfigurationNode> predicate;
    private final PluginContainer container;

    public Type(String name, Function<String, T> constructor, Predicate<ConfigurationNode> predicate, PluginContainer container) {
        this.name = name;
        this.constructor = constructor;
        this.predicate = predicate;
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

    public final boolean matches(ConfigurationNode node) {
        return predicate.test(node);
    }

}