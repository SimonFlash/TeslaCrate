package com.mcsimonflash.sponge.teslacrate.commands.display;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.managers.Storage;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.crates.Crate;
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
import java.util.stream.Collectors;

public class ListCrates implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        boolean all = src.hasPermission("teslacrate.listcrates.all");
        boolean lookup = src.hasPermission("teslacrate.lookup.base");
        List<Crate> crates = Storage.crateDirectory.values().stream().filter(c -> all | src.hasPermission("teslacrate.crates." + c.Name + ".base")).collect(Collectors.toList());
        List<Text> texts = Lists.newArrayList();
        if (crates.isEmpty()) {
            texts.add(Util.toText("&7No crates found!"));
        } else {
            int c = 1;
            for (Crate crate : crates) {
                if (lookup) {
                    texts.add(Text.builder("")
                            .append(Util.toText("&6" + c++ + ": &e" + crate.DisplayName + "&7 x" + Collections.frequency(Storage.crateRegistry.values(), crate)))
                            .onHover(TextActions.showText(Util.toText("&7Click to lookup this crate's locations")))
                            .onClick(TextActions.runCommand("/teslacrate lookup -c:" + crate.Name))
                            .build());
                } else {
                    texts.add(Util.toText("&6" + c++ + ": &e" + crate.DisplayName + "&7 x" + Collections.frequency(Storage.crateRegistry.values(), crate)));
                }
            }
        }
        PaginationList.builder().padding(Text.of(TextColors.DARK_GRAY, "-"))
                .title(Text.of(TextColors.YELLOW, "Tesla", TextColors.GOLD, "Crate"))
                .header(lookup ? Text.of(TextColors.WHITE, "Click a crate entry to lookup positions") : Text.EMPTY)
                .contents(texts)
                .sendTo(src);
        return CommandResult.success();
    }
}
