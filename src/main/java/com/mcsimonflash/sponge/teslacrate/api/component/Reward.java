package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;

import java.util.Arrays;
import java.util.List;

public abstract class Reward extends Referenceable<Double> {

    private boolean repeatable = true;
    private double weight = 0.0;
    private final List<Prize.Ref> prizes = Lists.newArrayList();

    protected Reward(String name) {
        super(name);
    }

    public final boolean isRepeatable() {
        return repeatable;
    }

    public final void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public final double getWeight() {
        return weight;
    }

    public final void setWeight(double weight) {
        this.weight = weight;
    }

    public final List<Prize.Ref> getPrizes() {
        return prizes;
    }

    public final void addPrize(Prize.Ref prize) {
        prizes.add(prize);
    }

    public void give(User user) {
        getPrizes().forEach(r -> r.give(user));
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        setRepeatable(node.getNode("repeatable").getBoolean(true));
        setWeight(Serializers.deserializeInt(node.getNode("weight"), Range.atLeast(0)));
        //TODO: prizes
    }

    @Override
    public final Double getRefValue() {
        return weight;
    }

    @Override
    public final Ref createRef(String name) {
        return new Ref(name, this);
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("weight", weight)
                .add("repeatable", repeatable)
                .add("prizes", Arrays.toString(prizes.stream().map(r -> r.getComponent().getName() + "=" + r.getValue()).toArray()));
    }

    public final static class Ref extends Reference<Reward, Double> {

        protected Ref(String name, Reward component) {
            super(name, component);
        }

        @Override
        public final void deserialize(ConfigurationNode node) {
            setValue(node.getDouble(getValue()));
        }

    }

}