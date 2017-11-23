package com.mcsimonflash.sponge.teslacrate.command.crate.edit;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacrate.internal.Effects;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class Particle implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Particle())
            .arguments(Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    GenericArguments.enumValue(Text.of("particle"), Effects.Particle.class))
            .permission("teslacrate.command.crate.edit.particle.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Crate crate = args.<Crate>getOne("crate").get();
        Effects.Particle newParticle = args.<Effects.Particle>getOne("particle").get();
        Effects.Particle oldParticle = crate.getParticle();
        crate.setParticle(newParticle);
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.edit.particle.success", "crate", crate.getName(), "new-particle", newParticle.name().toLowerCase(), "old-particle", oldParticle.name().toLowerCase());
        return CommandResult.success();
    }

}