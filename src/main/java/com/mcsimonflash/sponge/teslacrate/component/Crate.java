package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslacore.util.DefVal;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Crate extends Component {

    private Map<Key, Integer> keys = Maps.newHashMap();
    private Map<Reward, Double> rewards = Maps.newHashMap();
    private DefVal<String> announcement = DefVal.of("undefined");
    private DefVal<String> message = DefVal.of("undefined");
    private DefVal<Effects.Gui> gui = DefVal.of(Effects.Gui.NONE);
    private DefVal<Effects.Particle> particle = DefVal.of(Effects.Particle.NONE);
    private int cooldown = 0;
    private boolean firework = true;

    public Crate(String name) {
        super(name);
    }

    public void give(Player player, Reward reward, Location<World> location) {
        reward.give(player);
        Utils.spawnFirework(location);
        message.ifPresent(m -> player.sendMessage(Utils.toText(m.replace("<player>", player.getName()).replace("<crate>", getDisplayName()).replace("<reward>", reward.getDisplayName()))));
        if (announcement.isPresent() && reward.isAnnounce()) {
            Sponge.getServer().getBroadcastChannel().send(Utils.toText(getAnnouncement().replace("<player>", player.getName()).replace("<crate>", getDisplayName()).replace("<reward>", reward.getDisplayName())));
        }
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
            TeslaCrate.sendMessage(player, "teslacrate.crate.missing-keys", "crate", getDisplayName(), "keys", String.join(", ", missing.stream().map(k -> TeslaCrate.getTesla().Messages.get("teslacrate.crate.missing-keys.key-format", player.getLocale()).args("key", k.getDisplayName(), "quantity", keys.get(k)).toString()).collect(Collectors.toList())));
            return false;
        }
        keys.forEach((k, q) -> k.take(player, q));
        gui.get().run(player, this, location);
        return true;
    }

    public Reward getRandomReward() {
        double random = Math.random() * rewards.values().stream().mapToDouble(Double::valueOf).sum();
        for (Map.Entry<Reward, Double> entry : rewards.entrySet()) {
            if ((random -= entry.getValue()) <= 0) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Reached end of rewards list without selection!");
    }

    public void preview(Player player) {
        Utils.page(rewards.keySet().stream().map(Component::getDisplayItem).map(Element::of).collect(Collectors.toList())).open(player);
    }

    @Override
    public void deserialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        super.deserialize(node);
        setAnnouncement(node.getNode("announcement").getString());
        setMessage(node.getNode("message").getString());
        setCooldown(node.getNode("cooldown").getInt(0));
        setFirework(node.getNode("firework").getBoolean(false));
        setGui(Serializers.deserializeEnum(node.getNode("gui"), Effects.Gui.class, "No gui type found for name %s."));
        setParticle(Serializers.deserializeEnum(node.getNode("particle"), Effects.Particle.class, "No particle type found for name %s."));
        node.getNode("keys").getChildrenMap().values().forEach(n -> {
            Key key = Serializers.deserializeChild(n, Storage.keys, () -> new Key(getName() + ":" + n.getKey()), "key");
            addKey(key, n.hasMapChildren() || n.getInt(0) <= 0 ? key.getQuantity() : n.getInt());
        });
        node.getNode("rewards").getChildrenMap().values().forEach(n -> {
            Reward reward = Serializers.deserializeChild(n, Storage.rewards, () -> new Reward(getName() + ":" + n.getKey()), "reward");
            addReward(reward, n.hasMapChildren() || n.getDouble(0) <= 0 ? reward.getWeight() : n.getDouble());
        });
    }

    @Override
    public void serialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        super.serialize(node);
        announcement.ifPresent(a -> node.getNode("announcement").setValue(a));
        message.ifPresent(m -> node.getNode("message").setValue(m));
        node.getNode("cooldown").setValue(cooldown == 0 ? null : cooldown);
        node.getNode("firework").setValue(firework ? true : null);
        gui.ifPresent(g -> Serializers.serializeEnum(node.getNode("gui"), g));
        particle.ifPresent(p -> Serializers.serializeEnum(node.getNode("particle"), p));
        keys.forEach((k, v) -> Serializers.serializeChild(node.getNode("keys"), k, v));
        rewards.forEach((r, c) -> Serializers.serializeChild(node.getNode("rewards"), r, c));
    }

    public void addKey(Key key, int quantity) {
        keys.put(key, quantity);
    }

    public void removeKey(Key key) {
        keys.remove(key);
    }

    public void addReward(Reward reward, double weight) {
        rewards.put(reward, weight);
    }

    public void removeReward(Reward reward) {
        rewards.remove(reward);
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

    public Effects.Particle getParticle() {
        return particle.get();
    }

    public void setParticle(Effects.Particle particle) {
        this.particle.setVal(particle == Effects.Particle.NONE ? null : particle);
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
    public List<Element> getMenuElements() {
        List<Element> elements = super.getMenuElements();
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Announcement", getAnnouncement(), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Message", getMessage(), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Cooldown", String.valueOf(getCooldown()), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Firework", String.valueOf(isFirework()), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Gui", getGui().name().toLowerCase(), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Particle", getParticle().name().toLowerCase(), false)));
        keys.forEach((k, q) -> elements.add(Element.of(Utils.createItem(ItemTypes.NAME_TAG, k.getName(), "quantity=" + q, false), p -> Utils.page(k.getMenuElements()).open(p))));
        rewards.forEach((r, w) -> elements.add(Element.of(Utils.createItem(ItemTypes.BOOK, r.getName(), "weight=" + w, false), p -> Utils.page(r.getMenuElements()).open(p))));
        return elements;
    }

    @Override
    public String toString() {
        return super.toString() + "\nKeys: " + String.join(", ", keys.keySet().stream().map(Component::getName).collect(Collectors.toList())) + "\nRewards: " + String.join(", ", rewards.keySet().stream().map(Component::getName).collect(Collectors.toList())) + "\nAnnounceMessage: " + getAnnouncement() + "\nPlayerMessage: " + getMessage() + "\nCooldown: " + getCooldown();
    }

}