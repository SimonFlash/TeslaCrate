package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Referenceable;
import com.mcsimonflash.sponge.teslalibs.configuration.*;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

public final class Config {

    private static final Path directory = TeslaCrate.get().getDirectory();
    private static final Path configuration = directory.resolve("configuration");

    public static void load() {
        try {
            Registry.clear();
            TeslaCrate.get().getLogger().info("&aLoading config...");
            Files.createDirectories(configuration);
            loadComponents(loadConfig(configuration, "prizes.conf", true), Registry.PRIZES, "prize");
            loadComponents(loadConfig(configuration, "rewards.conf", true), Registry.REWARDS, "reward");
            loadComponents(loadConfig(configuration, "effects.conf", true), Registry.EFFECTS, "effect");
            loadComponents(loadConfig(configuration, "keys.conf", true), Registry.KEYS, "key");
            loadComponents(loadConfig(configuration, "crates.conf", true), Registry.CRATES, "crate");
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
        for (ConfigurationNode node : config.getNode().getChildrenMap().values()) {
            try {
                registry.register(Serializers.<T>getType(node, registry).create((String) node.getKey()).deserialize(node).build(), TeslaCrate.get().getContainer());
            } catch (ConfigurationException e) {
                TeslaCrate.get().getLogger().error("&cAn unexpected error occurred loading " + type + " '" + node.getKey() + "': " + e.getMessage() + " @" + Arrays.toString(e.getNode().getPath()));
                throw e;
            }
        }
    }

}
