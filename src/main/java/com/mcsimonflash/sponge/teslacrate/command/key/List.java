package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tuple;

import java.util.stream.Collectors;

@Aliases("list")
@Permission("teslacrate.command.key.list.base")
public final class List extends Command {

    @Inject
    private List(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate key list ", "Displays the keys a user has.", CmdUtils.USER_OR_SOURCE_ARG, CmdUtils.NO_GUI_ARG))
                .elements(CmdUtils.USER_OR_SOURCE_ELEM, CmdUtils.NO_GUI_FLAG));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        User user = args.<User>getOne("user").get();
        if (src instanceof Player && !args.hasAny("nogui")) {
            Inventory.page(Utils.toText("&6" + user.getName() + "&e's Keys"), Registry.KEYS.getComponents().getDistinct().stream()
                    .map(Tuple::getFirst)
                    .filter(k -> k.get(user) != 0)
                    .map(k -> Element.of(ItemStack.builder().fromSnapshot(k.getDisplayItem())
                            .add(Keys.ITEM_LORE, Lists.newArrayList(Utils.toText("&7quantity: &f" + k.get(user))))
                            .build()))
                    .collect(Collectors.toList()), Inventory.CLOSE)
                    .open((Player) src);
        } else {
            Utils.sendPagination(src, "&e" + user.getName() + "&6's Keys", Registry.KEYS.getComponents().getDistinct().stream()
                    .map(Tuple::getFirst)
                    .filter(k -> k.get(user) == 0)
                    .map(k -> Utils.toText("&7 - &f" + k.getName().toPlain() + "&7: &f" + k.get(user)))
                    .collect(Collectors.toList()));
        }
        return CommandResult.success();
    }

}