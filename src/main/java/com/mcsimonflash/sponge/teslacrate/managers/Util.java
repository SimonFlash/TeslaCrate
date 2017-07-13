package com.mcsimonflash.sponge.teslacrate.managers;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.objects.crates.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.crates.CrateLocation;
import com.mcsimonflash.sponge.teslacrate.objects.keys.Key;
import com.mcsimonflash.sponge.teslacrate.objects.rewards.Reward;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

    public static HoconConfigurationLoader getLoader(Path path, boolean asset) throws IOException {
        try {
            if (Files.notExists(path)) {
                if (asset) {
                    Sponge.getAssetManager().getAsset(TeslaCrate.getPlugin(), path.getFileName().toString()).get().copyToFile(path);
                } else {
                    Files.createFile(path);
                }
            }
            return HoconConfigurationLoader.builder().setPath(path).build();
        } catch (IOException e) {
            TeslaCrate.getPlugin().getLogger().error("Error loading file! File:[" + path.getFileName().toString() + "]");
            throw e;
        }
    }

    public static boolean initialize() {
        Storage.clearCache();
        if (Config.readConfig()) {
            return Storage.readStorage();
        }
        return false;
    }

    public static Text toText(String msg) {
        return TextSerializers.FORMATTING_CODE.deserialize(msg);
    }

    public static boolean notSpamClick(Player player) {
        if (!Storage.clickCooldowns.containsKey(player) || (System.nanoTime() - Storage.clickCooldowns.get(player)) / 1e6 >= 200) {
            Storage.clickCooldowns.put(player, System.nanoTime());
            return true;
        }
        return false;
    }

    public static void processInteract(InteractEvent event, Location loc, Player player) {
        Crate crate = Storage.crateRegistry.get(new CrateLocation(player.getWorld().getName(), loc.getBlockPosition()));
        if (crate != null) {
            event.setCancelled(true);
            if (notSpamClick(player)) {
                processCrate(crate, player);
            }
        }
    }

    public static void processCrate(Crate crate, Player player) {
        if (player.hasPermission("teslacrate.crates." + crate.Name + ".base")) {
            if (crate.Cooldown > 0 && !player.hasPermission("teslacrate.crates." + crate.Name + ".nocooldown")) {
                int cooldown = (int) ((System.nanoTime() - Storage.getCooldown(player, crate)) / 1e9);
                if (cooldown < crate.Cooldown) {
                    player.sendMessage(Config.displayPrefix.concat(Util.toText("&7You must wait &f" + cooldown + " &7seconds before using this crate!")));
                    return;
                }
            }
            List<Key> missingKeys = crate.Keys.keySet().stream().filter(k -> !k.hasKeys(player, crate.Keys.get(k))).collect(Collectors.toList());
            if (missingKeys.isEmpty()) {
                crate.Keys.forEach((k, i) -> k.takeKeys(player, i));
                crate.process(player);
                if (!player.hasPermission("teslacrate.crates" + crate.Name + ".nocooldown")) {
                    Storage.setCooldown(player, crate);
                }
            } else {
                List<String> keyDisplays = Lists.newArrayList();
                missingKeys.forEach(k -> keyDisplays.add(k.DisplayName + " &7(Uses x" + crate.Keys.get(k) + ") "));
                player.sendMessage(Config.displayPrefix.concat(Util.toText("&7Oh no! You're missing the following key" + (missingKeys.size() == 1 ? "" : "s") + ": &f" + String.join(", &f", keyDisplays) + "&7!")));
            }
        } else {
            player.sendMessage(Config.displayPrefix.concat(Util.toText("&7You do not have permission to use this crate!")));
        }
    }

    public static Reward getReward(Crate crate) {
        double randSelec = crate.ChanceSum * Math.random();
        for (Reward reward : crate.Rewards.keySet()) {
            randSelec -= crate.Rewards.get(reward);
            if (randSelec <= 0) {
                return reward;
            }
        }
        throw new RuntimeException("Reached end of reward list without selection! Error code 54171 | Crate:[" + crate.Name + "]");
    }
}
