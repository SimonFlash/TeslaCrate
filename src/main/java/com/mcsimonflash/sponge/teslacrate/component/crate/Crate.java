package com.mcsimonflash.sponge.teslacrate.component.crate;

import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslacrate.component.key.Key;
import com.mcsimonflash.sponge.teslacrate.component.reward.Reward;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Crate<T extends Crate<T, V>, V> extends Referenceable<T, V> {

    private ImmutableSet<Reference<? extends Key<?>, Integer>> keys;
    private ImmutableSet<Reference<? extends Reward<?>, Double>> rewards;

    protected Crate(Builder<T, V, ?> builder) {
        super(builder);
        keys = ImmutableSet.copyOf(builder.keys);
        rewards = ImmutableSet.copyOf(builder.rewards);
    }

    public abstract void open(Player player, Location<World> location);

    public final boolean takeKeys(Player player) {
        List<Reference<? extends Key, ?>> missing = keys.stream().filter(r -> !r.getComponent().check(player, r.getValue())).collect(Collectors.toList());
        if (!missing.isEmpty()) {
            player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.crate.missing-keys", "crate", getId(), "keys", String.join(", ", missing.stream().map(r -> TeslaCrate.get().getMessages().get("teslacrate.crate.missing-keys.key-format", player.getLocale()).arg("key", r.getComponent().getId()).arg("quantity", r.getValue()).toString()).collect(Collectors.toList()))));
            return false;
        }
        keys.forEach(r -> r.getComponent().take(player, r.getValue()));
        return true;
    }

    public void preview(Player player, Location<World> location) {}

    public final ImmutableSet<Reference<? extends Key<?>, Integer>> getKeys() {
        return keys;
    }

    public final ImmutableSet<Reference<? extends Reward<?>, Double>> getRewards() {
        return rewards;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("rewards", Arrays.toString(rewards.stream().map(r -> r.getComponent().getId() + "=" + r.getValue()).toArray()));
    }

    public static abstract class Builder<T extends Crate<T, V>, V, B extends Builder<T, V, B>> extends Referenceable.Builder<T, V, B> {

        private Set<Reference<? extends Key<?>, Integer>> keys = Sets.newHashSet();
        private Set<Reference<? extends Reward<?>, Double>> rewards = Sets.newHashSet();

        protected Builder(String id, Type<T, V, B, ?> type) {
            super(id, type);
        }

        public final B key(Reference<? extends Key<?>, Integer> key) {
            keys.add(key);
            return (B) this;
        }

        public final B reward(Reference<? extends Reward<?>, Double> reward) {
            rewards.add(reward);
            return (B) this;
        }

        @Override
        public B deserialize(ConfigurationNode node) throws ConfigurationException {
            node.getNode("keys").getChildrenMap().values().forEach(n -> {
                String id = this.id + ":key:" + n.getKey();
                key(Serializers.getComponent(id, n, Registry.KEYS, TeslaCrate.get().getContainer()).createRef(id).deserialize(n).build());
            });
            node.getNode("rewards").getChildrenMap().values().forEach(n -> {
                String id = this.id + ":reward:" + n.getKey();
                reward(Serializers.getComponent(id, n, Registry.REWARDS, TeslaCrate.get().getContainer()).createRef(id).deserialize(n).build());
            });
            return super.deserialize(node);
        }

    }

}
