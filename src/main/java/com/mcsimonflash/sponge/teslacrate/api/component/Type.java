package com.mcsimonflash.sponge.teslacrate.api.component;

import ninja.leaping.configurate.ConfigurationNode;

import java.util.function.Function;
import java.util.function.Predicate;

public final class Type<T extends Referenceable<V>, V> extends Component {

    private final Function<String, T> creator;
    private final Predicate<ConfigurationNode> predicate;

    public Type(String name, Function<String, T> creator, Predicate<ConfigurationNode> predicate) {
        super(name);
        this.creator = creator;
        this.predicate = predicate;
    }

    public final T create(String name) {
        return creator.apply(name);
    }

    public final boolean matches(ConfigurationNode node) {
        return predicate.test(node);
    }

    public static <T extends Referenceable<V>, V> Type<T, V> create(String name, Function<String, T> creator) {
        return new Type<>(name, creator, n -> false);
    }

    public static <T extends Referenceable<V>, V> Type<T, V> create(String name, Function<String, T> creator, Predicate<ConfigurationNode> predicate) {
        return new Type<>(name, creator, predicate);
    }

}