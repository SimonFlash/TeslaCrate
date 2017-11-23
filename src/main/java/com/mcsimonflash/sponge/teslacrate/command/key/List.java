package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Key;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.stream.Collectors;

public class List implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Give())
            .arguments(GenericArguments.optional(GenericArguments.userOrSource(Text.of("user"))))
            .permission("teslacrate.command.key.list.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            User user = args.<User>getOne("user").get();
            if (user == src || src.hasPermission("teslacrate.command.key.list.other")) {
                Utils.page(Storage.keys.values().stream().filter(k1 -> k1.check(user) != 0).collect(Collectors.toList()).stream().map(k -> Element.of(ItemStack.builder()
                        .fromContainer(k.getDisplayItem().toContainer())
                        .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, k.getDisplayName()))
                        .add(Keys.ITEM_LORE, Lists.newArrayList(Utils.toText(String.valueOf(k.check(user)))))
                        .build())).collect(Collectors.toList())).open((Player) src);
                return CommandResult.success();
            } else {
                throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.key.list.self-only"));
            }
        } else {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.player-only", src.getLocale()));
        }
    }

}