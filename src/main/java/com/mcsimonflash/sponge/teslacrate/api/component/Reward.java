package com.mcsimonflash.sponge.teslacrate.api.component;

public abstract class Reward<T extends Reward> extends Component<T> {

    private final double weight;

    protected Reward(Builder<T, ?> builder) {
        super(builder);
        weight = builder.weight;
    }

    public final double getWeight() {
        return weight;
    }

    protected static abstract class Builder<T extends Reward, B extends Builder<T, B>> extends Component.Builder<T, B> {

        private double weight;

        protected Builder(String id, Type<T, B> type) {
            super(id, type);
        }

        public final B weight(double weight) {
            this.weight = weight;
            return (B) this;
        }

    }

}
