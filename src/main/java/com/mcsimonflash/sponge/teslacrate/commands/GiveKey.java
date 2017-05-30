package com.mcsimonflash.sponge.teslacrate.commands;

import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.Crate;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;

public class GiveKey implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("user").get();
        String crateName = args.<String>getOne("crate-name").get();
        Integer quantity = args.<Integer>getOne("quantity").get();

        Crate crate = Util.getStoredCrate(crateName);
        if (crate != null) {
            if (quantity > 0) {
                if (user.hasPermission("teslacrate.crates." + crate.Name + ".base")) {
                    if (crate.Keydata.Physical) {
                        if (user.isOnline()) {
                            Player player = user.getPlayer().get();
                            InventoryTransactionResult result = player.getInventory().query(Hotbar.class, GridInventory.class).offer(ItemStack.builder().fromItemStack(crate.Keydata.Item).quantity(quantity).build());
                            if (result.equals(InventoryTransactionResult.failNoTransactions())) {
                                src.sendMessage(Util.toText("Unable to give player " + player.getName() + " a physical key!"));
                                return CommandResult.empty();
                            }
                        } else {
                            src.sendMessage(Util.toText("User " + user.getName() + " is currently offline and is unable to receive a physical key!"));
                            return CommandResult.empty();
                        }
                    } else {
                        Config.setKeys(user, crate, Config.getKeys(user, crate) + quantity);
                    }
                    src.sendMessage(Util.prefix.concat(Util.toText("&7Successfully gave &f" + user.getName() + " " + quantity + "x " + crate.Name + " &7crate keys!")));
                    if (user.isOnline()) {
                        ((Player) user).sendMessage(Util.prefix.concat(Util.toText("&7Received &f" + quantity + "x " + crate.Name + " &7crate keys!")));
                    }
                    return CommandResult.success();
                } else {
                    src.sendMessage(Util.prefix.concat(Util.toText("&7Player does not have permission to use this crate!")));
                    return CommandResult.empty();
                }
            } else {
                src.sendMessage(Util.prefix.concat(Util.toText("&7Quantity must be greater than 1!")));
                return CommandResult.empty();
            }
        } else {
            src.sendMessage(Util.prefix.concat(Util.toText("&7Unable to locate crate &f" + crateName + "&7!")));
            return CommandResult.empty();
        }
    }
}
