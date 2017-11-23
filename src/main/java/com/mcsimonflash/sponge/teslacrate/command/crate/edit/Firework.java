package com.mcsimonflash.sponge.teslacrate.command.crate.edit;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Firework implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Firework())
            .arguments(Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    Arguments.booleann("firework"))
            .permission("teslacrate.command.crate.edit.firework.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Crate crate = args.<Crate>getOne("command").get();
        Boolean newFirework = args.<Boolean>getOne("firework").get();
        Boolean oldFirework = crate.isFirework();
        crate.setFirework(newFirework);
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.edit.firework.success", "crate", crate.getName(), "new-firework", newFirework, "old-firework", oldFirework);
        return CommandResult.success();
    }

}