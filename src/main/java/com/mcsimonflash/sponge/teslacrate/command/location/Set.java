package com.mcsimonflash.sponge.teslacrate.command.location;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Registration;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import com.mcsimonflash.sponge.teslacore.command.element.core.ValueElement;
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

public class Set implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Set())
            .arguments(GenericArguments.location(Text.of("location")),
                    Arguments.map("crate", Storage.crates, ValueElement.next()))
            .permission("teslacrate.command.location.set.base")
            .build();

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