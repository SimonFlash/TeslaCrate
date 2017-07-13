package com.mcsimonflash.sponge.teslacrate.commands.keys;

import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Storage;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.crates.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.keys.Key;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;

public class GiveKey implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("user").get();
        String keyName = args.<String>getOne("key-name").get();
        Integer quantity = args.<Integer>getOne("quantity").get();

        Key key = Storage.keyDirectory.get(keyName.toLowerCase());
        if (key != null) {
            if (quantity > 0) {
                if (key.giveKeys(user, quantity)) {
                    if (!src.equals(user) && user.isOnline()) {
                        user.getPlayer().get().sendMessage(Config.teslaPrefix.concat(Util.toText("&7Received &f" + quantity + "x " + key.DisplayName + "&7 keys!")));
                    }
                    src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Successfully gave &f" + user.getName() + " " + quantity + "x " + key.DisplayName + "&7 keys!")));
                    return CommandResult.success();
                } else {
                    src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Failed to give &f" + user.getName() + " " + quantity + "x " + key.DisplayName + "&7 keys!")));
                }
            } else {
                src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Quantity must be greater than 1!")));
            }
        } else {
            src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Unable to locate key &f" + keyName + "&7!")));
        }
        return CommandResult.empty();
    }
}
