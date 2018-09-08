package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Referenceable;
import com.mcsimonflash.sponge.teslacrate.component.prize.CommandPrize;
import com.mcsimonflash.sponge.teslacrate.component.prize.ItemPrize;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigHolder;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public enum Config {;

    private static final Path directory = TeslaCrate.get().getDirectory();
    private static final Path configuration = directory.resolve("configuration");
    private static final Path storage = directory.resolve("storage");
    private static ConfigHolder config, crates, effects, keys, prizes, rewards, registrations, users;

    private static final Map<Location<World>, Registration> REGISTRATIONS = Maps.newHashMap();

    public static void load() {
        try {
            Registry.clear();
            REGISTRATIONS.values().forEach(Registration::stopEffects);
            REGISTRATIONS.clear();
            TeslaCrate.get().getLogger().info("&aLoading config...");
            Files.createDirectories(configuration);
            Files.createDirectories(storage);
            config = loadConfig(directory, "teslacrate.conf", true);
            crates = loadConfig(configuration, "crates.conf", true);
            effects = loadConfig(configuration, "effects.conf", true);
            keys = loadConfig(configuration, "keys.conf", true);
            prizes = loadConfig(configuration, "prizes.conf", true);
            rewards = loadConfig(configuration, "rewards.conf", true);
            registrations = loadConfig(storage, "registrations.conf", false);
            users = loadConfig(storage, "users.conf", false);
            convert("teslacrate-v2", "TeslaCrate v2", Config::convertTeslaV2);
            convert("teslacrate-v3-pr2", "TeslaCrate v3-pr2", Config::convertTeslaV3PR2);
            convert("huskycrates-v2", "HuskyCrates v2", Config::convertHuskyV2);
            loadComponents(prizes, Registry.PRIZES, "prize");
            loadComponents(rewards, Registry.REWARDS, "reward");
            loadComponents(effects, Registry.EFFECTS, "effect");
            loadComponents(keys, Registry.KEYS, "key");
            loadComponents(crates, Registry.CRATES, "crate");
            loadRegistrations();
            REGISTRATIONS.values().forEach(Registration::startEffects);
            TeslaCrate.get().getLogger().info("&aSuccessfully loaded config.");
        } catch (IOException | ConfigurationException e) {
            TeslaCrate.get().getLogger().error("&cConfiguration loading has halted.");
        }
    }

    private interface Converter {

        void convert() throws IOException, ConfigurationException;

    }

    private static void convert(String id, String name, Converter converter) throws IOException {
        if (config.getNode("converters", id).getBoolean(false)) {
            TeslaCrate.get().getLogger().info("&aStarting conversion from " + name + " to TeslaCrate v3-pr3");
            converter.convert();
            config.getNode("converters", id).setValue(false);
            saveAll();
            TeslaCrate.get().getLogger().info("&aConversion has completed successfully.");
        }
    }

    private static void saveAll() {
        config.save();
        crates.save();
        effects.save();
        keys.save();
        prizes.save();
        rewards.save();
    }

    private static ConfigHolder loadConfig(Path dir, String name, boolean asset) throws IOException {
        Path path = dir.resolve(name);
        try {
            if (asset) {
                Sponge.getAssetManager().getAsset(TeslaCrate.get(), name).get().copyToFile(path);
            } else if (Files.notExists(path)) {
                Files.createFile(path);
            }
            return ConfigHolder.of(HoconConfigurationLoader.builder().setPath(path).build());
        } catch (IOException e) {
            TeslaCrate.get().getLogger().error("&cAn unexpected error occurred initializing " + name + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static <T extends Referenceable<?>> void loadComponents(ConfigHolder config, Registry<T> registry, String type) throws ConfigurationException {
        config.getNode().getChildrenMap().values().forEach(n -> {
            try {
                T component = Serializers.getType(n, registry).create((String) n.getKey());
                component.deserialize(n);
                registry.register(component, TeslaCrate.get().getContainer());
            } catch (ConfigurationException e) {
                TeslaCrate.get().getLogger().error("&cAn unexpected error occurred loading " + type + " '" + n.getKey() + "': " + e.getMessage() + " @" + Arrays.toString(e.getNode().getPath()));
                throw e;
            }
        });
    }

    private static void loadRegistrations() {
        registrations.getNode().getChildrenMap().values().forEach(n -> {
            try {
                World world = Sponge.getServer().getWorld(n.getNode("world").getString(Sponge.getServer().getDefaultWorldName())).orElseThrow(() -> new ConfigurationException(n.getNode("world"), "No world found for name " + n.getNode("world").getString("undefined") + "."));
                Vector3i position = Serializers.deserializeVector3i(n.getNode("position"));
                Crate crate = Registry.CRATES.get(n.getNode("crate").getString("")).orElseThrow(() -> new ConfigurationException(n.getNode("crate"), "No crate found for id " + n.getNode("crate").getString("undefined") + "."));
                REGISTRATIONS.put(new Location<>(world, position), new Registration((String) n.getKey(), new Location<>(world, position.toDouble().add(0.5, 0.5, 0.5)), crate));
            } catch (ConfigurationException e) {
                TeslaCrate.get().getLogger().error("&cAn unexpected error occurred loading registration '" + n.getKey() + "': " + e.getMessage() + " @" + Arrays.toString(e.getNode().getPath()));
            }
        });
    }

    public static Optional<Registration> getRegistration(Location<World> location) {
        return Optional.ofNullable(REGISTRATIONS.get(new Location<>(location.getExtent(), location.getBlockPosition())));
    }

    public static boolean addRegistration(Registration registration) {
        Location<World> location = new Location<>(registration.getLocation().getExtent(), registration.getLocation().getBlockPosition());
        if (!REGISTRATIONS.containsKey(location)) {
            ConfigurationNode node = registrations.getNode(registration.getId());
            node.getNode("world").setValue(registration.getLocation().getExtent().getName());
            node.getNode("position").setValue(Arrays.stream(registration.getLocation().getBlockPosition().toArray()).boxed().collect(Collectors.toList()));
            node.getNode("crate").setValue(registration.getCrate().getId());
            if (registrations.save()) {
                REGISTRATIONS.put(location, registration);
                return true;
            }
        }
        return false;
    }

    public static boolean removeRegistration(Registration registration) {
        if (REGISTRATIONS.values().remove(registration)) {
            registrations.getNode(registration.getId()).setValue(null);
            return registrations.save();
        }
        return false;
    }

    public static Map<Location<World>, Registration> getAllRegistrations() {
        return Collections.unmodifiableMap(REGISTRATIONS);
    }

    public static long getCooldown(UUID uuid, Crate crate) {
        return users.getNode(uuid.toString(), "cooldowns", crate.getId()).getLong(0);
    }

    public static boolean resetCooldown(UUID uuid, Crate crate) {
        users.getNode(uuid.toString(), "cooldowns", crate.getId()).setValue(System.currentTimeMillis());
        return users.save();
    }

    public static int getStoredKeys(UUID uuid, Key key) {
        return users.getNode(uuid.toString(), "keys", key.getId()).getInt(0);
    }

    public static boolean setStoredKeys(UUID uuid, Key key, int quantity) {
        users.getNode(uuid.toString(), "keys", key.getId()).setValue(quantity);
        return users.save();
    }

    private static void convertTeslaV2() throws IOException {
        ConfigHolder commands = loadConfig(configuration, "commands.conf", false);
        ConfigHolder items = loadConfig(configuration, "items.conf", false);
        ConfigHolder particles = loadConfig(configuration, "particles.conf", false);
        commands.getNode().getChildrenMap().values().forEach(n -> convertCommand(prizes.getNode((String) n.getKey()).setValue(n)));
        items.getNode().getChildrenMap().values().forEach(n -> convertItem(prizes.getNode((String) n.getKey()).setValue(n)));
        particles.getNode().getChildrenMap().values().forEach(n -> convertParticle(effects.getNode((String) n.getKey()).setValue(n)));
        rewards.getNode().getChildrenMap().values().forEach(Config::convertReward);
        keys.getNode().getChildrenMap().values().forEach(Config::convertKey);
        crates.getNode().getChildrenMap().values().forEach(crate -> {
            Config.convertComponent(crate);
            NodeUtils.ifAttached(crate.getNode("cooldown"), n -> n.setValue(1000 * n.getLong(0)));
            NodeUtils.ifAttached(crate.getNode("gui"), n -> {
                crate.getNode("opener").setValue(n.getString("").equalsIgnoreCase("instant") ? "InstantGui" : n.getString("").equalsIgnoreCase("roulette") ? "RouletteGui" : null);
                n.setValue(null);
            });
            crate.getNode("keys").getChildrenMap().values().forEach(n -> {
                if (n.hasMapChildren()) {
                    convertKey(n);
                }
            });
            crate.getNode("particles").getChildrenMap().values().forEach(n -> {
                if (n.hasMapChildren()) {
                    convertParticle(n);
                }
                crate.getNode("effects", "passive", n.getKey()).setValue(n);
            });
            crate.getNode("particles").setValue(null);
        });
    }

    private static void convertComponent(ConfigurationNode node) {
        NodeUtils.ifAttached(node.getNode("display-name"), n -> NodeUtils.move(n, node.getNode("name")));
        NodeUtils.ifAttached(node.getNode("display-item"), Config::convertItemStack);
    }

    private static void convertItemStack(ConfigurationNode node) {
        if (node.getChildrenMap().size() == 1 && !node.getNode("id").isVirtual()) {
            node.setValue(node.getNode("id"));
        } else {
            NodeUtils.ifAttached(node.getNode("id"), n -> NodeUtils.move(n, node.getNode("type")));
            NodeUtils.ifAttached(node.getNode("keys", "display-name"), n -> NodeUtils.move(n, node.getNode("name")));
            NodeUtils.ifAttached(node.getNode("keys", "item-lore"), n -> {
                node.getNode("lore").setValue(n.getChildrenList().isEmpty() ? null : n.getChildrenList().size() == 1 ? n.getNode(0) : n);
                n.setValue(null);
            });
            if (node.getNode("keys").getChildrenMap().isEmpty()) {
                node.getNode("keys").setValue(null);
            }
        }
    }

    private static void convertCommand(ConfigurationNode node) {
        convertComponent(node);
        if (!node.getNode("value").isVirtual() || !node.getNode("source").isVirtual()) {
            node.getNode("command", "command").setValue(node.getNode("command"));
            NodeUtils.move(node.getNode("value"), node.getNode("command", "value"));
            NodeUtils.move(node.getNode("source"), node.getNode("command", "source"));
        }
    }

    private static void convertItem(ConfigurationNode node) {
        convertComponent(node);
        NodeUtils.ifAttached(node.getNode("id"), n -> NodeUtils.move(n, node.getNode("item", "type")));
        NodeUtils.ifAttached(node.getNode("data"), n -> NodeUtils.move(n, node.getNode("item", "data")));
        NodeUtils.ifAttached(node.getNode("keys"), n -> NodeUtils.move(n, node.getNode("item", "keys")));
        NodeUtils.ifAttached(node.getNode("enchantments"), n -> NodeUtils.move(n, node.getNode("item", "enchantments")));
        NodeUtils.ifAttached(node.getNode("nbt"), n -> NodeUtils.move(n, node.getNode("item", "nbt")));
        NodeUtils.ifAttached(node.getNode("quantity"), n -> NodeUtils.move(n, node.getNode("item", "quantity")));
        convertItemStack(node.getNode("item"));
    }

    private static void convertParticle(ConfigurationNode node) {
        convertComponent(node);
        NodeUtils.ifAttached(node.getNode("particle"), n -> node.getNode("particle", "type").setValue(n));
        NodeUtils.ifAttached(node.getNode("color"), n -> {
            node.getNode("particle", "color").setValue(Integer.toHexString(n.getInt(0)));
            n.setValue(null);
        });
        NodeUtils.ifAttached(node.getNode("rainbow"), n -> NodeUtils.move(n, node.getNode("particle", "rainbow")));
        NodeUtils.ifAttached(node.getNode("type"), n -> NodeUtils.move(n, node.getNode("path", "type")));
        NodeUtils.ifAttached(node.getNode("animated"), n -> NodeUtils.move(n, node.getNode("path", "animated")));
        NodeUtils.ifAttached(node.getNode("precision"), n -> NodeUtils.move(n, node.getNode("path", "precision")));
        NodeUtils.ifAttached(node.getNode("segments"), n -> NodeUtils.move(n, node.getNode("path", "segments")));
        NodeUtils.ifAttached(node.getNode("shift"), n -> NodeUtils.move(n, node.getNode("path", "shift")));
        NodeUtils.ifAttached(node.getNode("speed"), n -> NodeUtils.move(n, node.getNode("path", "speed")));
        NodeUtils.ifAttached(node.getNode("scale"), n -> NodeUtils.move(n, node.getNode("path", "scale")));
    }

    private static void convertReward(ConfigurationNode node) {
        convertComponent(node);
        node.getNode("commands").getChildrenMap().values().forEach(n -> {
            if (n.hasMapChildren()) {
                convertCommand(n);
            }
            node.getNode("prizes", n.getKey()).setValue(n);
        });
        node.getNode("commands").setValue(null);
        node.getNode("items").getChildrenMap().values().forEach(n -> {
            if (n.hasMapChildren()) {
                convertItem(n);
            }
            node.getNode("prizes", n.getKey()).setValue(n);
        });
        node.getNode("items").setValue(null);
    }

    private static void convertKey(ConfigurationNode key) {
        convertComponent(key);
        NodeUtils.ifAttached(key.getNode("item"), Config::convertItemStack);
        NodeUtils.ifVirtual(key.getNode("item"), n -> key.getNode("virtual").setValue(true));
    }

    private static void convertTeslaV3PR2() {
        prizes.getNode().getChildrenMap().values().forEach(Config::convertPrize);
        rewards.getNode().getChildrenMap().values().forEach(r -> {
            convertComponent(r);
            r.getNode("prizes").getChildrenMap().values().forEach(Config::convertPrize);
        });
        keys.getNode().getChildrenMap().values().forEach(k -> {
            convertComponent(k);
            NodeUtils.ifAttached(k.getNode("item"), Config::convertItemStack);
        });
        effects.getNode().getChildrenMap().values().forEach(Config::convertComponent);
        crates.getNode().getChildrenMap().values().forEach(c -> {
            convertComponent(c);
            c.getNode("rewards").getChildrenMap().values().forEach(r -> {
                convertComponent(r);
                r.getNode("prizes").getChildrenMap().values().forEach(Config::convertPrize);
            });
        });
    }

    private static void convertPrize(ConfigurationNode node) {
        convertComponent(node);
        if (CommandPrize.TYPE.matches(node) || node.getNode("type").getString("").equalsIgnoreCase("command")) {
            NodeUtils.ifAttached(node.getNode("server"), n -> {
                node.getNode("source").setValue(n.getBoolean(true) ? null : "player");
                n.setValue(null);
            });
            convertCommand(node);
        } else if (ItemPrize.TYPE.matches(node) || node.getNode("type").getString("").equalsIgnoreCase("item")) {
            if (node.getNode("item").getChildrenMap().size() == 1 && !node.getNode("item", "id").isVirtual()) {
                node.getNode("item").setValue(node.getNode("item", "id"));
            } else {
                node.getNode("item", "type").setValue(node.getNode("item", "id"));
            }
        }
    }

    private static void convertHuskyV2() throws IOException {
        ConfigHolder config = loadConfig(directory.getParent().resolve("huskycrates"), "crates.conf", false);
        config.getNode().getChildrenMap().values().forEach(c -> {
            ConfigurationNode crate = crates.getNode(c.getKey());
            NodeUtils.ifAttached(c.getNode("name"), n -> NodeUtils.move(n, crate.getNode("name")));
            NodeUtils.ifAttached(c.getNode("acceptedKeys"), k -> {
                if (k.hasMapChildren()) {
                    NodeUtils.move(k, crate.getNode("keys"));
                } else {
                    k.getChildrenList().forEach(n -> crate.getNode("keys", n.getString("")).setValue(1));
                }
            });
            NodeUtils.ifAttached(c.getNode("localKey"), k -> {
                convertHuskyItem(k, keys.getNode(crate.getKey() + "-k"));
                crate.getNode("keys", crate.getKey() + "-k").setValue(1);
            });
            c.getNode("slots").getChildrenList().forEach(s -> {
                ConfigurationNode reward = rewards.getNode(c.getKey() + "-r" + s.getKey());
                NodeUtils.ifAttached(s.getNode("displayItem"), n -> convertHuskyItem(n, reward.getNode("display-item")));
                s.getNode("rewards").getChildrenList().forEach(r -> {
                    if (r.hasMapChildren()) {
                        convertHuskyReward(r, reward.getNode("prizes"), prizes.getNode(reward.getKey() + "-p" + r.getKey()));
                    } else {
                        r.getChildrenList().forEach(n -> convertHuskyReward(n, reward.getNode("prizes"), prizes.getNode(reward.getKey() + "-r" + r.getKey() + "-p" + n.getKey())));
                    }
                });
                crate.getNode("rewards", reward.getKey()).setValue(s.getNode("chance"));
            });
        });
    }

    private static void convertHuskyReward(ConfigurationNode husky, ConfigurationNode reference, ConfigurationNode tesla) {
        switch (husky.getNode("type").getString("").toLowerCase()) {
            case "usercommand":
                tesla.getNode("server").setValue(false);
            case "servercommand": //fallthrough
                tesla.getNode("command").setValue(husky.getNode("data").getString("").replace("%p", "<player>"));
                break;
            case "usermessage":
                tesla.getNode("command").setValue("tell <player> " + husky.getNode("data").getString("").replace("%p", "<player>"));
                break;
            case "servermessage":
                tesla.getNode("command").setValue("plainbroadcast " + husky.getNode("data").getString("").replace("%p", "<player>"));
                break;
            case "item":
                convertHuskyItem(husky.getNode("item").isVirtual() ? tesla.getNode("display-item") : husky.getNode("item"), tesla.getNode("item"));
                return;
        }
        reference.getNode(tesla.getKey()).setValue("");
    }

    private static void convertHuskyItem(ConfigurationNode husky, ConfigurationNode tesla) {
        tesla.setValue(husky);
        NodeUtils.move(tesla.getNode("id"), tesla.getNode("type"));
        NodeUtils.move(tesla.getNode("damage"), tesla.getNode("data"));
        NodeUtils.ifAttached(tesla.getNode("count"), n -> {
            tesla.getNode("quantity").setValue(n.getInt(1) > 1 ? n.getInt(1) : null);
            n.setValue(null);
        });
    }

}