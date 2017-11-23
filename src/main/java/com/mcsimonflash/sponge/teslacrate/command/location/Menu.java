package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.stream.Collectors;

public class Menu implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Menu())
            .permission("teslacrate.location.menu.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Utils.page(Storage.registry.values().stream().map(r -> Element.of(ItemStack.builder()
                    .fromContainer(r.getCrate().getDisplayItem().toContainer())
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, r.getCrate().getName()))
                    .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(TextColors.GOLD, r.getLocation().getExtent().getName(), r.getLocation().getPosition())))
                    .build())).collect(Collectors.toList())).open((Player) src);
            return CommandResult.success();
        } else {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.player-only", src.getLocale()));
        }
    }

}