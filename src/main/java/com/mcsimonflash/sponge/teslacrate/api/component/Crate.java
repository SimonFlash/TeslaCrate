package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Crate extends Referenceable<Object> {

    private final List<Effect.Ref> effects = Lists.newArrayList();
    private final List<Key.Ref> keys = Lists.newArrayList();
    private final List<Reward.Ref> rewards = Lists.newArrayList();

    protected Crate(String name) {
        super(name);
    }

    public final List<Effect.Ref> getEffects() {
        return effects;
    }

    public final void addEffect(Effect.Ref effect) {
        effects.add(effect);
    }

    public final List<Key.Ref> getKeys() {
        return keys;
    }

    public final void addKey(Key.Ref key) {
        keys.add(key);
    }

    public final List<Reward.Ref> getRewards() {
        return rewards;
    }

    public final void addReward(Reward.Ref reward) {
        rewards.add(reward);
    }

    public void open(Player player, Location<World> location) {
        double rand = Math.random() * getRewards().stream().mapToDouble(Reference::getValue).sum();
        for (Reference<? extends Reward, Double> reward : getRewards()) {
            if ((rand -= reward.getValue()) <= 0) {
                reward.getComponent().give(player);
                effects.forEach(e -> e.run(player, location));
                return;
            }
        }
    }

    public void preview(Player player, Location<World> location) {
        //TODO:
    }

    public boolean takeKeys(Player player) {
        List<Reference<? extends Key, ?>> missing = keys.stream().filter(r -> r.getComponent().get(player) < r.getValue()).collect(Collectors.toList());
        if (!missing.isEmpty()) {
            player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.crate.missing-keys", "crate", getName(), "keys", String.join(", ", missing.stream().map(r -> TeslaCrate.get().getMessages().get("teslacrate.crate.missing-keys.key-format", player.getLocale()).arg("key", r.getComponent().getName()).arg("quantity", r.getValue()).toString()).collect(Collectors.toList()))));
            return false;
        }
        keys.forEach(r -> r.getComponent().take(player, r.getValue() != null ? r.getValue() : r.getComponent().getQuantity()));
        return true;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        //TODO: effects, keys, rewards
    }

    @Override
    public final Crate getRefValue() {
        return this;
    }

    @Override
    public final Reference<? extends Crate, Object>  createRef(String name) {
        return new Reference<>(name, this);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("effects", Arrays.toString(effects.stream().map(e -> e.getComponent().getName() + "=" + e.getValue()).toArray()))
                .add("keys", Arrays.toString(keys.stream().map(k -> k.getComponent().getName() + "=" + k.getValue()).toArray()))
                .add("rewards", Arrays.toString(rewards.stream().map(r -> r.getComponent().getName() + "=" + r.getValue()).toArray()));
    }

}