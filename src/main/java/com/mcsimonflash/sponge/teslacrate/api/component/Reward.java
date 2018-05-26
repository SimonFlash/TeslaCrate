package com.mcsimonflash.sponge.teslacrate.api.component;

import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

public abstract class Reward<T extends Reward<T>> extends Referenceable<T, Double> {

    private final double weight;

    protected Reward(Builder<T, ?> builder) {
        super(builder);
        weight = builder.weight;
    }

    public abstract void give(Player player);

    public final double getWeight() {
        return weight;
    }

    protected static abstract class Builder<T extends Reward<T>, B extends Builder<T, B>> extends Referenceable.Builder<T, Double, B> {

        private double weight;

        protected Builder(String id, Type<T, Double, B, ?> type) {
            super(id, type);
        }

        public final B weight(double weight) {
            this.weight = weight;
            return (B) this;
        }

        @Override
        public B deserialize(ConfigurationNode node) throws ConfigurationNodeException {
            return super.deserialize(node).weight(node.getNode("weight").getDouble());
        }

    }

}
