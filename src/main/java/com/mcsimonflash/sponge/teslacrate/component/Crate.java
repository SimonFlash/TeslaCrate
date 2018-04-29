package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslacore.util.DefVal;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class Crate extends Component {

    private static final Random RANDOM = new Random();

    private Map<Key, Integer> keys = Maps.newHashMap();
    private Map<Reward, Double> rewards = Maps.newHashMap();
    private DefVal<String> announcement = DefVal.of("undefined");
    private DefVal<String> message = DefVal.of("undefined");
    private DefVal<Effects.Gui> gui = DefVal.of(Effects.Gui.NONE);
    private List<Particle> particles = Lists.newArrayList();
    private int cooldown, rollMin, rollMax;
    private boolean firework, rollAll;
    private double weightSum;

    public Crate(String name) {
        super(name);
    }

    public void give(Player player, Reward reward, Location<World> location) {
        message.ifPresent(m -> player.sendMessage(Utils.toText(m.replace("<player>", player.getName()).replace("<crate>", getDisplayName()).replace("<reward>", reward.getDisplayName()))));
        if (announcement.isPresent() && reward.isAnnounce()) {
            Sponge.getServer().getBroadcastChannel().send(Utils.toText(getAnnouncement().replace("<player>", player.getName()).replace("<crate>", getDisplayName()).replace("<reward>", reward.getDisplayName())));
        }
        if (firework) {
            Utils.spawnFirework(FireworkEffect.builder().color(Color.YELLOW).build(), location);
        }
        reward.give(player);
    }

    public boolean process(Player player, Location<World> location) {
        if (!player.hasPermission("teslacrate.crates." + getName() + ".base")) {
            TeslaCrate.sendMessage(player, "teslacrate.crate.no-permission", "crate", getDisplayName());
            return false;
        }
        if (cooldown > 0 && !player.hasPermission("teslacrate.crates." + getName() + ".no-cooldown")) {
            long time = System.currentTimeMillis() - Config.getCooldown(player, this);
            if (time < cooldown) {
                TeslaCrate.sendMessage(player, "teslacrate.crate.cooldown", "crate", getDisplayName(), "cooldown", cooldown / 1000, "time", time / 1000);
                return false;
            }
            Config.resetCooldown(player, this);
        }
        List<Key> missing = keys.entrySet().stream().filter(e -> e.getKey().check(player) < e.getValue()).map(Map.Entry::getKey).collect(Collectors.toList());
        if (!missing.isEmpty()) {
            TeslaCrate.sendMessage(player, "teslacrate.crate.missing-keys", "crate", getDisplayName(), "keys", String.join(", ", missing.stream().map(k -> TeslaCrate.get().getMessages().get("teslacrate.crate.missing-keys.key-format", player.getLocale()).arg("key", k.getDisplayName()).arg("quantity", keys.get(k)).toString()).collect(Collectors.toList())));
            return false;
        }
        keys.forEach((k, q) -> k.take(player, q));
        gui.get().run(player, this, location);
        return true;
    }

    public Reward getRandomReward() {
        List<Reward> rewards = Lists.newArrayList();
        int num = RANDOM.nextInt(rollMax - rollMin + 1) + rollMin;
        for (int i = 0; i < num; i++) {
            if (rollAll) {
                this.rewards.entrySet().stream()
                        .filter(e -> RANDOM.nextDouble() * weightSum < e.getValue())
                        .map(Map.Entry::getKey)
                        .forEach(rewards::add);
            } else {
                double random = RANDOM.nextDouble() * weightSum;
                for (Map.Entry<Reward, Double> entry : this.rewards.entrySet()) {
                    if ((random -= entry.getValue()) <= 0) {
                        rewards.add(entry.getKey());
                        break;
                    }
                }
            }
        }
        if (rewards.size() == 1) {
            return rewards.get(0);
        } else {
            Collections.shuffle(rewards);
            Reward reward = new Reward("multi-reward") {
                @Override
                public void give(Player player) {
                    rewards.forEach(r -> r.give(player));
                }
            };
            reward.setDisplayName("&e" + String.join("&6, &e", rewards.stream().map(Component::getDisplayName).collect(Collectors.toList())) + "&6");
            reward.setDisplayItem( Utils.createItem(ItemTypes.BOOK, getDisplayName(), rewards.stream().map(Component::getDisplayName).collect(Collectors.toList()), true));
            reward.setAnnounce(rewards.stream().anyMatch(Reward::isAnnounce));
            return reward;
        }
    }

    public void preview(Player player) {
        Page.builder()
                .layout(Layout.builder()
                        .setAll(Inventory.TEMPLATE.getElements())
                        .set(Inventory.PANES[1], 45, 53)
                        .build())
                .property(InventoryTitle.of(Utils.toText(getDisplayName())))
                .build(TeslaCrate.get().getContainer())
                .define(rewards.entrySet().stream()
                        .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                        .map(e -> Element.of(e.getKey().getDisplayItem()))
                        .collect(Collectors.toList()))
                .open(player, 0);
    }

    @Override
    public void deserialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        super.deserialize(node);
        setAnnouncement(node.getNode("announcement").getString());
        setMessage(node.getNode("message").getString());
        setCooldown(node.getNode("cooldown").getInt(0));
        setFirework(node.getNode("firework").getBoolean(false));
        rollAll = node.getNode("roll-all").getBoolean(false);
        rollMin = node.getNode("roll-min").getInt(1);
        rollMax = node.getNode("roll-max").getInt(1);
        if (rollMin > rollMax) {
            throw new ConfigurationNodeException(node.getNode("roll-min"), "Roll-min cannot be larger than roll-max!").asUnchecked();
        }
        setGui(Serializers.deserializeEnum(node.getNode("gui"), Effects.Gui.class, "No gui type found for name %s."));
        node.getNode("particles").getChildrenMap().values().forEach(n -> {
            Particle particle = new Particle((String) n.getKey());
            particle.deserialize(n);
            particles.add(particle);
        });
        node.getNode("keys").getChildrenMap().values().forEach(n -> {
            Key key = Serializers.deserializeChild(n, Storage.keys, () -> new Key(getName() + ":" + n.getKey()), "key");
            addKey(key, n.hasMapChildren() || n.getInt(0) <= 0 ? key.getQuantity() : n.getInt());
        });
        node.getNode("rewards").getChildrenMap().values().forEach(n -> {
            Reward reward = Serializers.deserializeChild(n, Storage.rewards, () -> new Reward(getName() + ":" + n.getKey()), "reward");
            addReward(reward, n.hasMapChildren() || n.getDouble(0) <= 0 ? reward.getWeight() : n.getDouble());
        });
        double sum = rewards.values().stream().mapToDouble(Double::valueOf).sum();
        weightSum = node.getNode("weight-sum").getDouble(sum);
        if (!rollAll && sum != weightSum) {
            throw new ConfigurationNodeException(node.getNode("rewards"), "Weight sum " + sum + " did not match the expected sum of " + weightSum + ".").asUnchecked();
        }
    }

    public void addKey(Key key, int quantity) {
        keys.put(key, quantity);
    }

    public void addReward(Reward reward, double weight) {
        rewards.put(reward, weight);
    }

    public String getAnnouncement() {
        return announcement.get();
    }

    public void setAnnouncement(String announcement) {
        this.announcement.setVal(announcement);
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String message) {
        this.message.setVal(message);
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public Effects.Gui getGui() {
        return gui.get();
    }

    public void setGui(Effects.Gui gui) {
        this.gui.setVal(gui == Effects.Gui.NONE ? null : gui);
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public boolean isFirework() {
        return firework;
    }

    public void setFirework(boolean firework) {
        this.firework = firework;
    }

    @Override
    protected ItemStack getDefaultDisplayItem() {
        return Utils.createItem(ItemTypes.CHEST, getDisplayName(), getDescription(), true);
    }

    @Override
    public List<Element> getMenuElements(Element back) {
        List<Element> elements = super.getMenuElements(back);
        elements.add(Inventory.createDetail("Announcement", getAnnouncement()));
        elements.add(Inventory.createDetail("Message", getMessage()));
        elements.add(Inventory.createDetail("Cooldown", String.valueOf(getCooldown())));
        elements.add(Inventory.createDetail("Firework", String.valueOf(isFirework())));
        elements.add(Inventory.createDetail("Gui", getGui().name().toLowerCase()));
        elements.add(Inventory.createDetail("Weight Sum", String.valueOf(weightSum)));
        elements.add(Inventory.createDetail("Roll All", String.valueOf(rollAll)));
        elements.add(Inventory.createDetail("Roll Min", String.valueOf(rollMin)));
        elements.add(Inventory.createDetail("Roll Max", String.valueOf(rollMax)));
        Element self = Inventory.createComponent(this, back);
        particles.forEach(p -> elements.add(Element.of(p.getDisplayItem(), a -> Inventory.page(p.getName(), p.getMenuElements(self), self).open(a.getPlayer(), 0))));
        keys.forEach((k, q) -> elements.add(Element.of(Utils.createItem(ItemTypes.NAME_TAG, k.getName(), "quantity=" + q, false), a -> Inventory.page(k.getName(), k.getMenuElements(self), self).open(a.getPlayer(), 0))));
        rewards.forEach((r, w) -> elements.add(Element.of(Utils.createItem(ItemTypes.BOOK, r.getName(), "weight=" + w, false), a -> Inventory.page(r.getName(), r.getMenuElements(self), self).open(a.getPlayer(), 0))));
        return elements;
    }

}