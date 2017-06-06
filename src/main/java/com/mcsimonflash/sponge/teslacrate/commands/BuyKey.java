package com.mcsimonflash.sponge.teslacrate.commands;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.Crate;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;

public class BuyKey implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String crateName = args.<String>getOne("crate-name").get();
        Integer quantity = args.<Integer>getOne("quantity").get();

        if (src instanceof Player) {
            Crate crate = Util.getStoredCrate(crateName);
            if (crate != null) {
                if (src.hasPermission("teslacrate.crates." + crate.Name + ".base")) {
                    if (src.hasPermission("teslacrate.crates." + crate.Name + ".buykey")) {
                        if (crate.Keydata.BuyCost > 0) {
                            if (quantity > 0) {
                                Player player = (Player) src;
                                UniqueAccount account = TeslaCrate.getEconServ().getOrCreateAccount(player.getUniqueId()).orElse(null);
                                if (account != null) {
                                    TransactionResult accResult = account.withdraw(TeslaCrate.getEconServ().getDefaultCurrency(), BigDecimal.valueOf(quantity * crate.Keydata.BuyCost), Cause.source(TeslaCrate.getPlugin()).build());
                                    if (accResult.getResult().equals(ResultType.SUCCESS)) {
                                        if (crate.Keydata.Physical) {
                                            InventoryTransactionResult invResult = player.getInventory().query(Hotbar.class, GridInventory.class).offer(ItemStack.builder().fromItemStack(crate.Keydata.Item).quantity(quantity).build());
                                            if (invResult.equals(InventoryTransactionResult.failNoTransactions())) {
                                                src.sendMessage(Util.prefix.concat(Util.toText("&7Failed to give you a physical key!")));
                                                account.deposit(TeslaCrate.getEconServ().getDefaultCurrency(), BigDecimal.valueOf(quantity * crate.Keydata.BuyCost), Cause.source(TeslaCrate.getPlugin()).build());
                                                return CommandResult.empty();
                                            }
                                        } else {
                                            Config.setKeys(player, crate, Config.getKeys(player, crate) + quantity);
                                        }
                                        src.sendMessage(Util.prefix.concat(Util.toText("&7Successfully bought &f" + quantity + "x " + crate.Name + " &7crate keys!")));
                                        return CommandResult.success();
                                    } else {
                                        src.sendMessage(Util.prefix.concat(Util.toText("&7Quantity must be greater than 1!")));
                                        return CommandResult.empty();
                                    }
                                } else {
                                    src.sendMessage(Util.prefix.concat(Util.toText("&7An unexpected error occurred locating your account!")));
                                    return CommandResult.empty();
                                }
                            } else {
                                src.sendMessage(Util.prefix.concat(Util.toText("&7Quantity must be greater than 1!")));
                                return CommandResult.empty();
                            }
                        } else {
                            src.sendMessage(Util.prefix.concat(Util.toText("&7You may not buy keys for this crate!")));
                            return CommandResult.empty();
                        }
                    } else {
                        src.sendMessage(Util.prefix.concat(Util.toText("&7You do not have permission to buy keys for this crate!")));
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
