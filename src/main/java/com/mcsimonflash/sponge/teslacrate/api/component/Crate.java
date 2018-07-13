package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.message.Message;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Arrays;
import java.util.List;

public abstract class Crate extends Referenceable<Object> {

    private long cooldown = 0;
    private String message;
    private String announcement;
    private final List<Effect.Ref> effects = Lists.newArrayList();
    private final List<Key.Ref> keys = Lists.newArrayList();
    private final List<Reward.Ref> rewards = Lists.newArrayList();

    protected Crate(String name) {
        super(name);
    }

    public final long getCooldown() {
        return cooldown;
    }

    public final void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public final String getMessage() {
        return message;
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    public final String getAnnouncement() {
        return announcement;
    }

    public final void setAnnouncement(String announcement) {
        this.announcement = announcement;
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
                if (!message.isEmpty()) {
                    player.sendMessage(Message.of(message).args("player", player.getName(), "crate", Utils.toString(getName()), "reward", Utils.toString(reward.getComponent().getName())).toText());
                }
                if (!announcement.isEmpty() && reward.getComponent().isAnnounce()) {
                    Sponge.getServer().getBroadcastChannel().send(Message.of(announcement).args("player", player.getName(), "crate", Utils.toString(getName()), "reward", Utils.toString(reward.getComponent().getName())).toText());
                }
                reward.getComponent().give(player);
                effects.forEach(e -> e.run(player, location));
                return;
            }
        }
    }

    public void preview(Player player, Location<World> location) {
        //TODO:
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        setCooldown(node.getNode("cooldown").getLong(0));
        setMessage(node.getNode("message").getString(""));
        setAnnouncement(node.getNode("announcement").getString(""));
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
        super.deserialize(node);
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
    protected ItemStack.Builder createDisplayItem(Object value) {
        return Utils.createItem(ItemTypes.CHEST, getName(), Lists.newArrayList(getDescription()));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "effects", Arrays.toString(effects.stream().map(e -> e.getComponent().getId() + "=" + e.getValue()).toArray()))
                .add(indent + "keys", Arrays.toString(keys.stream().map(k -> k.getComponent().getId() + "=" + k.getValue()).toArray()))
                .add(indent + "rewards", Arrays.toString(rewards.stream().map(r -> r.getComponent().getId() + "=" + r.getValue()).toArray()));
    }

    private static final class Ref extends Reference<Crate, Object> {

        private Ref(String id, Crate component) {
            super(id, component);
        }

    }

}