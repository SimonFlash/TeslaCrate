package com.mcsimonflash.sponge.teslacrate.command.crate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.util.stream.Collectors;

@Aliases("list")
@Permission("teslacrate.command.crate.list.base")
public final class List extends Command {

    @Inject
    private List(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate crate list ", "Displays the crates a user has.", CmdUtils.USER_OR_SOURCE_ARG, CmdUtils.NO_GUI_ARG))
                .elements(CmdUtils.USER_OR_SOURCE_ELEM, CmdUtils.NO_GUI_FLAG));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (src instanceof Player && !args.hasAny("nogui")) {
            Inventory.page(Utils.toText("&eAvailable &6Crates"), Registry.CRATES.getComponents().getDistinct().stream()
                    .map(Tuple::getFirst)
                    .filter(c -> src.hasPermission("teslacrate.crates." + c.getId() + ".base"))
                    .map(c -> Element.of(c.getDisplayItem(), a -> Inventory.redeemMenu(c, a.getPlayer().getLocation())))
                    .collect(Collectors.toList()), Inventory.CLOSE)
                    .open((Player) src);
        } else {
            Utils.sendPagination(src, "&eAvailable &6Crates", Registry.CRATES.getComponents().getDistinct().stream()
                    .map(Tuple::getFirst)
                    .filter(c -> src.hasPermission("teslacrate.crates." + c.getId() + ".base"))
                    .map(c -> Utils.toText("&7 - &f" + c.getName().toPlain() + "&7: &f").concat(Text.joinWith(Text.of(", "), ((Crate<?>) c).getKeys().stream().map(k -> Text.of(k.getValue())).collect(Collectors.toList()))))
                    .collect(Collectors.toList()));
        }
        return CommandResult.success();
    }

}