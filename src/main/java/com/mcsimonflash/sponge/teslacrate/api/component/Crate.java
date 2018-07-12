package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
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
        List<Reference<? extends Key, ?>> missing = keys.stream().filter(r -> !r.getComponent().check(player, r.getValue())).collect(Collectors.toList());
        if (!missing.isEmpty()) {
            player.sendMessage(TeslaCrate.getMessage(player, "teslacrate.crate.missing-keys", "crate", getId(), "keys", String.join(", ", missing.stream().map(r -> TeslaCrate.get().getMessages().get("teslacrate.crate.missing-keys.key-format", player.getLocale()).arg("key", r.getComponent().getId()).arg("quantity", r.getValue()).toString()).collect(Collectors.toList()))));
            return false;
        }
        keys.forEach(r -> r.getComponent().take(player, r.getValue()));
        return true;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        node.getNode("effects").getChildrenMap().values().forEach(n -> {
            String name = getId() + ":effect:" + n.getKey();
            Effect.Ref effect = Serializers.getComponent(name, n, Registry.EFFECTS, TeslaCrate.get().getContainer()).createRef(name);
            effect.deserialize(n);
            addEffect(effect);
        });
        node.getNode("keys").getChildrenMap().values().forEach(n -> {
            String name = getId() + ":key:" + n.getKey();
            Key.Ref key = Serializers.getComponent(name, n, Registry.KEYS, TeslaCrate.get().getContainer()).createRef(name);
            key.deserialize(n);
            addKey(key);
        });
        node.getNode("rewards").getChildrenMap().values().forEach(n -> {
            String name = getId() + ":reward:" + n.getKey();
            Reward.Ref reward = Serializers.getComponent(name, n, Registry.REWARDS, TeslaCrate.get().getContainer()).createRef(name);
            reward.deserialize(n);
            addReward(reward);
        });
        if (getDisplayItem() == ItemStackSnapshot.NONE) {
            setDisplayItem(Utils.createItem(ItemTypes.CHEST, getName(), Lists.newArrayList()).build().createSnapshot());
        }
    }

    @Override
    public final Object getRefValue() {
        return this;
    }

    @Override
    public final Ref createRef(String id) {
        return new Ref(id, this);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "effects", Arrays.toString(effects.stream().map(e -> e.getComponent().getId() + "=" + e.getValue()).toArray()))
                .add(indent + "keys", Arrays.toString(keys.stream().map(k -> k.getComponent().getId() + "=" + k.getValue()).toArray()))
                .add(indent + "rewards", Arrays.toString(rewards.stream().map(r -> r.getComponent().getId() + "=" + r.getValue()).toArray()));
    }

    public static final class Ref extends Reference<Crate, Object> {

        private Ref(String id, Crate component) {
            super(id, component);
        }

    }

}