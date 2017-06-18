package com.mcsimonflash.sponge.teslacrate.commands.locations;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
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

public class DeleteLoc implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String worldName = args.<String>getOne("world-name").isPresent() ? args.<String>getOne("world-name").get() : null;
        Vector3d rawPos = args.<Vector3d>getOne("crate-pos").isPresent() ? args.<Vector3d>getOne("crate-pos").get() : null;

        Vector3i cratePos;
        if (worldName == null || rawPos == null) {
            if (src instanceof Player) {
                worldName = worldName == null ? ((Player) src).getWorld().getName() : worldName;
                cratePos = rawPos == null ? ((Player) src).getLocation().getBlockPosition() : new Vector3i(rawPos.getFloorX(), rawPos.getFloorY(), rawPos.getFloorZ());
            } else {
                src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7The world and position must be defined to use this command!")));
                return CommandResult.empty();
            }
        } else {
           cratePos = new Vector3i(rawPos.getFloorX(), rawPos.getFloorY(), rawPos.getFloorZ());
        }
        CrateLocation crateLoc = new CrateLocation(worldName, cratePos);
        Crate crate = Storage.crateRegistry.get(crateLoc);
        if (crate != null) {
            Storage.crateRegistry.remove(crateLoc);
            Storage.writeLocation(crate, crateLoc);
            src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Successfully unregistered crate location &f" + crateLoc.print() + "&7!")));
            return CommandResult.success();
        } else {
            src.sendMessage(Config.teslaPrefix.concat(Util.toText("&7No crate registered at location &f" + crateLoc.print() + "&7!")));
        }
        return CommandResult.empty();
    }
}
