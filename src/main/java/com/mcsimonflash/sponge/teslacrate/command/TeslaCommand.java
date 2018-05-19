package com.mcsimonflash.sponge.teslacrate.command;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.mcsimonflash.sponge.teslacore.tesla.Tesla;
import com.mcsimonflash.sponge.teslacore.tesla.TeslaUtils;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.argument.Arguments;
import com.mcsimonflash.sponge.teslalibs.argument.CommandElement;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TeslaCommand extends Command {

    protected static final CommandElement
            PLAYER_ELEM = Arguments.player().toElement("player"),
            USER_ELEM = Arguments.user().toElement("user"),
            USER_OR_SOURCE_ELEM = Arguments.user().orSource().toElement("user"),
            COMMAND_ELEM = Arguments.choices(Storage.commands, ImmutableMap.of("no-choice", "Argument <arg> is not the name of a command component.")).toElement("command"),
            CRATE_ELEM = Arguments.choices(Storage.crates, ImmutableMap.of("no-choice", "Argument <arg> is not the name of a crate component.")).toElement("crate"),
            ITEM_ELEM = Arguments.choices(Storage.items, ImmutableMap.of("no-choice", "Argument <arg> is not the name of a item component.")).toElement("item"),
            KEY_ELEM = Arguments.choices(Storage.keys, ImmutableMap.of("no-choice", "Argument <arg> is not the name of a key component.")).toElement("key"),
            REWARD_ELEM = Arguments.choices(Storage.rewards, ImmutableMap.of("no-choice", "Argument <arg> is not the name of a reward component.")).toElement("reward"),
            QUANTITY_ELEM = Arguments.intObj().inRange(Range.closed(1, 64)).toElement("quantity"),
            LOCATION_ELEM = Arguments.location().toElement("location");
    protected static final Text
            SUBCOMMAND_ARG = CmdUtils.arg(true, "...", "A subcommand."),
            PLAYER_ARG = CmdUtils.arg(true, "player", "Name of an online player."),
            USER_ARG = CmdUtils.arg(true, "user", "Name of a user."),
            OPTIONAL_USER_ARG = CmdUtils.arg(false, "user", "Name of a user. Defaults to the source if the source is a user."),
            COMMAND_ARG = CmdUtils.arg(true, "command", "Name of a command component."),
            CRATE_ARG = CmdUtils.arg(true, "crate", "Name of a crate component."),
            ITEM_ARG = CmdUtils.arg(true, "item", "Name of an item component."),
            KEY_ARG = CmdUtils.arg(true, "key", "Name of a key component."),
            REWARD_ARG = CmdUtils.arg(true, "reward", "Name of a reward component."),
            QUANTITY_ARG = CmdUtils.arg(true, "quantity", "An integer between 1 and 64 inclusive."),
            WORLD_ARG = CmdUtils.arg(true, "world", "Name of a loaded world. Defaults to the source's world if the source has a location."),
            POSITION_ARG = CmdUtils.arg(true, "position", "A vector3d (x, y, z) position. Relative coordinates (~) and select modifiers (#) are allowed.");
    private static final Text LINKS = Text.of("                      ", CmdUtils.link("Ore Project", TeslaCrate.get().getContainer().getUrl().flatMap(TeslaUtils::parseURL)), TextColors.GRAY, " | ", CmdUtils.link("Support Discord", Tesla.DISCORD));

    protected TeslaCommand(Text usage, Settings settings) {
        super(TeslaCrate.get().getCommands(), settings.usage(usage));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        PaginationList.builder()
                .title(TeslaCrate.PREFIX)
                .padding(Utils.toText("&7="))
                .contents(Stream.concat(Stream.of(getUsage()), getChildren().stream().map(Command::getUsage)).collect(Collectors.toList()))
                .footer(LINKS)
                .sendTo(src);
        return CommandResult.success();
    }

}
