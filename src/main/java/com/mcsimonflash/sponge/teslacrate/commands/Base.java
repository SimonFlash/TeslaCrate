package com.mcsimonflash.sponge.teslacrate.commands;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class Base implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Text BuyKey = Text.builder("/TeslaCrate BuyKey ")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/TeslaCrate BuyKey "))
                .append(Text.builder("<Crate> ")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Crate<String>: ", TextColors.GRAY, "Name of the crate")))
                        .build())
                .append(Text.builder("<Quantity>")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Quantity<Integer>: ", TextColors.GRAY, "Number of keys")))
                        .build())
                .build();
        Text DeleteLoc = Text.builder("/TeslaCrate DeleteLoc ")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/TeslaCrate DeleteLoc "))
                .append(Text.builder("[World] ")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "World[String]: ", TextColors.GRAY, "Name of the world (Player-Optional)")))
                        .build())
                .append(Text.builder("[Position]")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Location[Vector3i]: ", TextColors.GRAY, "Position of the crate (X Y Z) (Player-Optional)")))
                        .build())
                .build();
        Text GiveKey = Text.builder("/TeslaCrate GiveKey ")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/TeslaCrate GiveKey "))
                .append(Text.builder("<User> ")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "User<User>: ", TextColors.GRAY, "Name of the user")))
                        .build())
                .append(Text.builder("<Crate> ")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Crate<String>: ", TextColors.GRAY, "Name of the crate")))
                        .build())
                .append(Text.builder("<Quantity>")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Quantity<Integer>: ", TextColors.GRAY, "Number of keys")))
                        .build())
                .build();
        Text ListKeys = Text.builder("/TeslaCrate ListKeys ")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/TeslaCrate SetLoc "))
                .append(Text.builder("[User]")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "User<User>: ", TextColors.GRAY, "Name of the user")))
                        .build())
                .build();
        Text Lookup = Text.builder("/TeslaCrate SetLoc ")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/TeslaCrate SetLoc "))
                .append(Text.builder("[-c:Crate] ")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Crate[String]: ", TextColors.GRAY, "Name of a crate")))
                        .build())
                .append(Text.builder("[-p:Position] ")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Position[Vector3i]: ", TextColors.GRAY, "XYZ Position (X,Y,Z)")))
                        .build())
                .append(Text.builder("[-r:Radius]")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Radius[Integer]: ", TextColors.GRAY, "Radius around current position")))
                        .build())
                .append(Text.builder("[-w:World]")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "World[String]: ", TextColors.GRAY, "Name of a world")))
                        .build())
                .build();
        Text SellKey = Text.builder("/TeslaCrate SellKey ")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/TeslaCrate SellKey "))
                .append(Text.builder("<Crate> ")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Crate<String>: ", TextColors.GRAY, "Name of the crate")))
                        .build())
                .append(Text.builder("<Quantity>")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Quantity<Integer>: ", TextColors.GRAY, "Number of keys")))
                        .build())
                .build();
        Text SetLoc = Text.builder("/TeslaCrate SetLoc ")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/TeslaCrate SetLoc "))
                .append(Text.builder("<Crate> ")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Crate<String>: ", TextColors.GRAY, "Name of the crate")))
                        .build())
                .append(Text.builder("[World] ")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "World[String]: ", TextColors.GRAY, "Name of the world (Player-Optional)")))
                        .build())
                .append(Text.builder("[Position>]")
                        .color(TextColors.YELLOW)
                        .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Position[Vector3i]: ", TextColors.GRAY, "Position of the crate (X Y Z) (Player-Optional)")))
                        .build())
                .build();
        Text wikiDisc = Text.EMPTY;
        if (TeslaCrate.getWiki() != null && TeslaCrate.getDiscord() != null) {
            wikiDisc = Text.builder("| ")
                    .color(TextColors.GOLD)
                    .append(Text.builder("TeslaCrate Wiki")
                            .color(TextColors.YELLOW).style(TextStyles.UNDERLINE)
                            .onClick(TextActions.openUrl(TeslaCrate.getWiki()))
                            .onHover(TextActions.showText(Text.of("Click to open the TeslaCrate Wiki")))
                            .build())
                    .append(Text.of(TextColors.GOLD, " | "))
                    .append(Text.builder("Support Discord")
                            .color(TextColors.YELLOW).style(TextStyles.UNDERLINE)
                            .onClick(TextActions.openUrl(TeslaCrate.getDiscord()))
                            .onHover(TextActions.showText(Text.of("Click to open the Support Discord")))
                            .build())
                    .append(Text.of(TextColors.GOLD, " |"))
                    .build();
        }
        PaginationList.builder()
                .padding(Text.of(TextColors.DARK_GRAY, "-"))
                .title(Text.of(TextColors.YELLOW, "Tesla", TextColors.GOLD, "Crate"))
                .contents(BuyKey, DeleteLoc, GiveKey, ListKeys, Lookup, SellKey, SetLoc)
                .footer(wikiDisc)
                .sendTo(src);
        return CommandResult.success();
    }
}
