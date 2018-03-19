package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Registration;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Singleton
@Aliases({"delete", "del"})
@Permission("teslacrate.command.location.delete.base")
public class Delete extends TeslaCommand {

    @Inject
    private Delete() {
        super(CmdUtils.usage("/teslacrate location delete ", "Deletes a registered location", WORLD_ARG, POSITION_ARG),
                settings().arguments(LOCATION_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Location<World> location = args.<Location<World>>getOne("location").get();
        location = new Location<>(location.getExtent(), location.getBlockPosition());
        Registration existing = Storage.registry.remove(location);
        if (existing != null) {
            existing.stopParticles();
            Config.deleteLocation(location);
            TeslaCrate.sendMessage(src, "teslacrate.command.location.delete.success", "location", location.getExtent().getName()  + location.getPosition().toInt().toString(), "crate", existing.getCrate().getName());
            return CommandResult.success();
        } else {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.location.delete.not-set", "location", location.getExtent().getName() + location.getPosition().toInt().toString()));
        }
    }

}