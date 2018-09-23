package com.mcsimonflash.sponge.teslacrate.api.component;

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
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Crate<T extends Crate<T>> extends Component<T, Void> {

    private String message = "";
    private String announcement = "";
    private Opener opener = StandardOpener.INSTANCE;
    private final Map<Effect.Action, List<Reference<? extends Effect, ?>>> effects = Maps.newEnumMap(Effect.Action.class);
    private final List<Reference<? extends Key, Integer>> keys = Lists.newArrayList();
    private final List<Reference<? extends Reward, Double>> rewards = Lists.newArrayList();

    protected Crate(String name) {
        super(name);
    }

    public List<Reference<? extends Effect, ?>> getEffects(Effect.Action action) {
        return effects.getOrDefault(action, ImmutableList.of());
    }

    public List<Reference<? extends Key, Integer>> getKeys() {
        return keys;
    }

    @Override @Nullable
    public final Void getValue() {
        return null;
    }

    public void open(Player player, Location<World> location) {
        runEffects(player, location, Effect.Action.ON_OPEN);
        opener.open(player, location, this);
    }

    public void give(Player player, Location<World> location, Reference<? extends Reward, Double> reward) {
        if (!message.isEmpty()) {
            player.sendMessage(Message.of(message).args("player", player.getName(), "crate", Utils.toString(getName()), "reward", Utils.toString(reward.getComponent().getName())).toText());
        }
        if (!announcement.isEmpty() && reward.getComponent().isAnnounce()) {
            Sponge.getServer().getBroadcastChannel().send(Message.of(announcement).args("player", player.getName(), "crate", Utils.toString(getName()), "reward", Utils.toString(reward.getComponent().getName())).toText());
        }
        reward.getComponent().give(player);
        runEffects(player, location, Effect.Action.ON_RECEIVE);
    }

    public void preview(Player player, Location<World> location) {
        runEffects(player, location, Effect.Action.ON_PREVIEW);
        Inventory.page(getName(), rewards.stream().map(Reference::getDisplayItem).map(Element::of).collect(Collectors.toList()), Inventory.CLOSE).open(player);
    }

    public Reference<? extends Reward, Double> selectReward(Player player) {
        double rand = Math.random() * rewards.stream().mapToDouble(Reference::getValue).sum();
        for (Reference<? extends Reward, Double> reward : rewards) {
            if ((rand -= reward.getValue()) <= 0) {
                return reward;
            }
        }
        throw new IllegalStateException("Attempted to retrieve a reward from a crate without any rewards.");
    }

    private void runEffects(Player player, Location<World> location, Effect.Action action) {
        effects.getOrDefault(action, ImmutableList.of()).forEach(e -> e.getComponent().run(player, location, e.getValue()));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        message = node.getNode("message").getString("");
        announcement = node.getNode("announcement").getString("");
        NodeUtils.ifAttached(node.getNode("opener"), n -> opener = Serializers.opener(n));
        for (Effect.Action action : Effect.Action.values()) {
            node.getNode("effects", action.name().toLowerCase().replace("_", "-")).getChildrenMap().values().forEach(n -> {
                String id = getId() + ":effect:" + n.getKey();
                Effect<?, ?> effect = Serializers.getComponent(id, n, Registry.EFFECTS, TeslaCrate.get().getContainer());
                effects.computeIfAbsent(action, k -> Lists.newArrayList()).add(effect.createReference(id, n));
            });
        }
        node.getNode("keys").getChildrenMap().values().forEach(n -> {
            String id = getId() + ":key:" + n.getKey();
            Key<?> key = Serializers.getComponent(id, n, Registry.KEYS, TeslaCrate.get().getContainer());
            keys.add(key.createReference(id, n));
        });
        node.getNode("rewards").getChildrenMap().values().forEach(n -> {
            String id = getId() + ":reward:" + n.getKey();
            Reward<?> reward = Serializers.getComponent(id, n, Registry.REWARDS, TeslaCrate.get().getContainer());
            rewards.add(reward.createReference(id, n));
        });
        rewards.sort(Comparator.comparing(Reference::getValue));
        super.deserialize(node);
    }

    @Override
    protected Void deserializeValue(ConfigurationNode node) {
        return null;
    }

    @Override
    protected ItemStackSnapshot createDisplayItem(Void value) {
        return Utils.createItem(ItemTypes.CHEST, getName()).build().createSnapshot();
    }

}