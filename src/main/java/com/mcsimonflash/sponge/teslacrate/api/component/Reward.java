package com.mcsimonflash.sponge.teslacrate.api.component;

import org.spongepowered.api.entity.living.player.Player;

public abstract class Reward<T extends Reward<T>> extends Component<T> {

    private final Type<T, Double, ?, ?> type;
    private final double weight;

    protected Reward(Builder<T, ?> builder) {
        super(builder);
        type = builder.type;
        weight = builder.weight;
    }

    public abstract void give(Player player);

    public final Type<T, Double, ?, ?> getType() {
        return type;
    }

    public final double getWeight() {
        return weight;
    }

    protected static abstract class Builder<T extends Reward<T>, B extends Builder<T, B>> extends Component.Builder<T, B> {

        private final Type<T, Double, B, ?> type;
        private double weight;

        protected Builder(String id, Type<T, Double, B, ?> type) {
            super(id);
            this.type = type;
        }

        public final B weight(double weight) {
            this.weight = weight;
            return (B) this;
        }

    }

}
