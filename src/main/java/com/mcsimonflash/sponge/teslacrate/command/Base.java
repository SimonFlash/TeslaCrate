package com.mcsimonflash.sponge.teslacrate.command;

import com.google.common.collect.ImmutableMap;
import com.mcsimonflash.sponge.teslacore.tesla.Tesla;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Base implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Base())
            .child(CommandSpec.builder()
                    .child(CommandSpec.builder()
                            .child(com.mcsimonflash.sponge.teslacrate.command.command.edit.Command.COMMAND, "command", "cmd")
                            .child(com.mcsimonflash.sponge.teslacrate.command.command.edit.Description.COMMAND, "description", "dsc")
                            .child(com.mcsimonflash.sponge.teslacrate.command.command.edit.DisplayName.COMMAND, "display-name", "dsn")
                            .child(com.mcsimonflash.sponge.teslacrate.command.command.edit.Server.COMMAND, "server", "srv")
                            .child(com.mcsimonflash.sponge.teslacrate.command.command.edit.Value.COMMAND, "value", "val")
                            .build(), "edit")
                    .child(com.mcsimonflash.sponge.teslacrate.command.command.Give.COMMAND, "give")
                    .child(com.mcsimonflash.sponge.teslacrate.command.command.Menu.COMMAND, "menu")
                    .build(), "command", "cmd")
            .child(CommandSpec.builder()
                    .child(com.mcsimonflash.sponge.teslacrate.command.config.Load.COMMAND, "load")
                    .child(com.mcsimonflash.sponge.teslacrate.command.config.Save.COMMAND, "save")
                    .build(), "config", "cfg")
            .child(CommandSpec.builder()
                    .child(CommandSpec.builder()
                            .child(CommandSpec.builder()
                                    .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.keys.Add.COMMAND, "add")
                                    .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.keys.Remove.COMMAND, "remove")
                                    .build(), "keys")
                            .child(CommandSpec.builder()
                                    .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.rewards.Add.COMMAND, "add")
                                    .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.rewards.Remove.COMMAND, "remove")
                                    .build(), "rewards", "rwds")
                            .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.Announcement.COMMAND, "announcement", "ann")
                            .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.Cooldown.COMMAND, "cooldown", "cld")
                            .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.Description.COMMAND, "description", "dsc")
                            .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.DisplayName.COMMAND, "display-name", "dsn")
                            .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.Firework.COMMAND, "firework", "fwk")
                            .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.Gui.COMMAND, "gui")
                            .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.Message.COMMAND, "message", "msg")
                            .child(com.mcsimonflash.sponge.teslacrate.command.crate.edit.Particle.COMMAND, "particle", "prt")
                            .build(), "edit")
                    .child(com.mcsimonflash.sponge.teslacrate.command.crate.Give.COMMAND, "give")
                    .child(com.mcsimonflash.sponge.teslacrate.command.crate.Menu.COMMAND, "menu")
                    .child(com.mcsimonflash.sponge.teslacrate.command.crate.Preview.COMMAND, "preview", "prev")
                    .build(), "crate", "crt")
            .child(CommandSpec.builder()
                    .child(CommandSpec.builder()
                            .child(com.mcsimonflash.sponge.teslacrate.command.item.edit.Description.COMMAND, "description", "dsc")
                            .child(com.mcsimonflash.sponge.teslacrate.command.item.edit.DisplayName.COMMAND, "display-name", "dsn")
                            .build(), "edit")
                    .child(com.mcsimonflash.sponge.teslacrate.command.item.Give.COMMAND, "give")
                    .child(com.mcsimonflash.sponge.teslacrate.command.item.Menu.COMMAND, "menu")
                    .build(), "item", "itm")
            .child(CommandSpec.builder()
                    .child(CommandSpec.builder()
                            .child(com.mcsimonflash.sponge.teslacrate.command.key.edit.Description.COMMAND, "description", "dsc")
                            .child(com.mcsimonflash.sponge.teslacrate.command.key.edit.DisplayName.COMMAND, "display-name", "dsn")
                            .child(com.mcsimonflash.sponge.teslacrate.command.key.edit.Quantity.COMMAND, "quantity", "qnt")
                            .build(), "edit")
                    .child(com.mcsimonflash.sponge.teslacrate.command.key.Check.COMMAND, "check")
                    .child(com.mcsimonflash.sponge.teslacrate.command.key.Give.COMMAND, "give")
                    .child(com.mcsimonflash.sponge.teslacrate.command.key.Menu.COMMAND, "menu")
                    .child(com.mcsimonflash.sponge.teslacrate.command.key.Take.COMMAND, "take")
                    .build(), "key")
            .child(CommandSpec.builder()
                    .child(com.mcsimonflash.sponge.teslacrate.command.location.Delete.COMMAND, "delete", "del")
                    .child(com.mcsimonflash.sponge.teslacrate.command.location.Menu.COMMAND, "menu")
                    .child(com.mcsimonflash.sponge.teslacrate.command.location.Set.COMMAND, "set")
                    .build(), "location", "loc")
            .child(CommandSpec.builder()
                    .child(CommandSpec.builder()
                            .child(CommandSpec.builder()
                                    .child(com.mcsimonflash.sponge.teslacrate.command.reward.edit.commands.Add.COMMAND, "add")
                                    .child(com.mcsimonflash.sponge.teslacrate.command.reward.edit.commands.Remove.COMMAND, "remove", "rem")
                                    .build(), "commands", "cmds")
                            .child(CommandSpec.builder()
                                    .child(com.mcsimonflash.sponge.teslacrate.command.reward.edit.items.Add.COMMAND, "add")
                                    .child(com.mcsimonflash.sponge.teslacrate.command.reward.edit.items.Remove.COMMAND, "remove", "rem")
                                    .build(), "items", "itms")
                            .child(com.mcsimonflash.sponge.teslacrate.command.reward.edit.Announce.COMMAND, "announce", "ann")
                            .child(com.mcsimonflash.sponge.teslacrate.command.reward.edit.Weight.COMMAND, "weight", "wht")
                            .child(com.mcsimonflash.sponge.teslacrate.command.reward.edit.Description.COMMAND, "description", "dsc")
                            .child(com.mcsimonflash.sponge.teslacrate.command.reward.edit.DisplayName.COMMAND, "display-name", "dsn")
                            .build(), "edit")
                    .child(com.mcsimonflash.sponge.teslacrate.command.reward.Give.COMMAND, "give")
                    .child(com.mcsimonflash.sponge.teslacrate.command.reward.Menu.COMMAND, "menu")
                    .build(), "reward", "rwd")
            .child(Menu.COMMAND, "menu")
            .permission("teslacrate.command.base")
            .build();

    private static final Text Plr = arg(true, "player", "a player");
    private static final Text Usr = arg(true, "user", "a user");
    private static final Text Cmd = arg(true, "command", "name of a command.");
    private static final Text Crt = arg(true, "crate", "name of a crate.");
    private static final Text Itm = arg(true, "item", "name of an item.");
    private static final Text Key = arg(true, "key", "name of a key.");
    private static final Text Loc = arg(true, "location", "a location");
    private static final Text Rwd = arg(true, "reward", "name of a reward.");
    private static final Text EdtDsc = arg(true, "description", "the new description.");
    private static final Text EdtDpN = arg(true, "display-name", "the new display-name");
    private static final Text EdtQnt = arg(true, "quantity", "a positive integer.");
    private static final Text OptQnt = arg(false, "quantity", "a positive integer.");
    private static final Text OptVal = arg(false, "value", "the value.");

    private static final ImmutableMap<String, Text> Usages = ImmutableMap.<String, Text>builder()
            .put("teslacrate.command.command.edit.command.base", usage("/crate command edit command ", Cmd, arg(true, "command", "the new command.")))
            .put("teslacrate.command.command.edit.description.base", usage("/crate command edit description ", Cmd, EdtDsc))
            .put("teslacrate.command.command.edit.display-name.base", usage("/crate command edit display-name ", Cmd, EdtDpN))
            .put("teslacrate.command.command.edit.server.base", usage("/crate command edit server ", Cmd, arg(true, "server", "the new server value.")))
            .put("teslacrate.command.command.edit.value.base", usage("/crate command edit value ", Cmd, arg(true, "value", "the new value")))
            .put("teslacrate.command.command.give.base", usage("/crate command give ", Plr, Cmd, OptVal))
            .put("teslacrate.command.command.menu.base", usage("/crate command menu ", arg(false, "command", "name of a command.")))
            .put("teslacrate.command.config.load.base", usage("/crate config load "))
            .put("teslacrate.command.config.save.base", usage("/crate config save "))
            .put("teslacrate.command.crate.edit.announcement.base", usage("/crate crate edit announcement ", Crt, arg(true, "announcement", "the new announcement.")))
            .put("teslacrate.command.crate.edit.cooldown.base", usage("/crate crate edit cooldown ", Crt, arg(true, "cooldown", "the new cooldown.")))
            .put("teslacrate.command.crate.edit.description.base", usage("/crate crate edit description ", Crt, EdtDsc))
            .put("teslacrate.command.crate.edit.display-name.base", usage("/crate crate edit display-name ", Crt, EdtDpN))
            .put("teslacrate.command.crate.edit.firework.base", usage("/crate crate edit firework ", Crt, arg(true, "firework", "the new firework value")))
            .put("teslacrate.command.crate.edit.gui.base", usage("/crate crate edit gui ", Crt, arg(true, "gui", "the new gui value")))
            .put("teslacrate.command.crate.edit.keys.add.base", usage("/crate crate edit keys add ", Crt, Key, OptQnt))
            .put("teslacrate.command.crate.edit.keys.remove.base", usage("/crate crate edit keys remove ", Crt, Key))
            .put("teslacrate.command.crate.edit.message.base", usage("/crate crate edit message ", Crt, arg(true, "message", "the new message.")))
            .put("teslacrate.command.crate.edit.particle.base", usage("/crate crate edit particle ", Crt, arg(true, "particle", "the new particle value")))
            .put("teslacrate.command.crate.edit.rewards.add.base", usage("/crate crate edit rewards add ", Crt, Rwd, arg(false, "weight", "the weight.")))
            .put("teslacrate.command.crate.edit.rewards.remove.base", usage("/crate crate edit rewards remove ", Crt, Rwd))
            .put("teslacrate.command.crate.give.base", usage("/crate crate give ", Plr, Crt, arg(false, "reward", "name of a reward.")))
            .put("teslacrate.command.crate.menu.base", usage("/crate crate menu ", arg(false, "crate", "name of a crate.")))
            .put("teslacrate.command.crate.preview.base", usage("/crate crate preview ", Crt))
            .put("teslacrate.command.item.edit.description.base", usage("/crate item edit description ", Itm, EdtDsc))
            .put("teslacrate.command.item.edit.display-name.base", usage("/crate item edit display-name ", Itm, EdtDpN))
            .put("teslacrate.command.item.edit.quantity.base", usage("/crate item edit quantity ", Itm, EdtQnt))
            .put("teslacrate.command.item.give.base", usage("/crate item give ", Plr, Itm, OptQnt))
            .put("teslacrate.command.item.menu.base", usage("/crate item menu ", arg(false, "item", "name of an item.")))
            .put("teslacrate.command.key.edit.description.base", usage("/crate key edit description ", Key, EdtDsc))
            .put("teslacrate.command.key.edit.display-name.base", usage("/crate key edit display-name ", Key, EdtDpN))
            .put("teslacrate.command.key.edit.quantity.base", usage("/crate key edit quantity ", Key, EdtQnt))
            .put("teslacrate.command.key.check.base", usage("/crate key check ", Usr, Key))
            .put("teslacrate.command.key.give.base", usage("/crate key give ", Usr, Key, OptQnt))
            .put("teslacrate.command.key.list.base", usage("/crate key list ", arg(false, "user", "the user")))
            .put("teslacrate.command.key.menu.base", usage("/crate key menu ", arg(false, "item", "name of a key.")))
            .put("teslacrate.command.key.take.base", usage("/crate key take ", Key, OptQnt))
            .put("teslacrate.command.location.delete.base", usage("/crate location delete ", Loc))
            .put("teslacrate.command.location.menu.base", usage("/crate location menu "))
            .put("teslacrate.command.location.set.base", usage("/crate location set ", Loc, Crt))
            .put("teslacrate.command.menu.base", usage("/crate menu "))
            .put("teslacrate.command.reward.edit.announce.base", usage("/crate reward edit announce ", Rwd, arg(true, "announce", "the new announce value.")))
            .put("teslacrate.command.reward.edit.weight.base", usage("/crate reward edit weight ", Rwd, arg(true, "chance", "the new chance.")))
            .put("teslacrate.command.reward.edit.commands.add.base", usage("/crate reward edit commands add ", Rwd, Cmd, OptVal))
            .put("teslacrate.command.reward.edit.commands.remove.base", usage("/crate reward edit commands remove ", Rwd, Cmd))
            .put("teslacrate.command.reward.edit.description.base", usage("/crate reward edit description ", Rwd, EdtDsc))
            .put("teslacrate.command.reward.edit.display-name.base", usage("/crate reward edit display-name ", Rwd, EdtDpN))
            .put("teslacrate.command.reward.edit.items.add.base", usage("/crate reward edit items add ", Rwd, Itm, OptQnt))
            .put("teslacrate.command.reward.edit.items.remove.base", usage("/crate reward edit items remove  ", Rwd, Itm))
            .put("teslacrate.command.reward.give.base", usage("/crate reward give ", Plr, Rwd))
            .put("teslacrate.command.reward.menu.base", usage("/crate reward menu ", arg(false, "reward", "name of a reward.")))
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        PaginationList.builder()
                .title(TeslaCrate.getTesla().Prefix)
                .padding(Text.of(TextColors.GRAY, "="))
                .contents(Usages.entrySet().stream().filter(e -> src.hasPermission(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toList()))
                .footer(Text.of("                  ").concat(Text.joinWith(Text.of(TextColors.GRAY, " | "), link("Sponge Thread", TeslaCrate.getTesla().URL), link("Support Discord", Tesla.Discord))))
                .sendTo(src);
        return CommandResult.success();
    }

    private static Text usage(String base, Text... args) {
        return Text.builder(base)
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand(base))
                .append(Text.joinWith(Text.of(" "), args))
                .build();
    }

    private static Text arg(boolean req, String name, String desc) {
        return Text.builder((req ? "<" : "[") + name + (req ? ">" : "]"))
                .color(TextColors.YELLOW)
                .onHover(TextActions.showText(Text.of(TextColors.WHITE, name, ": ", TextColors.GRAY, desc)))
                .build();
    }

    private static Text link(String name, Optional<URL> optUrl) {
        return optUrl.map(u -> Text.builder(name)
                .style(TextStyles.UNDERLINE)
                .onClick(TextActions.openUrl(u))
                .onHover(TextActions.showText(Text.of(u)))
                .build()).orElse(Text.builder(name)
                .color(TextColors.RED)
                .onHover(TextActions.showText(Text.of("Sorry! This URL is unavailable.")))
                .build());
    }

}