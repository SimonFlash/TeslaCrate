package com.mcsimonflash.sponge.teslacrate.managers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.objects.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.CrateLocation;
import com.mcsimonflash.sponge.teslacrate.objects.rewards.CrateReward;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Map;

public class Util {

    public static Text prefix = Util.toText("&8&l[&eTesla&6Crate&8&l]&r ");
    public static Text displayPrefix = Util.toText("&8&l[&eTesla&6Crate&8&l]&r ");

    private static Map<String, Crate> crateDirectory = Maps.newHashMap();
    private static Map<CrateLocation, Crate> crateRegistry = Maps.newHashMap();
    private static Map<String, CrateReward> globalRewards = Maps.newHashMap();
    private static Map<Player, Long> clickCooldowns = Maps.newHashMap();

    /**
     * GENERAL
     */

    public static Text toText(String msg) {
        return TextSerializers.FORMATTING_CODE.deserialize(msg);
    }

    public static List<Text> toText(List<String> msgs) {
        List<Text> texts = Lists.newArrayList();
        for (String msg : msgs) {
            texts.add(Util.toText(msg));
        }
        return texts;
    }

    public static void clearStorage() {
        crateDirectory.clear();
        crateRegistry.clear();
        globalRewards.clear();
        clickCooldowns.clear();
    }

    public static boolean notSpamClick(Player player) {
        if (!clickCooldowns.containsKey(player) || (System.nanoTime() - clickCooldowns.get(player)) / 1e6 >= 200) {
            clickCooldowns.put(player, System.nanoTime());
            return true;
        }
        return false;
    }

    /**
     * ACCESS
     */

    public static Crate getStoredCrate(String crateName) {
        return crateDirectory.get(crateName);
    }

    public static Map<String, Crate> getCrateDirectory() {
        return crateDirectory;
    }

    public static Crate getRegisteredCrate(CrateLocation crateLoc) {
        return crateRegistry.get(crateLoc);
    }

    public static Map<CrateLocation, Crate> getCrateRegistry() {
        return crateRegistry;
    }

    public static CrateReward getGlobalReward(String name) {
        return globalRewards.get(name);
    }

    /**
     * SETUP
     */

    public static void storeCrate(Crate crate) {
        crateDirectory.put(crate.Name, crate);
    }

    public static void registerCrate(CrateLocation crateLoc, Crate crate) {
        crateRegistry.put(crateLoc, crate);
        Config.saveLoc(crateLoc, crate.Name);
    }

    public static void unrigisterCrate(CrateLocation crateLoc) {
        crateRegistry.remove(crateLoc);
        Config.saveLoc(crateLoc, null);
    }

    public static void addGlobalReward(String name, CrateReward reward) {
        globalRewards.put(name, reward);
    }
}
