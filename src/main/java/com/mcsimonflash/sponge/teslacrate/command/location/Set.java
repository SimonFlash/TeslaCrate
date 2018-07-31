package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Registration;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Aliases("set")
@Permission("teslacrate.command.location.set.base")
public final class Set extends Command {

    @Inject
    private Set(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate location set ", "Sets a location as a crate", CmdUtils.LOCATION_ARG, CmdUtils.CRATE_ARG))
                .elements(CmdUtils.LOCATION_ELEM, CmdUtils.CRATE_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Location<World> location = args.<Location<World>>getOne("location").get();
        location = new Location<>(location.getExtent(), location.getBlockPosition());
        Crate crate = args.<Crate> getOne("crate").get();
        Registration registration = Config.getRegistration(location).orElse(null);
        if (registration != null) {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.location.set.already-set", "location", location.getExtent().getName() + location.getBlockPosition(), "crate", registration.getCrate().getId()));
        }
        registration = new Registration(location.getExtent().getName() + location.getBlockPosition(), location.add(0.5, 0.5, 0.5), crate);
        registration.startEffects();
        Config.addRegistration(registration);
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.location.set.success", "location", location.getExtent().getName() + location.getBlockPosition(), "crate", crate.getId()));
        return CommandResult.success();
    }

}
