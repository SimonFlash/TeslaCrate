package com.mcsimonflash.sponge.teslacrate.managers;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.exceptions.InvalidConfigException;
import com.mcsimonflash.sponge.teslacrate.objects.config.ConfigWrapper;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class Config {

    private static Path rootDir = TeslaCrate.getPlugin().getDirectory().resolve("teslapowered").resolve("teslacrate");
    private static Path confDir = rootDir.resolve("configuration");
    private static ConfigWrapper core, commands, crates, items, keys, rewards;

    public static boolean errorComments;
    public static boolean strictChances;
    public static Text teslaPrefix = Util.toText("&8&l[&eTesla&6Crate&8&l]&r ");
    public static Text displayPrefix;

    private static boolean initializeNodes() {
        try {
            Files.createDirectories(confDir);
            core = new ConfigWrapper(rootDir.resolve("teslacrate.core"), true);
            commands = new ConfigWrapper(confDir.resolve("commands.conf"), true);
            crates = new ConfigWrapper(confDir.resolve("crates.conf"), true);
            items = new ConfigWrapper(confDir.resolve("items.conf"), true);
            keys = new ConfigWrapper(confDir.resolve("keys.conf"), true);
            rewards = new ConfigWrapper(confDir.resolve("rewards.conf"), true);
        } catch (IOException e) {
            TeslaCrate.getPlugin().getLogger().error("A config error was detected on loading!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean readConfig() {
        if (initializeNodes()) {
            displayPrefix = Util.toText(core.Node.getNode("display-prefix").getString("&8&l[&eTesla&6Crate&8&l]&r "));
            if (displayPrefix.toPlain().toLowerCase().contains("edison")) {
                TeslaCrate.getPlugin().getLogger().warn("Edison? Really? We're not having any of that here.");
                displayPrefix = teslaPrefix;
            }
            errorComments = core.Node.getNode("error-comments").getBoolean(true);
            strictChances = core.Node.getNode("strict-chances").getBoolean(false);
            try {
                loadBuilders(commands, c -> Storage.globalCommands.put(((String) c.getKey()).toLowerCase(), Builders.buildCommand(c, false)));
                loadBuilders(items, i -> Storage.globalItems.put(((String) i.getKey()).toLowerCase(), Builders.buildItem(i, false)));
                loadBuilders(keys, k -> Storage.globalKeys.put(((String) k.getKey()).toLowerCase(), Builders.buildKey(k, false)));
                loadBuilders(rewards, r -> Storage.globalRewards.put(((String) r.getKey()).toLowerCase(), Builders.buildReward(r, false)));
                loadBuilders(crates, c -> Storage.crateDirectory.put(((String) c.getKey()).toLowerCase(), Builders.buildCrate(c, false)));
            } catch (InvalidConfigException e) {
                TeslaCrate.getPlugin().getLogger().error(e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    public static void loadBuilders(ConfigWrapper cw, Consumer<CommentedConfigurationNode> builder) throws InvalidConfigException {
        try {
            cw.Node.getChildrenMap().values().forEach(builder::accept);
        } catch (InvalidConfigException e) {
            if (errorComments) {
                e.getErrNode().setComment("ERROR: " + e.getMessage());
                cw.save();
            }
            throw e;
        }
    }
}