package com.mcsimonflash.sponge.teslacrate.api.component;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Reward<T extends Reward<T>> extends Component<T, Double> {

    private boolean announce = true;
    private boolean repeatable = true;
    private double weight = 0.0;

    protected Reward(String id) {
        super(id);
    }

    public boolean isAnnounce() {
        return announce;
    }

    @Override
    protected Double getValue() {
        return weight;
    }

    public abstract void give(User user);

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        announce = node.getNode("announce").getBoolean(true);
        repeatable = node.getNode("repeatable").getBoolean(true);
        weight = node.getNode("weight").getDouble(0.0);
        super.deserialize(node);
    }

    @Override
    protected Double deserializeValue(ConfigurationNode node) {
        return node.getDouble(weight);
    }

}