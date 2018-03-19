package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.stream.Collectors;

@Singleton
@Aliases("menu")
@Permission("teslacrate.command.location.menu.base")
public class Menu extends TeslaCommand {

    @Inject
    private Menu() {
        super(CmdUtils.usage("/teslacrate location menu", "Opens the location menu."), settings());
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = CmdUtils.requirePlayer(src);
        Inventory.page("&6Locations", Storage.registry.values().stream().map(r -> Element.of(ItemStack.builder()
                .fromContainer(r.getCrate().getDisplayItem().toContainer())
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, r.getCrate().getName()))
                .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(TextColors.GOLD, r.getLocation().getExtent().getName(), r.getLocation().getPosition())))
                .build())).collect(Collectors.toList()), Inventory.CLOSE).open(player, 0);
        return CommandResult.success();
    }

}