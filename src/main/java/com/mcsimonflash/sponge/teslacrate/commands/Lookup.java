package com.mcsimonflash.sponge.teslacrate.commands;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.CrateLocation;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Map;

public class Lookup implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String flags = args.<String>getOne("flags").isPresent() ? args.<String>getOne("flags").get() + " " : null;

        List<CrateLocation> crateLookup = Util.getRegisteredCrates();
        if (flags != null) {
            if (flags.contains("-c:")) {
                String name = flags.substring(flags.indexOf("-c:") + 3, flags.indexOf(" ", flags.indexOf("-c:")));
                Crate crate = Util.getStoredCrate(name);
                if (crate != null) {
                    crateLookup.removeIf(l -> !Util.getRegisteredCrate(l).Name.equalsIgnoreCase(name));
                } else {
                    src.sendMessage(Util.prefix.concat(Util.toText("&7Unable to locate crate &f" + name + "&7!")));
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
                        src.sendMessage(Util.prefix.concat(Util.toText("&7Unable to parse position from &f" + position + "&7!")));
                        return CommandResult.empty();
                    }
                } else {
                    src.sendMessage(Util.prefix.concat(Util.toText("&7Insufficient arguments for position &f" + position + "&7!")));
                    return CommandResult.empty();
                }
            }
            if (flags.contains("-r:")) {
                if (src instanceof Player) {
                    String radius = flags.substring(flags.indexOf("-r:") + 3, flags.indexOf(" ", flags.indexOf("-r:")));
                    try {
                        int rad = Integer.parseInt(radius);
                        String world = ((Player) src).getWorld().getName();
                        Vector3i pos = ((Player) src).getLocation().getBlockPosition();
                        crateLookup.removeIf(l -> !l.World.equalsIgnoreCase(world) || l.Vec3i.distance(pos) > rad);
                    } catch (NumberFormatException ignored) {
                        src.sendMessage(Util.prefix.concat(Util.toText("&7Unable to parse radius from &f" + radius + "&7!")));
                        return CommandResult.empty();
                    }
                } else {
                    src.sendMessage(Util.prefix.concat(Util.toText("&7Only a player may use this flag!")));
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
                crateTexts.add(Text.builder(c++ + ": ")
                        .color(TextColors.GOLD)
                        .append(Text.builder(Util.getRegisteredCrate(crateLoc).Name + "- ")
                                .color(TextColors.YELLOW)
                                .append(Text.of(TextColors.GRAY, crateLoc.print()))
                                .build())
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
