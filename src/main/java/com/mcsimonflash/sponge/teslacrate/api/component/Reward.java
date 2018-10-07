package com.mcsimonflash.sponge.teslacrate.api.component;

import com.mcsimonflash.sponge.teslacrate.internal.Config;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Reward<T extends Reward<T>> extends Component<T, Double> {

    private boolean announce = true;
    private int limit = 0;
    private double weight = 0.0;

    protected Reward(String id) {
        super(id);
    }

    public boolean isAnnounce() {
        return announce;
    }

    @Override
    public Double getValue() {
        return weight;
    }

    @OverridingMethodsMustInvokeSuper
    public void give(User user) {
        if (limit != 0) {
            Config.setRewardCount(user.getUniqueId(), this, Config.getRewardCount(user.getUniqueId(), this));
        }
    }

    public boolean check(User user) {
        return limit == 0 || Config.getRewardCount(user.getUniqueId(), this) < limit;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        announce = node.getNode("announce").getBoolean(true);
        limit = node.getNode("limit").getInt(0);
        weight = node.getNode("weight").getDouble(0.0);
        super.deserialize(node);
    }

    @Override
    protected Double deserializeValue(ConfigurationNode node) {
        return node.getDouble(weight);
    }

}