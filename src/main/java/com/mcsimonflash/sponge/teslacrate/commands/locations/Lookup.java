package com.mcsimonflash.sponge.teslacrate.commands.locations;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Storage;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.crates.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.crates.CrateLocation;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class Lookup implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String flags = args.<String>getOne("flags").isPresent() ? args.<String>getOne("flags").get() + " " : null;

        List<CrateLocation> crateLookup = Lists.newArrayList(Storage.crateRegistry.keySet());
        if (flags != null) {
            if (flags.contains("-c:")) {
                String name = flags.substring(flags.indexOf("-c:") + 3, flags.indexOf(" ", flags.indexOf("-c:")));
                if (Storage.crateDirectory.get(name.toLowerCase()) != null) {
                    crateLookup.removeIf(l -> !Storage.crateRegistry.get(l).Name.equalsIgnoreCase(name));
                } else {
                    src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Unable to locate crate &f" + name + "&7!")));
                    return CommandResult.empty();
                }
            }
            if (flags.contains("-k:")) {
                String name = flags.substring(flags.indexOf("-k:") + 3, flags.indexOf(" ", flags.indexOf("-k:")));
                if (Storage.globalKeys.get(name.toLowerCase()) != null) {
                    crateLookup.removeIf(l -> Storage.crateRegistry.get(l).Keys.keySet().stream().noneMatch(k -> k.Name.equalsIgnoreCase(name)));
                } else {
                    src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Unable to locate key &f" + name + "&7!")));
                    return CommandResult.empty();
                }
            }
            if (flags.contains("-p:")) {
                String position = flags.substring(flags.indexOf("-p:") + 3, flags.indexOf(" ", flags.indexOf("-p:")));
                String[] vec3iArr = position.split(",");
                if (vec3iArr.length == 3) {
                    try {
                        Vector3i vec3i = new Vector3i(Integer.parseInt(vec3iArr[0]), Integer.parseInt(vec3iArr[1]), Integer.parseInt(vec3iArr[2]));
                        crateLookup.removeIf(l -> !l.Vec3i.equals(vec3i));
                    } catch (NumberFormatException ignored) {
                        src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Unable to parse position from &f" + position + "&7!")));
                        return CommandResult.empty();
                    }
                } else {
                    src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Insufficient arguments for position &f" + position + "&7!")));
                    return CommandResult.empty();
                }
            }
            if (flags.contains("-r:")) {
                if (src instanceof Player) {
                    String radius = flags.substring(flags.indexOf("-r:") + 3, flags.indexOf(" ", flags.indexOf("-r:")));
                    try {
                        double rad = Double.parseDouble(radius);
                        String world = ((Player) src).getWorld().getName();
                        Vector3i pos = ((Player) src).getLocation().getBlockPosition();
                        crateLookup.removeIf(l -> !l.World.equalsIgnoreCase(world) || l.Vec3i.distance(pos) > rad);
                    } catch (NumberFormatException ignored) {
                        src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Unable to parse radius from &f" + radius + "&7!")));
                        return CommandResult.empty();
                    }
                } else {
                    src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Only a player may use this flag!")));
                    return CommandResult.empty();
                }
            }
            if (flags.contains("-w:")) {
                String world = flags.substring(flags.indexOf("-w:") + 3, flags.indexOf(" ", flags.indexOf("-w:")));
                crateLookup.removeIf(l -> !l.World.equalsIgnoreCase(world));
            }
        }
        List<Text> crateTexts = Lists.newArrayList();
        if (crateLookup.isEmpty()) {
            crateTexts.add(Util.toText("&7No crates found!"));
        } else {
            int c = 1;
            for (CrateLocation crateLoc : crateLookup) {
                crateTexts.add(Text.builder("")
                        .append(Util.toText("&6" + c++ + ": &e" + Storage.crateRegistry.get(crateLoc).Name + "&6 - &7" + crateLoc.print()))
                        .onHover(TextActions.showText(Util.toText("&7Click to suggest a teleport to this location.")))
                        .onClick(TextActions.suggestCommand("/tppos " + crateLoc.Vec3i.getX() + " " + crateLoc.Vec3i.getY() + " " + crateLoc.Vec3i.getZ()))
                        .build());
            }
        }
        PaginationList.builder()
                .padding(Text.of(TextColors.DARK_GRAY, "-"))
                .title(Text.of(TextColors.YELLOW, "Tesla", TextColors.GOLD, "Crate"))
                .contents(crateTexts)
                .sendTo(src);
        return CommandResult.success();
    }
}
