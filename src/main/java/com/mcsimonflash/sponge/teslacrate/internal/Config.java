package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.*;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Referenceable;
import com.mcsimonflash.sponge.teslacrate.component.crate.Crate;
import com.mcsimonflash.sponge.teslalibs.configuration.*;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public final class Config {

    private static final Path directory = TeslaCrate.get().getDirectory();
    private static final Path configuration = directory.resolve("configuration");
    private static final Path storage = directory.resolve("storage");
    private static ConfigHolder registrations;

    private static final Map<Location<World>, Registration> REGISTRATIONS = Maps.newHashMap();

    public static void load() {
        try {
            Registry.clear();
            TeslaCrate.get().getLogger().info("&aLoading config...");
            Files.createDirectories(configuration);
            Files.createDirectories(storage);
            loadComponents(loadConfig(configuration, "prizes.conf", true), Registry.PRIZES, "prize");
            loadComponents(loadConfig(configuration, "rewards.conf", true), Registry.REWARDS, "reward");
            loadComponents(loadConfig(configuration, "effects.conf", true), Registry.EFFECTS, "effect");
            loadComponents(loadConfig(configuration, "keys.conf", true), Registry.KEYS, "key");
            loadComponents(loadConfig(configuration, "crates.conf", true), Registry.CRATES, "crate");
            registrations = loadConfig(storage, "registrations.conf", false);
            loadRegistrations();
            TeslaCrate.get().getLogger().info("&aSuccessfully loaded config.");
        } catch (IOException | ConfigurationException e) {
            TeslaCrate.get().getLogger().error("&cConfiguration loading has halted.");
        }
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

    private static <T extends Referenceable<? extends T, ?>> void loadComponents(ConfigHolder config, Registry<T> registry, String type) throws ConfigurationException {
        config.getNode().getChildrenMap().values().forEach(n -> {
            try {
                registry.register(Serializers.<T>getType(n, registry).create((String) n.getKey()).deserialize(n).build(), TeslaCrate.get().getContainer());
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

}
