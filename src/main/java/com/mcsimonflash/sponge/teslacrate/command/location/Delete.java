package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.*;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"delete"})
@Permission("teslacrate.command.location.delete.base")
public final class Delete extends Command {

    @Inject
    private Delete(Settings settings) {
        super(settings.usage(usage("/teslacrate location delete ", "Deletes a registered location.", LOCATION_ARG)).elements(LOCATION_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Location<World> location = args.<Location<World>>getOne("location").get();
        location = new Location<>(location.getExtent(), location.getBlockPosition());
        Registration registration = Config.getRegistration(location).orElse(null);
        if (registration == null) {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.location.delete.not-set", "location", location.getExtent().getName() + location.getBlockPosition()));
        }
        Config.removeRegistration(registration);
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.location.delete.success", "location", location.getExtent().getName() + location.getBlockPosition(), "crate", registration.getCrate().getId()));
        return CommandResult.success();
    }

}
