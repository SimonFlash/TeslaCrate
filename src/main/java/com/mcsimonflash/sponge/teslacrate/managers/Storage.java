package com.mcsimonflash.sponge.teslacrate.managers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.objects.crates.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.crates.CrateLocation;
import com.mcsimonflash.sponge.teslacrate.objects.keys.Key;
import com.mcsimonflash.sponge.teslacrate.objects.rewards.Reward;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Storage {

    private static Path storDir = TeslaCrate.getPlugin().getDirectory().resolve("teslapowered").resolve("teslacrate").resolve("storage");
    private static Path locationsStor = storDir.resolve("locations.stor"), playersStor = storDir.resolve("players.stor");
    private static ConfigurationLoader<CommentedConfigurationNode> locationsLoader, playersLoader;
    private static CommentedConfigurationNode locationsNode, playersNode;

    public static Map<String, Crate> crateDirectory = Maps.newHashMap();
    public static Map<CrateLocation, Crate> crateRegistry = Maps.newHashMap();
    public static Map<String, Key> keyDirectory = Maps.newHashMap();
    public static Map<String, String> globalCommands = Maps.newHashMap();
    public static Map<String, ItemStack> globalItems = Maps.newHashMap();
    public static Map<String, Key> globalKeys = Maps.newHashMap();
    public static Map<String, Reward> globalRewards = Maps.newHashMap();
    public static Map<Player, Long> clickCooldowns = Maps.newHashMap();

    private static void initializeNodes() {
        try {
            Files.createDirectories(storDir);
            if (Files.notExists(locationsStor)) {
                Files.createFile(locationsStor);
            }
            if (Files.notExists(playersStor)) {
                Files.createFile(playersStor);
            }
            locationsLoader = HoconConfigurationLoader.builder().setPath(locationsStor).build();
            playersLoader = HoconConfigurationLoader.builder().setPath(playersStor).build();
            locationsNode = locationsLoader.load();
            playersNode = playersLoader.load();
        } catch (IOException e) {
            TeslaCrate.getPlugin().getLogger().error("Unable to load storage files! Error:[" + e.getMessage() + "]");
            e.printStackTrace();
        }
    }
    private static boolean saveLocationsNode() {
        try {
            locationsLoader.save(locationsNode);
            return true;
        } catch (IOException e) {
            TeslaCrate.getPlugin().getLogger().error("locations.stor could not be saved!");
            e.printStackTrace();
            return false;
        }
    }
    private static boolean savePlayersNode() {
        try {
            playersLoader.save(playersNode);
            return true;
        } catch (IOException e) {
            TeslaCrate.getPlugin().getLogger().error("players.stor could not be saved!");
            e.printStackTrace();
            return false;
        }
    }

    public static void clearCache() {
        crateDirectory.clear();
        crateRegistry.clear();
        keyDirectory.clear();
        globalCommands.clear();
        globalItems.clear();
        globalKeys.clear();
        globalRewards.clear();
        clickCooldowns.clear();
    }

    public static void readStorage() {
        initializeNodes();
        locationsNode.getChildrenMap().values().forEach(Storage::loadLocations);
    }

    public static void loadLocations(CommentedConfigurationNode crateNode) {
        String crateName = (String) crateNode.getKey();
        if (crateDirectory.containsKey(crateName)) {
            try {
                crateNode.getChildrenMap().values().forEach(l -> crateRegistry.put(CrateLocation.buildCrateLoc((String) l.getKey()), crateDirectory.get(crateName)));
            } catch (IllegalArgumentException e) {
                TeslaCrate.getPlugin().getLogger().error(e.getMessage() + " Crate:[" + crateName + "]");
            }
        } else {
            TeslaCrate.getPlugin().getLogger().error("Unable to locate crate! | Crate:[" + crateName + "]");
        }
    }

    public static boolean writeLocation(Crate crate, CrateLocation crateLoc) {
        locationsNode.getNode(crate.Name, crateLoc.print()).setValue(crateRegistry.containsKey(crateLoc) ? "" : null);
        return saveLocationsNode();
    }

    public static long getCooldown(User user, Crate crate) {
        return playersNode.getNode(user.getUniqueId().toString(), "cooldowns", crate.Name).getLong(0);
    }

    public static boolean setCooldown(User user, Crate crate) {
        playersNode.getNode(user.getUniqueId().toString(), "cooldowns", crate.Name).setValue(System.nanoTime());
        return savePlayersNode();
    }

    public static int getStoredKeys(User user, Key key) {
        return playersNode.getNode(user.getUniqueId().toString(), "keys", key.Name).getInt(0);
    }

    public static boolean setStoredKeys(User user, Key key, int quantity) {
        playersNode.getNode(user.getUniqueId(), "keys", key.Name).setValue(quantity == 0 ? null : quantity);
        return savePlayersNode();
    }

    public static List<Text> getAllStoredKeys(User user) {
        List<Text> keys = Lists.newArrayList();
        int c = 1;
        for (CommentedConfigurationNode keyNode : playersNode.getNode(user.getUniqueId(), "keys").getChildrenMap().values()) {
            if (keyDirectory.containsKey(keyNode.getKey())) {
                keys.add(Util.toText("&6" + c++ + ": &e" + keyDirectory.get(keyNode.getKey()).DisplayName + "&7 x" + keyNode.getInt(0)));
            } else {
                TeslaCrate.getPlugin().getLogger().error("Attempted to load keys for unknown key! | Key:[" + keyNode.getKey() + "] User:[" + user.getUniqueId() + " (" + user.getName() + ")]");
            }
        }
        if (keys.isEmpty()) {
            keys.add(Util.toText("&7No keys found!"));
        }
        return keys;
    }
}