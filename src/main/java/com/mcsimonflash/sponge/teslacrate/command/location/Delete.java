package com.mcsimonflash.sponge.teslacrate.command.location;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Registration;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Delete implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Delete())
            .arguments(GenericArguments.location(Text.of("location")))
            .permission("teslacrate.command.location.delete.base")
            .build();

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