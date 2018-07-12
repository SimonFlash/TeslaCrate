package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Reward extends Referenceable<Double> {

    private boolean repeatable = true;
    private double weight = 0.0;
    private final List<Prize.Ref> prizes = Lists.newArrayList();

    protected Reward(String id) {
        super(id);
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
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        setRepeatable(node.getNode("repeatable").getBoolean(true));
        setWeight(Serializers.deserializeInt(node.getNode("weight"), Range.atLeast(0)));
        node.getNode("prizes").getChildrenMap().values().forEach(n -> {
            String id = getId() + ":prize:" + n.getKey();
            Prize.Ref prize = Serializers.getComponent(id, n, Registry.PRIZES, TeslaCrate.get().getContainer()).createRef(id);
            prize.deserialize(n);
            addPrize(prize);
        });
        if (getDisplayItem() == ItemStackSnapshot.NONE) {
            setDisplayItem(Utils.createItem(ItemTypes.BOOK, getName(), prizes.stream().map(Component::getName).collect(Collectors.toList())).build().createSnapshot());
        }
    }

    @Override
    public final Double getRefValue() {
        return weight;
    }

    @Override
    public final Ref createRef(String id) {
        return new Ref(id, this);
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "weight", weight)
                .add(indent + "repeatable", repeatable)
                .add(indent + "prizes", Arrays.toString(prizes.stream().map(r -> r.getComponent().getId() + "=" + r.getValue()).toArray()));
    }

    public final static class Ref extends Reference<Reward, Double> {

        private Ref(String id, Reward component) {
            super(id, component);
        }

        @Override
        public final void deserialize(ConfigurationNode node) {
            super.deserialize(node);
            setValue(node.getDouble(getValue()));
        }

    }

}