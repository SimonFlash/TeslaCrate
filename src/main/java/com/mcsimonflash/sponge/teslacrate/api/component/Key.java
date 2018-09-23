package com.mcsimonflash.sponge.teslacrate.api.component;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Key<T extends Key<T>> extends Component<T, Integer> {

    private int quantity = 1;

    protected Key(String id) {
        super(id);
    }

    @Override
    public Integer getValue() {
        return quantity;
    }

    public abstract int get(User user);

    public boolean check(User user, int quantity) {
        return get(user) >= quantity;
    }

    public abstract boolean give(User user, int quantity);

    public abstract boolean take(User user, int quantity);

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        quantity = node.getNode("quantity").getInt(1);
        super.deserialize(node);
    }

    @Override
    protected Integer deserializeValue(ConfigurationNode node) {
        return node.getInt(quantity);
    }

}