package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.crate.Crate;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.*;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"set"})
@Permission("teslacrate.command.location.set.base")
public final class Set extends Command {

    @Inject
    private Set(Settings settings) {
        super(settings.usage(usage("/teslacrate location set ", "Sets a location as a crate", LOCATION_ARG, CRATE_ARG)).elements(LOCATION_ELEM, CRATE_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Location<World> location = args.<Location<World>>getOne("location").get();
        location = new Location<>(location.getExtent(), location.getBlockPosition());
        Crate crate = args.<Crate> getOne("crate").get();
        Registration registration = Config.getRegistration(location).orElse(null);
        if (registration != null) {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.location.set.already-set", "location", location.getExtent().getName() + location.getPosition().toInt().toString(), "crate", registration.getCrate().getIcon()));
        }
        Config.addRegistration(new Registration(location.getExtent().getName() + location.getBlockPosition(), location.add(0.5, 0.5, 0.5), crate));
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.location.set.success", "location", location.getExtent().getName() + location.getBlockPosition(), "crate", crate.getId()));
        return CommandResult.success();
    }

}
