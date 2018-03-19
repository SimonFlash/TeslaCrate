package com.mcsimonflash.sponge.teslacrate.command.item;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.component.Item;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

@Singleton
@Aliases("give")
@Permission("teslacrate.command.item.give.base")
public class Give extends TeslaCommand {

    @Inject
    private Give() {
        super(CmdUtils.usage("/teslacrate item give ", "Gives an item to a player.", PLAYER_ARG, ITEM_ARG, QUANTITY_ARG),
                settings().arguments(PLAYER_ELEM, ITEM_ELEM, QUANTITY_ELEM.getParser().optional().toElement("quantity")));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player player = args.<Player>getOne("player").get();
        Item item = args.<Item>getOne("item").get();
        Integer quantity = args.<Integer>getOne("quantity").orElse(item.getItem().getQuantity());
        item.give(player, quantity);
        TeslaCrate.sendMessage(src, "teslacrate.command.item.give.success", "player", player.getName(), "item", item.getName(), "quantity", quantity);
        return CommandResult.success();
    }

}