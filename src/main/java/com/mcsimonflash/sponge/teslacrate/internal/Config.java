package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigHolder;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Config {

    private static final Path directory = TeslaCrate.get().getDirectory();
    private static final Path configuration = directory.resolve("configuration");
    private static final Path storage = directory.resolve("storage");
    private static ConfigHolder core, commands, crates, items, keys, particles, rewards, locations, players;
    private static final ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(TypeSerializers.getDefaultSerializers().newChild()
            .registerType(TypeToken.of(Text.class), new TypeSerializer<Text>() {
                @Override
                public Text deserialize(TypeToken<?> type, ConfigurationNode value) {
                    return TextSerializers.FORMATTING_CODE.deserialize(value.getString());
                }
                @Override
                public void serialize(TypeToken<?> type, Text obj, ConfigurationNode value) {
                    value.setValue(TextSerializers.FORMATTING_CODE.serialize(obj));
                }
            }));
    private static final Pattern VECTOR = Pattern.compile("\\((?<x>[-+]?[0-9]+), ?(?<y>[-+]?[0-9]+), ?(?<z>[-+]?[0-9]+)\\)");

    static boolean convertReference, customSerialization, errorComments;
    static int particleRefresh;

    public static void load() {
        Storage.commands.clear();
        Storage.crates.clear();
        Storage.items.clear();
        Storage.keys.clear();
        Storage.particles.clear();
        Storage.rewards.clear();
        Storage.registry.values().forEach(Registration::stopParticles);
        Storage.registry.clear();
        try {
            TeslaCrate.get().getLogger().info("&aLoading Configuration.");
            core = createConfig(directory, "teslacrate.conf", true);
            commands = createConfig(configuration, "commands.conf", true);
            crates = createConfig(configuration, "crates.conf", true);
            items = createConfig(configuration, "items.conf", true);
            keys = createConfig(configuration, "keys.conf", true);
            particles = createConfig(configuration, "particles.conf", true);
            rewards = createConfig(configuration, "rewards.conf", true);
            locations = createConfig(storage, "locations.conf", false);
            players = createConfig(storage, "players.conf", false);
            customSerialization = core.getNode("custom-serialization").getBoolean(true);
            errorComments = core.getNode("error-comments").getBoolean(false);
            particleRefresh = core.getNode("particle-refresh").getInt(20);
            if (particleRefresh <= 0) {
                TeslaCrate.get().getLogger().error("The particle refresh rate must be greater than 0. Reverting to 20.");
                particleRefresh = 20;
            }
            if (core.getNode("convert", "huskycrates").getBoolean(false)) {
                convertReference = core.getNode("convert", "reference").getBoolean(true);
                Path path = directory.getParent().resolve("huskycrates").resolve("huskycrates.conf");
                if (Files.exists(path)) {
                    TeslaCrate.get().getLogger().warn("Attempting to convert HuskyCrates config.");
                    HoconConfigurationLoader.builder().setPath(path).build().load().getNode("crates").getChildrenMap().values().forEach(Config::convertHuskyCrate);
                    commands.save();
                    items.save();
                    keys.save();
                    rewards.save();
                    crates.save();
                    TeslaCrate.get().getLogger().warn("Conversion of HuskyCrates config complete.");
                } else {
                    TeslaCrate.get().getLogger().warn("Attempted to convert HuskyCrates config, but no config was found at /config/huskycrates/huskycrates.conf");
                }
                core.getNode("convert", "huskycrates").setValue(false);
                core.save();
            }
            items.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Item((String) n.getKey()), n, Storage.items));
            commands.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Command((String) n.getKey()), n, Storage.commands));
            rewards.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Reward((String) n.getKey()), n, Storage.rewards));
            keys.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Key((String) n.getKey()), n, Storage.keys));
            particles.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Particle((String) n.getKey()), n, Storage.particles));
            crates.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Crate((String) n.getKey()), n, Storage.crates));
            loadLocations();
            TeslaCrate.get().getLogger().info("&aLoading Complete");
        } catch (IOException e) {
            TeslaCrate.get().getLogger().error("&cUnable to initialize configuration: " + e.getMessage());
        } catch (ConfigurationNodeException.Unchecked e) {
            TeslaCrate.get().getLogger().error("&cConfiguration loading has been halted.");
        }
    }

    private static ConfigHolder createConfig(Path directory, String name, boolean asset) throws IOException {
        try {
            Files.createDirectories(directory);
            Path path = directory.resolve(name);
            if (asset) {
                TeslaCrate.get().getContainer().getAsset(name).get().copyToFile(path);
            } else if (Files.notExists(path)) {
                Files.createFile(path);
            }
            return ConfigHolder.of(HoconConfigurationLoader.builder().setPath(path).setDefaultOptions(options).build());
        } catch (IOException e) {
            TeslaCrate.get().getLogger().error("&cUnable to load config file " + name + ".");
            throw e;
        }

    }

    private static void error(ConfigurationNode node, String prefix, String message) {
        if (errorComments) {
            NodeUtils.tryComment(node, "ERROR: " + message);
        }
        TeslaCrate.get().getLogger().error(prefix + message);
    }

    private static <T extends Component> void loadComponent(T component, ConfigurationNode node, Map<String, T> storage) throws ConfigurationNodeException.Unchecked {
        try {
            component.deserialize(node);
            storage.put(component.getName().toLowerCase(), component);
        } catch (ConfigurationNodeException.Unchecked e) {
            error(e.getCause().getNode(), "&cError loading component " + component.getName() + " in " + component.getClass().getSimpleName().toLowerCase() + "s.conf at " + Arrays.toString(e.getCause().getNode().getPath()) + ": ", e.getCause().getMessage());
            throw e;
        }
    }

    public static void loadLocations() {
        Storage.registry.values().forEach(Registration::stopParticles);
        Storage.registry.clear();
        locations.getNode().getChildrenMap().values().forEach(n -> {
            Optional<World> optWorld = Sponge.getServer().getWorld((String) n.getKey());
            if (optWorld.isPresent()) {
                n.getChildrenMap().values().forEach(c -> {
                    Matcher matcher = VECTOR.matcher((String) c.getKey());
                    if (matcher.matches()) {
                        Crate crate = Storage.crates.get(c.getString("").toLowerCase());
                        if (crate != null) {
                            Location<World> loc = new Location<>(optWorld.get(), new Vector3d(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")), Integer.parseInt(matcher.group("z"))));
                            Storage.registry.put(loc, new Registration(loc.add(0.5, 0.5, 0.5), crate));
                        } else {
                            error(c, "&cUnable to load location " + c.getKey() + " in world " + n.getKey() + ": ", "No crate found for name " + c.getString() + ".");
                        }
                    } else {
                        error(c, "&cUnable to load location " + c.getKey() + " in world " + n.getKey() + " : ", "Invalid position format - expected (x,y,z).");
                    }
                });
            } else {
                error(n, "&cUnable to load locations: ", "No world found for name " + n.getString() + ".");
            }
        });
        Storage.registry.values().forEach(Registration::startParticles);
    }

    public static boolean setLocation(Location<World> location, Crate crate) {
        locations.getNode(location.getExtent().getName(), location.getPosition().toInt().toString()).setValue(crate.getName());
        return locations.save();
    }

    public static boolean deleteLocation(Location<World> location) {
        locations.getNode(location.getExtent().getName(), location.getPosition().toInt().toString()).setValue(null);
        return locations.save();
    }

    public static long getCooldown(User user, Crate crate) {
        return players.getNode(user.getUniqueId().toString(), "cooldowns", crate.getName()).getLong(0);
    }

    public static boolean resetCooldown(User user, Crate crate) {
        players.getNode(user.getUniqueId().toString(), "cooldowns", crate.getName()).setValue(System.currentTimeMillis());
        return players.save();
    }

    public static int getKeys(User user, Key key) {
        return players.getNode(user.getUniqueId().toString(), "keys", key.getName()).getInt(0);
    }

    public static boolean setKeys(User user, Key key, int quantity) {
        players.getNode(user.getUniqueId().toString(), "keys", key.getName()).setValue(quantity);
        return players.save();
    }

    private static void convertHuskyCrate(ConfigurationNode husky) {
        ConfigurationNode tesla = crates.getNode(((String) husky.getKey()).toLowerCase());
        tesla.getNode("display-name").setValue(husky.getNode("name"));
        tesla.getNode("message").setValue(convertHuskyLang(husky.getNode("lang", "rewardMessage").getString()));
        tesla.getNode("announcement").setValue(convertHuskyLang(husky.getNode("lang", "rewardAnnounceMessage").getString()));
        NodeUtils.ifAttached(husky.getNode("options", "keyID"), n -> {
            ConfigurationNode key = (convertReference ? keys.getNode() : tesla.getNode("keys")).getNode(((String) tesla.getKey()).toLowerCase() + ":k");
            key.getNode("display-name").setValue(husky.getNode("name"));
            key.getNode("item", "id").setValue(n.getNode("none"));
            if (convertReference) {
                tesla.getNode("keys", key.getKey()).setValue(1);
                key.removeChild("quantity");
            }
        });
        husky.getNode("items").getChildrenList().forEach(child -> {
            ConfigurationNode reward = (convertReference ? rewards.getNode() : tesla.getNode("rewards")).getNode(((String) tesla.getKey()).toLowerCase() + ":r" + child.getKey());
            reward.getNode("display-name").setValue(child.getNode("huskydata", "overrideRewardName").getString(reward.getNode("display-item", "keys", "display-name").getString()));
            convertHuskyItem(child, reward.getNode("display-item"), true);
            reward.getNode("announce").setValue(child.getNode("huskydata", "announce").getBoolean(false) || tesla.getNode("announcement").isVirtual() ? null : false);
            reward.getNode("weight").setValue(child.getNode("huskydata", "weight"));
            child.getNode("huskydata", "rewards").getChildrenList().forEach(c -> {
                String type = c.getNode("type").getString("");
                if (type.equalsIgnoreCase("item")) {
                    ConfigurationNode item = (convertReference ? items.getNode() : reward.getNode("items")).getNode(((String) reward.getKey()).toLowerCase() + ":i" + c.getKey());
                    if (c.getNode("overrideItem").isVirtual()) {
                        item.setValue(reward.getNode("display-item"));
                        if (child.getNode("huskydata", "rewards").getChildrenList().size() == 1) {
                            reward.getNode("display-item").setValue(null);
                        }
                    } else {
                        convertHuskyItem(c.getNode("overrideItem"), item, false);
                    }
                    if (convertReference) {
                        reward.getNode("items", item.getKey()).setValue(c.getNode("overrideCount").getInt(item.getInt(item.getNode("quantity").getInt(1))));
                    }
                } else if (type.equalsIgnoreCase("command")) {
                    ConfigurationNode command = (convertReference ? commands.getNode() : reward.getNode("commands")).getNode(((String) reward.getKey()).toLowerCase() + ":c" + c.getKey());
                    command.getNode("command").setValue(c.getNode("command").getString("").replace("%p", "<player>"));
                    if (convertReference) {
                        reward.getNode("commands", command.getKey()).setValue("");
                    }
                }
            });
            if (convertReference) {
                tesla.getNode("rewards", reward.getKey()).setValue(reward.getNode("weight").getDouble(0.0));
                reward.removeChild("weight");
            }
        });
    }

    private static String convertHuskyLang(@Nullable String message) {
        return message == null ? null : message
                .replace("%a|%K|%k", "<UNUSED>")
                .replace("%P|%p", "<player>")
                .replace("%C|%c", "<crate>")
                .replace("%R|%r", "<reward>");
    }

    private static void convertHuskyItem(ConfigurationNode husky, ConfigurationNode tesla, boolean display) {
        tesla.getNode("id").setValue(husky.getNode("id"));
        int quantity = husky.getNode("count").getInt(1);
        tesla.getNode("quantity").setValue(quantity == 1 ? null : quantity);
        tesla.getNode("damage").setValue(husky.getNode("damage"));
        tesla.getNode("nbt").setValue(husky.getNode("nbt"));
        List<String> lore = husky.getNode("lore").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        if (display) {
            if (tesla.getParent().getNode("display-name").isVirtual()) {
                tesla.getParent().getNode("display-name").setValue(husky.getNode("name"));
            } else {
                tesla.getNode("keys", "display-name").setValue(husky.getNode("name"));
            }
            if (lore.size() == 1) {
                tesla.getParent().getNode("description").setValue(lore.get(0));
            } else if (!lore.isEmpty()) {
                tesla.getNode("keys", "item-lore").setValue(lore);
            }
        } else {
            tesla.getNode("keys", "display-name").setValue(husky.getNode("name"));
            tesla.getNode("keys", "item-lore").setValue(lore.isEmpty() ? null : lore.size() == 1 ? lore.get(0) : lore);
        }
        tesla.getNode("keys", "enchantments").setValue(husky.getNode("enchantments"));
    }

}