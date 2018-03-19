package com.mcsimonflash.sponge.teslacrate.command;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.net.URL;
import java.util.Optional;

public class CmdUtils {

    public static Player requirePlayer(CommandSource src) throws CommandException {
        if (src instanceof Player) {
            return (Player) src;
        }
        throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.player-only"));
    }

    public static Text usage(String base, String description, Text... args) {
        return Text.builder(base)
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand(base))
                .onHover(TextActions.showText(Text.of(TextColors.GRAY, description)))
                .append(Text.joinWith(Text.of(" "), args))
                .build();
    }

    public static Text arg(boolean req, String name, String desc) {
        return Text.builder((req ? "<" : "[") + name + (req ? ">" : "]"))
                .color(TextColors.YELLOW)
                .onHover(TextActions.showText(Text.of(TextColors.WHITE, name, ": ", TextColors.GRAY, desc)))
                .build();
    }

    public static Text link(String name, Optional<URL> optUrl) {
        return optUrl.map(u -> Text.builder(name)
                .onClick(TextActions.openUrl(u))
                .onHover(TextActions.showText(Text.of(u)))
                .build()).orElseGet(() -> Text.builder(name)
                .color(TextColors.RED)
                .onHover(TextActions.showText(Text.of("Sorry! This URL is unavailable.")))
                .build());
    }

}