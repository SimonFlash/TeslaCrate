package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigHolder;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.TreeTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {

    private static final Path directory = TeslaCrate.getTesla().Directory;
    private static final Path configuration = directory.resolve("configuration");
    private static final Path storage = directory.resolve("storage");
    private static ConfigHolder core, commands, crates, items, keys, rewards, locations, players;
    private static final ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(TypeSerializers.getDefaultSerializers().newChild().registerType(TypeToken.of(Text.class), new TypeSerializer<Text>() {
        @Override
        public Text deserialize(TypeToken<?> type, ConfigurationNode value) {
            return TextSerializers.FORMATTING_CODE.deserialize(value.getString());
        }

        @Override
        public void serialize(TypeToken<?> type, Text obj, ConfigurationNode value) {
            value.setValue(TextSerializers.FORMATTING_CODE.serialize(obj));
        }
    }));

    private static boolean customSerializers;
    private static boolean errorComments;

    private static void loadConfig() {
        try {
            core = createConfig(directory, "teslacrate.conf", true);
            boolean legacy = core.getNode("-legacy").getBoolean(false);
            if (legacy) {
                TeslaCrate.getTesla().Logger.warn("Attempting to convert legacy configuration.");
                try {
                    Path lDirectory = Config.directory.getParent().resolve("teslapowered").resolve("teslacrate");
                    Path lConfiguration = lDirectory.resolve("configuration");
                    Path lStorage = lDirectory.resolve("storage");
                    TeslaCrate.getTesla().Logger.warn("Copying legacy files...");
                    copy(lDirectory, "core", Config.directory, "teslacrate");
                    copy(lConfiguration, "conf", Config.configuration, "commands", "crates", "items", "keys", "rewards");
                    copy(lStorage, "stor", Config.storage, "locations", "players");
                    TeslaCrate.getTesla().Logger.warn("Successfully copied legacy files.");
                } catch (IOException e) {
                    TeslaCrate.getTesla().Logger.error("Error converting legacy config.");
                    e.printStackTrace();
                }
            }
            commands = createConfig(configuration, "commands.conf", true);
            crates = createConfig(configuration, "crates.conf", true);
            items = createConfig(configuration, "items.conf", true);
            keys = createConfig(configuration, "keys.conf", true);
            rewards = createConfig(configuration, "rewards.conf", true);
            locations = createConfig(storage, "locations.conf", false);
            players = createConfig(storage, "players.conf", false);
            if (legacy) {
                TeslaCrate.getTesla().Logger.warn("Converting legacy config...");
                rewards.getNode().getChildrenMap().values().forEach(Config::legacyReward);
                keys.getNode().getChildrenMap().values().forEach(Config::legacyKey);
                crates.getNode().getChildrenMap().values().forEach(Config::legacyCrate);
                locations.getNode().getChildrenMap().values().forEach(Config::legacyLocation);
                NodeUtils.ifAttached(core.getNode("display-prefix"), n -> NodeUtils.tryComment(n, "Discontinued in v2.0.0"));
                core.getNode("-legacy").setValue(false);
                core.save();
                rewards.save();
                keys.save();
                crates.save();
                locations.save();
                TeslaCrate.getTesla().Logger.warn("Completed legacy conversion.");
            }
        } catch (IOException e) {
            TeslaCrate.getTesla().Logger.error("Failed to initialize config files.");
            e.printStackTrace();
        }
    }

    private static ConfigHolder createConfig(Path directory, String name, boolean asset) throws IOException {
        try {
            Files.createDirectories(directory);
            Path path = directory.resolve(name);
            if (asset) {
                TeslaCrate.getTesla().Container.getAsset(name).get().copyToFile(path, false, true);
            } else if (Files.notExists(path)) {
                Files.createFile(path);
            }
            return ConfigHolder.of(HoconConfigurationLoader.builder()
                    .setPath(path)
                    .setDefaultOptions(options)
                    .build());
        } catch (IOException e) {
            TeslaCrate.getTesla().Logger.error("Unable to load config file " + name + ".");
            throw e;
        }

    }

    private static void copy(Path from, String fromExt, Path to, String... names) throws IOException {
        for (String name : names) {
            Path fromFile = from.resolve(name + "." + fromExt);
            Path toFile = to.resolve(name + ".conf");
            if (Files.exists(fromFile)) {
                Files.copy(fromFile, toFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public static void load() {
        loadConfig();
        Storage.commands.clear();
        Storage.crates.clear();
        Storage.items.clear();
        Storage.keys.clear();
        Storage.rewards.clear();
        customSerializers = core.getNode("custom-serializers").getBoolean(true);
        errorComments = core.getNode("error-comments").getBoolean(false);
        try {
            items.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Item((String) n.getKey()), n, Storage.items));
            commands.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Command((String) n.getKey()), n, Storage.commands));
            rewards.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Reward((String) n.getKey()), n, Storage.rewards));
            keys.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Key((String) n.getKey()), n, Storage.keys));
            crates.getNode().getChildrenMap().values().forEach(n -> loadComponent(new Crate((String) n.getKey()), n, Storage.crates));
        } catch (ConfigurationNodeException.Unchecked e) {
            error(e.getCause().getNode(), e.getCause().getMessage());
        }
    }

    private static <T extends Component> void loadComponent(T component, ConfigurationNode node, Map<String, T> storage) throws ConfigurationNodeException.Unchecked {
        try {
            component.deserialize(node);
            storage.put(component.getName().toLowerCase(), component);
        } catch (ConfigurationNodeException.Unchecked e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationNodeException(node, e).asUnchecked();
        }
    }

    private static final Pattern VECTOR = Pattern.compile("\\((?<x>[-+]?[0-9]+), ?(?<y>[-+]?[0-9]+), ?(?<z>[-+]?[0-9]+)\\)");

    public static void loadLocations() {
        Storage.registry.values().forEach(Registration::stopParticles);
        Storage.registry.clear();
        locations.getNode().getChildrenMap().values().forEach(n -> {
            Optional<World> optWorld = Sponge.getServer().getWorld((String) n.getKey());
            if (optWorld.isPresent()) {
                n.getChildrenMap().values().forEach(c -> {
                    Matcher matcher = VECTOR.matcher((String) c.getKey());
                    if (matcher.matches()) {
                        Crate crate = Storage.crates.get(c.getString());
                        if (crate != null) {
                            Location<World> loc = new Location<>(optWorld.get(), new Vector3d(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")), Integer.parseInt(matcher.group("z"))));
                            Storage.registry.put(loc, new Registration(loc.add(0.5, 0.5, 0.5), crate));
                        } else {
                            error(c, "No crate found for name " + c.getString() + ". Skipping this location.");
                        }
                    } else {
                        error(c, "Unable to parse vector from input " + c.getKey() + ". Skipping this location.");
                    }
                });
            } else {
                error(n, "No world found for name " + n.getKey() + ". Skipping " + n.getChildrenMap().size() + " locations.");
            }
        });
        Storage.registry.values().forEach(Registration::startParticles);
    }

    private static void error(ConfigurationNode node, String message) {
        if (errorComments) {
            NodeUtils.tryComment(node, "ERROR: " + message);
        }
        TeslaCrate.getTesla().Logger.error(message + " at " + Arrays.toString(node.getPath()));
    }

    public static void save() {
        saveComponents(commands, Storage.commands);
        saveComponents(items, Storage.items);
        saveComponents(rewards, Storage.rewards);
        saveComponents(keys, Storage.keys);
        saveComponents(crates, Storage.crates);
    }

    public static <T extends Component> void saveComponents(ConfigHolder config, Map<String, T> storage) {
        storage.values().stream().filter(Component::isGlobal).forEach(c -> c.serialize(config.getNode(c.getName())));
        config.save();
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

    public static void legacyReward(ConfigurationNode reward) {
        NodeUtils.ifAttached(reward.getNode("rewards"), node -> {
            NodeUtils.ifAttached(node.getNode("commands"), n -> NodeUtils.move(n, reward.getNode("commands")));
            NodeUtils.ifAttached(node.getNode("items"), n -> NodeUtils.move(n, reward.getNode("items")));
            NodeUtils.ifAttached(node.getNode("chance"), n -> NodeUtils.move(n, reward.getNode("weight")));
            node.setValue(null);
        });
        NodeUtils.ifAttached(reward.getNode("metadata", "announce"), n -> NodeUtils.move(n, reward.getNode("announce")));
    }

    public static void legacyKey(ConfigurationNode key) {
        NodeUtils.ifAttached(key.getNode("virtual"), n -> n.setValue(null));
        NodeUtils.ifAttached(key.getNode("physical"), node -> {
            NodeUtils.ifAttached(node.getNode("item"), n -> NodeUtils.move(n, key.getNode("item")));
            node.setValue(null);
        });
    }

    public static void legacyCrate(ConfigurationNode crate) {
        NodeUtils.ifAttached(crate.getNode("metadata"), node -> {
            NodeUtils.ifAttached(node.getNode("cooldown"), n -> NodeUtils.move(n, crate.getNode("cooldown")));
            NodeUtils.ifAttached(node.getNode("msg-announce"), n -> NodeUtils.move(n, crate.getNode("announcement")));
            NodeUtils.ifAttached(node.getNode("msg-player"), n -> NodeUtils.move(n, crate.getNode("message")));
            node.setValue(null);
        });
        crate.getNode("keys").getChildrenMap().values().stream().filter(ConfigurationNode::hasMapChildren).forEach(Config::legacyKey);
        crate.getNode("rewards").getChildrenMap().values().stream().filter(ConfigurationNode::hasMapChildren).forEach(Config::legacyReward);
    }

    public static void legacyLocation(ConfigurationNode location) {
        String[] split = ((String) location.getKey()).split(",");
        if (split.length == 4) {
            locations.getNode(split[0], "(" + split[1] + ", " + split[2] + ", " + split[3] + ")").setValue(location.getString());
            location.setValue(null);
        } else {
            TeslaCrate.getTesla().Logger.warn("Improperly formatted location found during conversion: " + location.getKey());
        }
    }

    public static boolean isCustomSerializers() {
        return customSerializers;
    }

}