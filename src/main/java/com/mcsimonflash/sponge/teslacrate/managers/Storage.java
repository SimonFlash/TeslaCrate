package com.mcsimonflash.sponge.teslacrate.managers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.objects.config.ConfigWrapper;
import com.mcsimonflash.sponge.teslacrate.objects.crates.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.crates.CrateLocation;
import com.mcsimonflash.sponge.teslacrate.objects.keys.Key;
import com.mcsimonflash.sponge.teslacrate.objects.rewards.Reward;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
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
    private static ConfigWrapper locations, players;

    public static Map<String, Crate> crateDirectory = Maps.newHashMap();
    public static Map<CrateLocation, Crate> crateRegistry = Maps.newHashMap();
    public static Map<String, Key> keyDirectory = Maps.newHashMap();
    public static Map<String, String> globalCommands = Maps.newHashMap();
    public static Map<String, ItemStack> globalItems = Maps.newHashMap();
    public static Map<String, Key> globalKeys = Maps.newHashMap();
    public static Map<String, Reward> globalRewards = Maps.newHashMap();
    public static Map<Player, Long> clickCooldowns = Maps.newHashMap();

    private static boolean initializeNodes() {
        try {
            Files.createDirectories(storDir);
            locations = new ConfigWrapper(storDir.resolve("locations.stor"), false);
            players = new ConfigWrapper(storDir.resolve("players.stor"), false);
        } catch (IOException e) {
            TeslaCrate.getPlugin().getLogger().error("Unable to load storage files!");
            e.printStackTrace();
            return false;
        }
        return true;
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

    public static boolean readStorage() {
        if (initializeNodes()) {
            locations.Node.getChildrenMap().values().forEach(Storage::loadLocations);
            return true;
        }
        return false;
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
        locations.Node.getNode(crate.Name, crateLoc.print()).setValue(crateRegistry.containsKey(crateLoc) ? "" : null);
        return locations.save();
    }

    public static long getCooldown(User user, Crate crate) {
        return players.Node.getNode(user.getUniqueId().toString(), "cooldowns", crate.Name).getLong(0);
    }

    public static boolean setCooldown(User user, Crate crate) {
        players.Node.getNode(user.getUniqueId().toString(), "cooldowns", crate.Name).setValue(System.nanoTime());
        return players.save();
    }

    public static int getStoredKeys(User user, Key key) {
        return players.Node.getNode(user.getUniqueId().toString(), "keys", key.Name).getInt(0);
    }

    public static boolean setStoredKeys(User user, Key key, int quantity) {
        players.Node.getNode(user.getUniqueId(), "keys", key.Name).setValue(quantity == 0 ? null : quantity);
        return players.save();
    }

    public static List<Text> getAllStoredKeys(User user) {
        List<Text> keys = Lists.newArrayList();
        int c = 1;
        for (CommentedConfigurationNode keyNode : players.Node.getNode(user.getUniqueId(), "keys").getChildrenMap().values()) {
            if (keyDirectory.containsKey(keyNode.getKey())) {
                keys.add(Util.toText("&6" + c++ + ": &e" + keyDirectory.get(keyNode.getKey()).DisplayName + "&7 x" + keyNode.getInt(0)));
            } else {
                TeslaCrate.getPlugin().getLogger().error("Attempted to load keys for unknown key! | Key:[" + keyNode.getKey() + "] User:[" + user.getUniqueId() + " (" + user.getName() + ")]");
            }
        }
        return keys.isEmpty() ? Lists.newArrayList(Util.toText("&7No keys found!")) : keys;
    }
}