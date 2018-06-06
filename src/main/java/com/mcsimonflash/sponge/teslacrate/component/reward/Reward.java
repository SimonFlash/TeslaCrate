package com.mcsimonflash.sponge.teslacrate.component.reward;

import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslacrate.component.prize.Prize;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

public abstract class Reward<T extends Reward<T>> extends Referenceable<T, Double> {

    private final double weight;
    private final boolean repeatable;
    private final ImmutableSet<Reference<? extends Prize, ?>> prizes;

    protected Reward(Builder<T, ?> builder) {
        super(builder);
        weight = builder.weight;
        repeatable = builder.repeatable;
        prizes = ImmutableSet.copyOf(builder.prizes);
    }

    public abstract void give(Player player);

    public final double getWeight() {
        return weight;
    }

    public final boolean isRepeatable() {
        return repeatable;
    }

    public final ImmutableSet<Reference<? extends Prize, ?>> getPrizes() {
        return prizes;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("weight", weight)
                .add("repeatable", repeatable)
                .add("prizes", Arrays.toString(prizes.stream().map(r -> r.getComponent().getId() + "=" + r.getValue()).toArray()));
    }

    protected static abstract class Builder<T extends Reward<T>, B extends Builder<T, B>> extends Referenceable.Builder<T, Double, B> {

        private double weight = 0;
        private boolean repeatable = true;
        private final Set<Reference<? extends Prize<?, ?>, ?>> prizes = Sets.newHashSet();

        protected Builder(String id, Type<T, Double, B, ?> type) {
            super(id, type);
        }

        public final B weight(double weight) {
            this.weight = weight;
            return (B) this;
        }

        public final B repeatable(boolean repeatable) {
            this.repeatable = repeatable;
            return (B) this;
        }

        public final B prize(Reference<? extends Prize<?, ?>, ?> prize) {
            prizes.add(prize);
            return (B) this;
        }

        @Override
        public B deserialize(ConfigurationNode node) throws ConfigurationException {
            for (ConfigurationNode n : node.getNode("prizes").getChildrenMap().values()) {
                String id = this.id + ":prize:" + n.getKey();
                prize(Serializers.getComponent(id, n, Registry.PRIZES, TeslaCrate.get().getContainer()).createRef(id).deserialize(n).build());
            }
            return super.deserialize(node)
                    .weight(node.getNode("weight").getDouble())
                    .repeatable(node.getNode("repeatable").getBoolean(true));
        }

        @Override
        protected void resolve() throws IllegalStateException {
            super.resolve();
            checkState(!prizes.isEmpty(), "Prizes may not be empty.");
            checkState(weight >= 0, "Weight must be at least 0.");
        }

    }

    public static abstract class RefBuilder<T extends Reward<T>, B extends RefBuilder<T, B>> extends Reference.Builder<T, Double, B> {

        protected RefBuilder(String id, T component) {
            super(id, component);
        }

        @Override
        public final B deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).value(node.getDouble(component.getWeight()));
        }

        @Override
        protected void resolve() throws IllegalStateException {
            super.resolve();
            checkState(value >= 0, "Weight must be at least 0.");
        }

    }

}
