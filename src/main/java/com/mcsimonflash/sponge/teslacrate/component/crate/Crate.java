package com.mcsimonflash.sponge.teslacrate.component.crate;

import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslacrate.component.reward.Reward;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.*;

import java.util.*;

public abstract class Crate<T extends Crate<T, V>, V> extends Referenceable<T, V> {

    private ImmutableSet<Reference<? extends Reward, Double>> rewards;

    protected Crate(Builder<T, V, ?> builder) {
        super(builder);
        rewards = ImmutableSet.copyOf(builder.rewards);
    }

    public abstract void open(Player player, Location<World> location);

    public final ImmutableSet<Reference<? extends Reward, Double>> getRewards() {
        return rewards;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("rewards", Arrays.toString(rewards.stream().map(r -> r.getComponent().getId() + "=" + r.getValue()).toArray()));
    }

    public static abstract class Builder<T extends Crate<T, V>, V, B extends Builder<T, V, B>> extends Referenceable.Builder<T, V, B> {

        private Set<Reference<? extends Reward, Double>> rewards = Sets.newHashSet();

        protected Builder(String id, Type<T, V, B, ?> type) {
            super(id, type);
        }

        public final B reward(Reference<? extends Reward<?>, Double> reward) {
            rewards.add(reward);
            return (B) this;
        }

        @Override
        public B deserialize(ConfigurationNode node) throws ConfigurationException {
            for (ConfigurationNode n : node.getNode("rewards").getChildrenMap().values()) {
                String id = this.id + ":reward:" + n.getKey();
                reward(Serializers.getComponent(id, n, Registry.REWARDS, TeslaCrate.get().getContainer()).createRef(id).deserialize(n).build());
            }
            return super.deserialize(node);
        }

    }

}
