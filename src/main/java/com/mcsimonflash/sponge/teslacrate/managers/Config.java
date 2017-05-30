package com.mcsimonflash.sponge.teslacrate.managers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.objects.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.CrateKeydata;
import com.mcsimonflash.sponge.teslacrate.objects.CrateLocation;
import com.mcsimonflash.sponge.teslacrate.objects.CrateMetadata;
import com.mcsimonflash.sponge.teslacrate.objects.exceptions.LocationFormatException;
import com.mcsimonflash.sponge.teslacrate.objects.rewards.CrateReward.RewardType;
import com.mcsimonflash.sponge.teslacrate.objects.rewards.*;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Config {
    private static ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
            .setPath(TeslaCrate.getPlugin().getDefaultConfig()).build();
    private static CommentedConfigurationNode rootNode;

    private static void loadConfig() {
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            TeslaCrate.getPlugin().getLogger().error("Config could not be loaded!");
        }
    }
    private static void saveConfig() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
            TeslaCrate.getPlugin().getLogger().error("Config could not be saved!");
        }
    }

    private static boolean strictChances = false;

    public static void readConfig() {
        if (Files.notExists(TeslaCrate.getPlugin().getDefaultConfig())) {
            try {
                Sponge.getAssetManager().getAsset(TeslaCrate.getPlugin(), "defaultConfig.conf").get().copyToFile(TeslaCrate.getPlugin().getDefaultConfig());
                TeslaCrate.getPlugin().getLogger().warn("Default config loaded into teslacrate.conf!");
            } catch (IOException e) {
                e.printStackTrace();
                TeslaCrate.getPlugin().getLogger().error("Unable to load defaultConfig.conf! Config must be manually loaded.");
            }
        }
        loadConfig();
        Util.clearStorage();
        strictChances = rootNode.getNode("config", "strict-chances").getBoolean(false);
        Map<Object, ? extends CommentedConfigurationNode> commandRewardsMap = rootNode.getNode("rewards", "command").getChildrenMap();
        for (Map.Entry<Object, ? extends CommentedConfigurationNode> rewardName : commandRewardsMap.entrySet()) {
            loadReward(RewardType.COMMAND, (String) rewardName.getKey());
        }
        Map<Object, ? extends CommentedConfigurationNode> itemRewardsMap = rootNode.getNode("rewards", "item").getChildrenMap();
        for (Map.Entry<Object, ? extends CommentedConfigurationNode> rewardName : itemRewardsMap.entrySet()) {
            loadReward(RewardType.ITEM, (String) rewardName.getKey());
        }
        Map<Object, ? extends CommentedConfigurationNode> compoundRewardsMap = rootNode.getNode("rewards", "compound").getChildrenMap();
        for (Map.Entry<Object, ? extends CommentedConfigurationNode> rewardName : compoundRewardsMap.entrySet()) {
            loadReward(RewardType.COMPOUND, (String) rewardName.getKey());
        }
        Map<Object, ? extends CommentedConfigurationNode> cratesMap = rootNode.getNode("crates").getChildrenMap();
        for (Map.Entry<Object, ? extends CommentedConfigurationNode> crateName : cratesMap.entrySet()) {
            loadCrate((String) crateName.getKey());
        }
        Map<Object, ? extends CommentedConfigurationNode> locationsMap = rootNode.getNode("storage", "locations").getChildrenMap();
        for (Map.Entry<Object, ? extends CommentedConfigurationNode> locIden : locationsMap.entrySet()) {
            loadLoc((String) locIden.getKey());
        }
    }

    public static void loadCrate(String crateName) {
        loadConfig();
        CommentedConfigurationNode crateNode = rootNode.getNode("crates", crateName);
        String display = crateNode.getNode("display").getString("");
        if (display.isEmpty()) {
            TeslaCrate.getPlugin().getLogger().error("No display found! | Crate:[" + crateName + "]");
            return;
        }
        Map<CrateReward, Double> rewards = Maps.newHashMap();
        Map<Object, ? extends CommentedConfigurationNode> rewardsMap = crateNode.getNode("rewards").getChildrenMap();
        for (Map.Entry<Object, ? extends CommentedConfigurationNode> rewardKey : rewardsMap.entrySet()) {
            CrateReward reward = Util.getGlobalReward((String) rewardKey.getKey());
            if (reward == null) {
                TeslaCrate.getPlugin().getLogger().error("No reward found! | Name: [" + rewardKey.getKey() + "] Reward:[" + rewardKey.getKey() + "]");
                return;
            }
            double chance = crateNode.getNode("rewards", rewardKey.getKey()).getDouble(0);
            if (chance <= 0) {
                TeslaCrate.getPlugin().getLogger().error("Invalid chance! | Chance:[" + chance + "] Reward:[" + rewardKey.getKey() + "] Crate:[" + crateName + "]");
                return;
            }
            rewards.put(reward, chance);
        }
        CrateKeydata keydata = new CrateKeydata();
        //keydata.Cost = crateNode.getNode("keydata", "cost").getDouble(0);
        keydata.Display = crateNode.getNode("keydata", "display").getString(display + " &6Key");
        keydata.Physical = !crateNode.getNode("keydata", "physical").isVirtual();
        if (keydata.Physical) {
            String id = crateNode.getNode("keydata", "physical", "id").getString("");
            if (id.isEmpty()) {
                TeslaCrate.getPlugin().getLogger().error("No key id found! | Crate:[" + crateName + "]");
                return;
            }
            ItemType type = Sponge.getRegistry().getType(ItemType.class, id).orElse(null);
            if (type == null) {
                TeslaCrate.getPlugin().getLogger().error("No key type found! | Crate:[" + crateName + "]");
                return;
            }
            keydata.Item = ItemStack.builder().itemType(type).quantity(1).add(Keys.DISPLAY_NAME, Util.toText(keydata.Display)).add(Keys.ITEM_LORE, Util.toText(Lists.newArrayList("Usable on a " + crateName + " crate"))).build();
        }
        CrateMetadata metadata = new CrateMetadata();
        metadata.ChanceSum = crateNode.getNode("metadata", "chance-sum").getDouble(0);
        double chanceSum = rewards.values().stream().mapToDouble(Double::doubleValue).sum();
        if (chanceSum != metadata.ChanceSum) {
            if (metadata.ChanceSum > 0) {
                if (strictChances) {
                    TeslaCrate.getPlugin().getLogger().error("Rewards chance sum does not match the given sum! | ChanceSum:[" + chanceSum + "] Crate:[" + crateName + "]");
                    return;
                } else {
                    TeslaCrate.getPlugin().getLogger().warn("Rewards chance sum does not match the given sum! | ChanceSum:[" + chanceSum + "] Crate:[" + crateName + "]");
                }
            }
            metadata.ChanceSum = chanceSum;
        }
        metadata.MsgAnnounce = crateNode.getNode("metadata", "msg-announce").getString("");
        metadata.MsgPlayer = crateNode.getNode("metadata", "msg-player").getString("");
        Util.storeCrate(new Crate(crateName, display, rewards, keydata, metadata));
    }

    public static void loadReward(RewardType rewardType, String rewardName) {
        CommentedConfigurationNode rewardNode = rootNode.getNode("rewards", rewardType.Name.toLowerCase(), rewardName);
        String display = rewardNode.getNode("display").getString("");
        CrateReward reward;
        switch (rewardType) {
            case COMMAND:
                String command = rewardNode.getNode("command").getString("");
                if (display.isEmpty()) {
                    TeslaCrate.getPlugin().getLogger().error("No command found! | Reward:[" + rewardName + "]");
                    return;
                }
                reward = new CommandReward(display, command);
                break;
            case COMPOUND:
                List<CrateReward> rewards = Lists.newArrayList();
                try {
                    List<String> rewardNames = rewardNode.getNode("rewards").getList(TypeToken.of(String.class));
                    for (String name : rewardNames) {
                        CrateReward globalReward = Util.getGlobalReward(name);
                        if (globalReward == null) {
                            TeslaCrate.getPlugin().getLogger().error("No global reward found! | GlobalName: [" + name + "] Reward:[" + rewardName + "]");
                            return;
                        }
                        rewards.add(globalReward);
                    }
                } catch (ObjectMappingException ignored) {
                    TeslaCrate.getPlugin().getLogger().error("Unable to retrieve rewards list! | Reward: [" + rewardName + "]");
                    return;
                }
                reward = new CompoundReward(display, rewards);
                break;
            case ITEM:
                String id = rewardNode.getNode("id").getString("");
                if (id.isEmpty()) {
                    TeslaCrate.getPlugin().getLogger().error("No id found! | Reward:[" + rewardName + "]");
                    return;
                }
                ItemType type = Sponge.getRegistry().getType(ItemType.class, id).orElse(null);
                if (type == null) {
                    TeslaCrate.getPlugin().getLogger().error("No item type found! | ID:[" + id + "] Reward:[" + rewardName + "]");
                    return;
                }
                int quantity = rewardNode.getNode("quantity").getInt(0);
                if (quantity <= 0) {
                    TeslaCrate.getPlugin().getLogger().error("Invalid quantity! | Quantity:[" + quantity + "] Reward:[" + rewardName + "]");
                    return;
                }
                ItemStack item = ItemStack.of(type, quantity);
                reward = new ItemReward(display, item);
                break;
            default:
                TeslaCrate.getPlugin().getLogger().error("Attempted to load unknown reward type! | Type:[" + rewardType + "]");
                return;
        }
        Util.addGlobalReward(rewardName, reward);
    }

    public static void loadLoc(String locStr) {
        loadConfig();
        String crateName = rootNode.getNode("storage", "locations", locStr).getString("");
        Crate crate = Util.getStoredCrate(crateName);
        if (crate != null) {
            try {
                Util.registerCrate(CrateLocation.buildCrateLoc(locStr.split(",")), crate);
            } catch (LocationFormatException ignored) {
                TeslaCrate.getPlugin().getLogger().error(ignored.getMessage() + " Location:[" + locStr + "]");
            }
        } else {
            TeslaCrate.getPlugin().getLogger().error("Unable to locate crate location! | Location:[" + locStr + "] CrateName:[" + crateName + "]");
        }
    }

    public static void saveLoc(CrateLocation crateLoc, String crateName) {
        loadConfig();
        rootNode.getNode("storage", "locations", crateLoc.print()).setValue(crateName);
        saveConfig();
    }

    public static int getKeys(User user, Crate crate) {
        loadConfig();
        return rootNode.getNode("storage", "keys", user.getUniqueId().toString(), crate.Name).getInt(0);
    }

    public static List<String> getAllKeys(User user) {
        loadConfig();
        List<String> keysList = Lists.newArrayList();
        Map<Object, ? extends CommentedConfigurationNode> keysMap = rootNode.getNode("storage", "keys", user.getUniqueId().toString()).getChildrenMap();
        for (Map.Entry<Object, ? extends CommentedConfigurationNode> crateName : keysMap.entrySet()) {
            Crate crate = Util.getStoredCrate((String) crateName.getKey());
            if (crate != null) {
                keysList.add("&6" + crate.Display + " &7x" + rootNode.getNode("storage", "keys", user.getUniqueId().toString(), crate.Name).getInt(0));
            } else {
                TeslaCrate.getPlugin().getLogger().error("Attempted to load keys for unknown crate! | Crate:[" + crateName.getKey() + "] User:[" + user.getUniqueId() + " (" + user.getName() + ")]");
            }
        }
        keysList.sort(Comparator.naturalOrder());
        return keysList;
    }

    public static void setKeys(User user, Crate crate, int keys) {
        loadConfig();
        rootNode.getNode("storage", "keys", user.getUniqueId().toString(), crate.Name).setValue(keys == 0 ? null : keys);
        saveConfig();
    }
}