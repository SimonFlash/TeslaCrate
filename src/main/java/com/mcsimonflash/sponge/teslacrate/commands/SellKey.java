package com.mcsimonflash.sponge.teslacrate.commands;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.Crate;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.List;

public class SellKey implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String crateName = args.<String>getOne("crate-name").get();
        Integer quantity = args.<Integer>getOne("quantity").get();

        if (src instanceof Player) {
            Crate crate = Util.getStoredCrate(crateName);
            if (crate != null) {
                if (src.hasPermission("teslacrate.crates." + crate.Name + ".base")) {
                    if (src.hasPermission("teslacrate.crates." + crate.Name + ".sellkey")) {
                        if (crate.Keydata.SellCost > 0) {
                            if (quantity > 0) {
                                Player player = (Player) src;
                                if (crate.Keydata.Physical) {
                                    ItemStack handItem = player.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
                                    if (handItem != null) {
                                        List<Text> lore = handItem.get(Keys.ITEM_LORE).orElse(null);
                                        if (lore != null && lore.equals(Util.toText(Lists.newArrayList("Usable on a " + crate.Name + " crate")))) {
                                            int keys = player.getInventory().query(crate.Keydata.Item).size();
                                            if (keys >= quantity) {
                                                ItemStack item = player.getInventory().query(crate.Keydata.Item).poll(quantity).orElse(null);
                                                if (item == null) {
                                                    src.sendMessage(Util.prefix.concat(Util.toText("&7An unexpected error occurred retrieving your key!")));
                                                    TeslaCrate.getPlugin().getLogger().error("Unable to retrieve key! | Crate:[" + crate.Name + "] Player:[" + player.getName() + "]");
                                                    return CommandResult.empty();
                                                }
                                            } else {
                                                src.sendMessage(Util.prefix.concat(Util.toText("&7You only have &f" + keys + "&7 keys!")));
                                                return CommandResult.empty();
                                            }
                                        }
                                    }
                                    src.sendMessage(Util.prefix.concat(Util.toText("&7You are not holding a key for this crate!")));
                                    return CommandResult.empty();
                                } else {
                                    int keys = Config.getKeys(player, crate);
                                    if (keys >= quantity) {
                                        Config.setKeys(player, crate, keys - quantity);
                                    } else {
                                        src.sendMessage(Util.prefix.concat(Util.toText("&7You only have &f" + keys + "&7 keys!")));
                                        return CommandResult.empty();
                                    }
                                }
                                UniqueAccount account = TeslaCrate.getEconServ().getOrCreateAccount(player.getUniqueId()).orElse(null);
                                if (account != null) {
                                    account.deposit(TeslaCrate.getEconServ().getDefaultCurrency(), BigDecimal.valueOf(quantity * crate.Keydata.SellCost), Cause.source(TeslaCrate.getPlugin()).build());
                                    src.sendMessage(Util.prefix.concat(Util.toText("&7Successfully sold &f" + quantity + "x " + crate.Name + " &7crate keys!")));
                                    return CommandResult.success();
                                } else {
                                    src.sendMessage(Util.prefix.concat(Util.toText("&7An unexpected error occurred locating your account!")));
                                    return CommandResult.empty();
                                }
                            } else {
                                src.sendMessage(Util.prefix.concat(Util.toText("&7Quantity must be greater than 1!")));
                                return CommandResult.empty();
                            }
                        } else {
                            src.sendMessage(Util.prefix.concat(Util.toText("&7You may not sell keys for this crate!")));
                            return CommandResult.empty();
                        }
                    } else {
                        src.sendMessage(Util.prefix.concat(Util.toText("&7You do not have permission to sell keys for this crate!")));
                        return CommandResult.empty();
                    }
                } else {
                    src.sendMessage(Util.prefix.concat(Util.toText("&7You do not have permission to use this crate!")));
                    return CommandResult.empty();
                }
            } else {
                src.sendMessage(Util.prefix.concat(Util.toText("&7Unable to locate crate &f" + crateName + "&7!")));
                return CommandResult.empty();
            }
        } else {
            src.sendMessage(Util.prefix.concat(Util.toText("&7Only a player may execute this command!")));
            return CommandResult.empty();
        }
    }
}
