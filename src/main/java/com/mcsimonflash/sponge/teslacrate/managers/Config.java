package com.mcsimonflash.sponge.teslacrate.managers;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    private static Path rootDir = TeslaCrate.getPlugin().getDirectory().resolve("teslapowered").resolve("teslacrate");
    private static Path confDir = rootDir.resolve("configuration");
    private static CommentedConfigurationNode coreNode, commandsNode, cratesNode, itemsNode, keysNode, rewardsNode;

    private static void initializeNodes() {
        try {
            Files.createDirectories(confDir);
            coreNode = Util.loadNode(rootDir, "teslacrate.core");
            commandsNode = Util.loadNode(confDir, "commands.conf");
            cratesNode = Util.loadNode(confDir, "crates.conf");
            itemsNode = Util.loadNode(confDir, "items.conf");
            keysNode = Util.loadNode(confDir, "keys.conf");
            rewardsNode = Util.loadNode(confDir, "rewards.conf");
        } catch (IOException e) {
            TeslaCrate.getPlugin().getLogger().error("Config loading has halted due to an unexpected error!");
            e.printStackTrace();
        }
    }

    public static boolean strictChances;
    public static Text teslaPrefix = Util.toText("&8&l[&eTesla&6Crate&8&l]&r ");
    public static Text displayPrefix;

    public static void readConfig() {
        initializeNodes();
        String prefix = coreNode.getNode("config", "display-prefix").getString("&8&l[&eTesla&6Crate&8&l]&r ");
        if (prefix.toLowerCase().contains("edison")) {
            TeslaCrate.getPlugin().getLogger().warn("Edison? Really? We're not having any of that here.");
            prefix = "&8&l[&eTesla&6Crate&8&l]&r ";
        }
        displayPrefix = Util.toText(prefix);
        strictChances = coreNode.getNode("config", "strict-chances").getBoolean(false);
        try {
            commandsNode.getChildrenMap().values().forEach(c -> Storage.globalCommands.put((String) c.getKey(), Builders.buildCommand(c, false)));
            itemsNode.getChildrenMap().values().forEach(i -> Storage.globalItems.put((String) i.getKey(), Builders.buildItem(i, false)));
            keysNode.getChildrenMap().values().forEach(k -> Storage.globalKeys.put((String) k.getKey(), Builders.buildKey(k, false)));
            rewardsNode.getChildrenMap().values().forEach(r -> Storage.globalRewards.put((String) r.getKey(), Builders.buildReward(r, false)));
            cratesNode.getChildrenMap().values().forEach(c -> Storage.crateDirectory.put((String) c.getKey(), Builders.buildCrate(c, false)));
        } catch (IllegalArgumentException e) {
            TeslaCrate.getPlugin().getLogger().error(e.getMessage());
        }
    }
}