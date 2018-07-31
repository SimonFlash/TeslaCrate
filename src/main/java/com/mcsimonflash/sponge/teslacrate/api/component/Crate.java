package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.opener.Opener;
import com.mcsimonflash.sponge.teslacrate.component.opener.StandardOpener;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Crate extends Referenceable<Object> {

    private long cooldown = 0;
    private String message = "";
    private String announcement = "";
    private Opener opener = StandardOpener.INSTANCE;
    private final Map<Effect.Trigger, List<Effect.Ref>> effects = Maps.newEnumMap(Effect.Trigger.class);
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

    public final Opener getOpener() {
        return opener;
    }

    public final void setOpener(Opener opener) {
        this.opener = opener;
    }

    public final Map<Effect.Trigger, List<Effect.Ref>> getEffects() {
        return effects;
    }

    public final void addEffect(Effect.Trigger trigger, Effect.Ref ref) {
        effects.computeIfAbsent(trigger, t -> Lists.newArrayList()).add(ref);
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
        effects.getOrDefault(Effect.Trigger.ON_OPEN, ImmutableList.of()).forEach(e -> e.run(player, location));
        opener.open(player, location, this);
    }

    public Reward.Ref selectReward(Player player) {
        double rand = Math.random() * getRewards().stream().mapToDouble(Reference::getValue).sum();
        for (Reward.Ref reward : getRewards()) {
            if ((rand -= reward.getValue()) <= 0) {
                return reward;
            }
        }
        throw new IllegalStateException("Attempted to retrieve a reward from a crate without any rewards.");
    }

    public void give(Player player, Location<World> location, Reward.Ref reward) {
        if (!message.isEmpty()) {
            player.sendMessage(Message.of(message).args("player", player.getName(), "crate", Utils.toString(getName()), "reward", Utils.toString(reward.getComponent().getName())).toText());
        }
        if (!announcement.isEmpty() && reward.getComponent().isAnnounce()) {
            Sponge.getServer().getBroadcastChannel().send(Message.of(announcement).args("player", player.getName(), "crate", Utils.toString(getName()), "reward", Utils.toString(reward.getComponent().getName())).toText());
        }
        reward.getComponent().give(player);
        effects.getOrDefault(Effect.Trigger.ON_RECEIVE, ImmutableList.of()).forEach(e -> e.run(player, location));
    }

    public void preview(Player player, Location<World> location) {
        effects.getOrDefault(Effect.Trigger.ON_PREVIEW, ImmutableList.of()).forEach(e -> e.run(player, location));
        Inventory.page(getName(), rewards.stream().map(Component::getDisplayItem).map(Element::of).collect(Collectors.toList()), Inventory.CLOSE).open(player);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        setCooldown(node.getNode("cooldown").getLong(0));
        setMessage(node.getNode("message").getString(""));
        setAnnouncement(node.getNode("announcement").getString(""));
        NodeUtils.ifAttached(node.getNode("opener"), n -> setOpener(Serializers.deserializeOpener(n)));
        for (Effect.Trigger trigger : Effect.Trigger.values()) {
            node.getNode("effects", trigger.name().toLowerCase().replace("_", "-")).getChildrenMap().values().forEach(n -> {
                String id = getId() + ":effect:" + n.getKey();
                Effect.Ref effect = Serializers.getComponent(id, n, Registry.EFFECTS, TeslaCrate.get().getContainer()).createRef(id);
                effect.deserialize(n);
                addEffect(trigger, effect);
            });
        }
        node.getNode("keys").getChildrenMap().values().forEach(n -> {
            String id = getId() + ":key:" + n.getKey();
            Key.Ref key = Serializers.getComponent(id, n, Registry.KEYS, TeslaCrate.get().getContainer()).createRef(id);
            key.deserialize(n);
            addKey(key);
        });
        node.getNode("rewards").getChildrenMap().values().forEach(n -> {
            String id = getId() + ":reward:" + n.getKey();
            Reward.Ref reward = Serializers.getComponent(id, n, Registry.REWARDS, TeslaCrate.get().getContainer()).createRef(id);
            reward.deserialize(n);
            addReward(reward);
        });
        rewards.sort(Comparator.comparing(Reference::getValue));
        super.deserialize(node);
    }

    @Override
    protected ItemStack.Builder createDisplayItem(Object value) {
        return Utils.createItem(ItemTypes.CHEST, getName(), Lists.newArrayList(getDescription()));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "effects", "{" + String.join(", ", Arrays.stream(Effect.Trigger.values())
                        .filter(effects::containsKey)
                        .map(t -> t.name().toLowerCase().replace("_", "-") + "=" + Arrays.toString(effects.get(t).stream()
                                .map(e -> e.getComponent().getId() + "=" + e.getValue()).toArray()))
                        .collect(Collectors.toList())) + "}")
                .add(indent + "keys", Arrays.toString(keys.stream().map(k -> k.getComponent().getId() + "=" + k.getValue()).toArray()))
                .add(indent + "rewards", Arrays.toString(rewards.stream().map(r -> r.getComponent().getId() + "=" + r.getValue()).toArray()));
    }

    @Override
    public final Object getRefValue() {
        return this;
    }

    @Override
    public final Ref createRef(String id) {
        return new Ref(id, this);
    }

    private static final class Ref extends Reference<Crate, Object> {

        private Ref(String id, Crate component) {
            super(id, component);
        }

        @Override
        public final Object deserializeValue(ConfigurationNode node) {
            return getComponent();
        }

    }

}