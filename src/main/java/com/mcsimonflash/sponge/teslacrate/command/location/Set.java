package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.command.TeslaCommand;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
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
@Aliases("set")
@Permission("teslacrate.command.location.set.base")
public class Set extends TeslaCommand {

    @Inject
    private Set() {
        super(CmdUtils.usage("/teslacrate location set ", "Sets a location to a crate.", WORLD_ARG, POSITION_ARG, CRATE_ARG),
                settings().arguments(LOCATION_ELEM, CRATE_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Location<World> location = args.<Location<World>>getOne("location").get();
        location = new Location<>(location.getExtent(), location.getBlockPosition());
        Registration existing = Storage.registry.get(location);
        if (existing == null) {
            Registration registration = new Registration(location.add(0.5, 0.5, 0.5), args.<Crate>getOne("crate").get());
            registration.startParticles();
            Storage.registry.put(location, registration);
            Config.setLocation(location, registration.getCrate());
            TeslaCrate.sendMessage(src, "teslacrate.command.location.set.success", "location", location.getExtent().getName() + location.getPosition().toInt().toString(), "crate", registration.getCrate().getName());
            return CommandResult.success();
        } else {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.location.set.already-set", "location", location.getExtent().getName() + location.getPosition().toInt().toString(), "crate", existing.getCrate().getName()));
        }
    }

}