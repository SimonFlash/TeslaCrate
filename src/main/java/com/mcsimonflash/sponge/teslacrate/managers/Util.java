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

    public static CommentedConfigurationNode loadNode(Path root, String file) throws IOException {
        Path path = root.resolve(file);
        try {
            if (Files.notExists(path)) {
                Sponge.getAssetManager().getAsset(TeslaCrate.getPlugin(), file).get().copyToFile(path);
            }
            return HoconConfigurationLoader.builder().setPath(path).build().load();
        } catch (IOException e) {
            TeslaCrate.getPlugin().getLogger().error("Error loading config! File:[" + file + "]");
            throw e;
        }
    }

    public static void initialize() {
        Storage.clearCache();
        Config.readConfig();
        Storage.readStorage();
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
                        crate.process(player);
                        Storage.setCooldown(player, crate);
                    } else {
                        List<String> keyDisplays = Lists.newArrayList();
                        missingKeys.forEach(k -> keyDisplays.add(k.DisplayName + " &7(Uses x" + crate.Keys.get(k) + ")"));
                        player.sendMessage(Config.displayPrefix.concat(Util.toText("&7Oh no! You're missing the following key" + (missingKeys.size() == 1 ? "" : "s") + ": &f" + String.join(", &f", keyDisplays) + "&7!")));
                    }
                } else {
                    player.sendMessage(Config.displayPrefix.concat(Util.toText("&7You do not have permission to use this crate!")));
                }
            }
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
