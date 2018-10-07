package com.mcsimonflash.sponge.teslacrate.api.component;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;

public abstract class Key<T extends Key<T>> extends Component<T, Integer> {

    protected Key(String id) {
        super(id);
    }

    public abstract int get(User user);

    public boolean check(User user, int quantity) {
        return get(user) >= quantity;
    }

    public abstract boolean give(User user, int quantity);

    public abstract boolean take(User user, int quantity);

    @Override
    protected Integer deserializeValue(ConfigurationNode node) {
        return node.getInt(getValue());
    }

}