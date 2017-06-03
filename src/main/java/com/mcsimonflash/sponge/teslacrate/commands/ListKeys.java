package com.mcsimonflash.sponge.teslacrate.commands;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class ListKeys implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("opt-user").isPresent() ? args.<User>getOne("opt-user").get() : null;

        if (user == null) {
            if (src instanceof Player) {
                user = (Player) src;
            } else {
                src.sendMessage(Util.prefix.concat(Util.toText("&7A user must be defined to execute this command!")));
                return CommandResult.empty();
            }
        }
        if (src.equals(user) || src.hasPermission("teslacrate.keys.other")) {
            int c = 1;
            List<Text> keys = Lists.newArrayList();
            for (String key : Config.getAllKeys(user)) {
                keys.add(Util.toText("&6" + c++ + ": " + key));
            }
            if (keys.isEmpty()) {
                keys.add(Util.toText("&7No keys found!"));
            }
            PaginationList.builder()
                    .padding(Text.of(TextColors.DARK_GRAY, "-"))
                    .title(Text.of(TextColors.YELLOW, "Tesla", TextColors.GOLD, "Crate"))
                    .contents(keys)
                    .sendTo(src);
            return CommandResult.success();
        } else {
            src.sendMessage(Util.prefix.concat(Util.toText("&7You do not have permission to view other users keys!")));
            return CommandResult.empty();
        }
    }
}
