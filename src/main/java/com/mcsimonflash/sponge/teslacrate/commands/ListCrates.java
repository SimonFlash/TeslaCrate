package com.mcsimonflash.sponge.teslacrate.commands;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.Crate;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;
import java.util.List;

public class ListCrates implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        boolean all = src.hasPermission("teslacrate.listcrates.all");
        int c = 1;
        List<Text> crates = Lists.newArrayList();
        Text header = Text.EMPTY;
        if (src.hasPermission("teslacrate.lookup.base")) {
            header = Util.toText("&fClick a crate entry to lookup positions");
            for (Crate crate : Util.getCrateDirectory().values()) {
                if (all || src.hasPermission("teslacrate.crates." + crate.Name + ".base")) {
                    crates.add(Text.builder("")
                            .append(Util.toText("&6" + c++ + ": &6" + crate.Display + "&7 x" + Collections.frequency(Util.getCrateRegistry().values(), crate)))
                            .onHover(TextActions.showText(Util.toText("&7Click to lookup this crate's locations")))
                            .onClick(TextActions.runCommand("/teslacrate lookup -c:" + crate.Name))
                            .build());
                }
            }
        } else {
            for (Crate crate : Util.getCrateDirectory().values()) {
                if (all || src.hasPermission("teslacrate.crates." + crate.Name + ".base")) {
                    crates.add(Util.toText("&6" + c++ + ": &6" + crate.Display + "&7 x" + Collections.frequency(Util.getCrateRegistry().values(), crate)));
                }
            }
        }
        if (crates.isEmpty()) {
            crates.add(Util.toText("&7No crates found!"));
        }
        PaginationList.builder()
                .padding(Text.of(TextColors.DARK_GRAY, "-"))
                .title(Text.of(TextColors.YELLOW, "Tesla", TextColors.GOLD, "Crate"))
                .header(header)
                .contents(crates)
                .sendTo(src);
        return CommandResult.success();
    }
}
