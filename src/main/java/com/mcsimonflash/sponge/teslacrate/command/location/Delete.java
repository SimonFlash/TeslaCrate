package com.mcsimonflash.sponge.teslacrate.command.location;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
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

@Aliases({"delete", "del"})
@Permission("teslacrate.command.location.delete.base")
public final class Delete extends Command {

    @Inject
    private Delete(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate location delete ", "Deletes a registered location.", CmdUtils.LOCATION_ARG)).elements(CmdUtils.LOCATION_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Location<World> location = args.<Location<World>>getOne("location").get();
        location = new Location<>(location.getExtent(), location.getBlockPosition());
        Registration registration = Config.getRegistration(location).orElse(null);
        if (registration == null) {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.location.delete.not-set", "location", location.getExtent().getName() + location.getBlockPosition()));
        }
        registration.stopEffects();
        Config.removeRegistration(registration);
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.location.delete.success", "location", location.getExtent().getName() + location.getBlockPosition(), "crate", registration.getCrate().getId()));
        return CommandResult.success();
    }

}
