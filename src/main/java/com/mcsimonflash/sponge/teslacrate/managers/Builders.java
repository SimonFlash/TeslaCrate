package com.mcsimonflash.sponge.teslacrate.managers;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.objects.crates.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.keys.Key;
import com.mcsimonflash.sponge.teslacrate.objects.keys.PhysicalKey;
import com.mcsimonflash.sponge.teslacrate.objects.keys.VirtualKey;
import com.mcsimonflash.sponge.teslacrate.objects.rewards.Reward;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class Builders {

    public static String buildCommand(CommentedConfigurationNode commandNode, boolean singleton) throws IllegalArgumentException {
        String commandName = (String) commandNode.getKey();
        if (commandNode.hasMapChildren()) {
            String command =  commandNode.getNode("command").getString("");
            command = command.startsWith("/") ? command.substring(1) : command;
            if (command.isEmpty()) {
                throw new IllegalArgumentException("Empty command! | Command:[" + commandName + "]");
            }
            return command;
        } else {
            String refName = singleton ? commandNode.getString("") : commandName;
            if (!Storage.globalCommands.containsKey(refName)) {
                throw new IllegalArgumentException("No global command found! | Command:[" + refName + "]");
            }
            return Storage.globalCommands.get(refName);
        }
    }

    public static Crate buildCrate(CommentedConfigurationNode crateNode, boolean singleton) throws IllegalArgumentException {
        String crateName = (String) crateNode.getKey();
        Crate crate = new Crate(crateName);
        if (crateNode.hasMapChildren()) {
            try {
                crateNode.getNode("rewards").getChildrenMap().values().forEach(r -> {
                    Reward reward = buildReward(r, false);
                    double chance = r.hasMapChildren() ? r.getNode("chance").getDouble(0) : r.getDouble(0);
                    if (chance <= 0) {
                        throw new IllegalArgumentException("Invalid chance! | Chance:[" + chance + "] Reward:[" + r.getKey() + "]");
                    }
                    crate.Rewards.put(reward, chance);
                });
                crateNode.getNode("keys").getChildrenMap().values().forEach(k -> {
                    Key key = buildKey(k, false);
                    int quantity = k.hasMapChildren() ? k.getNode("quantity").getInt(1) : k.getInt(1);
                    if (quantity <= 0) {
                        throw new IllegalArgumentException("Invalid quantity! | Quantity:[" + quantity + "] Key:[" + k.getKey() + "]");
                    }
                    crate.Keys.put(key, quantity);
                });
                crate.ChanceSum = crateNode.getNode("metadata", "chance-sum").getDouble(0);
                double chanceSum = crate.Rewards.values().stream().mapToDouble(Double::doubleValue).sum();
                if (crate.ChanceSum > 0 && chanceSum != crate.ChanceSum) {
                    if (Config.strictChances) {
                        throw new IllegalArgumentException("Rewards chance sum does not match given sum! | Calculated:[" + chanceSum + "] Expected:[" + crate.ChanceSum + "]");
                    } else {
                        TeslaCrate.getPlugin().getLogger().warn("Rewards chance sum does not match given sum! | Calculated:[" + chanceSum + "] Expected:[" + crate.ChanceSum + "]");
                    }
                }
                crate.ChanceSum = chanceSum;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage() + " Crate:[" + crateName + "]");
            }
        } else {
            String refName = singleton ? crateNode.getString("") : crateName;
            if (!Storage.globalCommands.containsKey(refName)) {
                throw new IllegalArgumentException("No global command found! | Command:[" + refName + "]");
            }
            return Storage.crateDirectory.get(refName);
        }
        crate.DisplayName = crateNode.getNode("display-name").getString(crate.Name);
        crate.Cooldown = crateNode.getNode("metadata", "cooldown").getDouble(0);
        crate.AnnounceMsg = crateNode.getNode("metadata", "msg-announce").getString("");
        crate.PlayerMsg = crateNode.getNode("metadata", "msg-player").getString("");
        crate.Keys.keySet().forEach(k -> Storage.keyDirectory.putIfAbsent(k.Name, k));
        return crate;
    }

    public static ItemStack buildItem(CommentedConfigurationNode itemNode, boolean singleton) throws IllegalArgumentException {
        String itemName = (String) itemNode.getKey();
        if (itemNode.hasMapChildren()) {
            String id = itemNode.getNode("id").getString("");
            ItemType type = Sponge.getRegistry().getType(ItemType.class, id).orElse(null);
            if (type == null) {
                throw new IllegalArgumentException("No item type found! | Id:[" + id + "] Item:[" + itemName + "]");
            }
            int quantity = itemNode.getNode("quantity").getInt(1);
            if (quantity < 1 || quantity > 64) {
                throw new IllegalArgumentException("Invalid quantity! | Quantity:[" + quantity + "] Item:[" + itemName + "]");
            }
            return ItemStack.of(type, quantity);
        } else {
            String refName = singleton ? itemNode.getString("") : itemName;
            if (!Storage.globalItems.containsKey(refName)) {
                throw new IllegalArgumentException("No global item found! | Item:[" + refName + "]");
            }
            return Storage.globalItems.get(refName);
        }
    }

    public static Key buildKey(CommentedConfigurationNode keyNode, boolean singleton) throws IllegalArgumentException {
        String keyName = (String) keyNode.getKey();
        String displayName = keyNode.getNode("display-name").getString(keyName);
        Key key;
        if (keyNode.hasMapChildren()) {
            if (!keyNode.getNode("physical").isVirtual()) {
                try {
                    ItemStack item = buildItem(keyNode.getNode("physical", "item"), true);
                    item.offer(Keys.DISPLAY_NAME, Util.toText(displayName));
                    item.offer(Keys.ITEM_LORE, Lists.newArrayList(Text.of(keyName)));
                    key = new PhysicalKey(keyName, item);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e.getMessage() + " Key:[" + keyName + "]");
                }
            } else if (!keyNode.getNode("virtual").isVirtual()) {
                key = new VirtualKey(keyName);
            } else {
                throw new IllegalArgumentException("No key type found! | Types:[Physical, Virtual] Key:[" + keyName + "]");
            }
            key.DisplayName = displayName;
            return key;
        } else {
            String refName = singleton ? keyNode.getString("") : keyName;
            if (!Storage.globalKeys.containsKey(refName)) {
                throw new IllegalArgumentException("No global key found! | Key:[" + refName + "]");
            }
            return Storage.globalKeys.get(refName);
        }
    }

    public static Reward buildReward(CommentedConfigurationNode rewardNode, boolean singleton) throws IllegalArgumentException {
        String rewardName = (String) rewardNode.getKey();
        if (rewardNode.hasMapChildren()) {
            Reward reward = new Reward(rewardName);
            try {
                rewardNode.getNode("rewards", "commands").getChildrenMap().values().forEach(c -> reward.Commands.add(buildCommand(c, false)));
                rewardNode.getNode("rewards", "items").getChildrenMap().values().forEach(i -> reward.Items.add(buildItem(i, false)));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage() + " Reward:[" + reward.Name + "]");
            }
            reward.DisplayName = rewardNode.getNode("display-name").getString(reward.Name);
            reward.Announce = rewardNode.getNode("metadata", "announce").getBoolean(true);
            return reward;
        } else {
            String refName = singleton ? rewardNode.getString("") : rewardName;
            if (!Storage.globalRewards.containsKey(refName)) {
                throw new IllegalArgumentException("No global reward found! | Reward:[" + refName + "]");
            }
            return Storage.globalRewards.get(refName);
        }
    }
}
