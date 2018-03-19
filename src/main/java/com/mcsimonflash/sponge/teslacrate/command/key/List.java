package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.stream.Collectors;

@Singleton
@Aliases("list")
@Permission("teslacrate.command.key.list.base")
public class List extends TeslaCommand {

    @Inject
    private List() {
        super(CmdUtils.usage("/teslacrate key list ", "Lists all known keys of a user.", OPTIONAL_USER_ARG),
                settings().arguments(USER_OR_SOURCE_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = CmdUtils.requirePlayer(src);
        User user = args.<User>getOne("user").get();
        if (user == src || src.hasPermission("teslacrate.command.key.list.other")) {
            Inventory.page(user.getName() + "'s Keys", Storage.keys.values().stream().filter(k1 -> k1.check(user) != 0).collect(Collectors.toList()).stream().map(k -> Element.of(ItemStack.builder()
                    .fromContainer(k.getDisplayItem().toContainer())
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, k.getDisplayName()))
                    .add(Keys.ITEM_LORE, Lists.newArrayList(Utils.toText("&6" + String.valueOf(k.check(user)) + (k.isPhysical() ? " (physical)" : " (virtual)"))))
                    .build())).collect(Collectors.toList()), Inventory.CLOSE).open(player, 0);
            return CommandResult.success();
        } else {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.key.list.self-only"));
        }
    }

}