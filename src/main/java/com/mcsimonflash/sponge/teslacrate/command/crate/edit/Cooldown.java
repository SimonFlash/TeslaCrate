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

public class Cooldown implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Cooldown())
            .arguments(Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    Arguments.intt("new-cooldown"))
            .permission("teslacrate.command.crate.edit.cooldown.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Crate crate = args.<Crate>getOne("crate").get();
        Integer newCooldown = args.<Integer>getOne("new-cooldown").get();
        Integer oldCooldown = crate.getCooldown();
        crate.setCooldown(newCooldown);
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.edit.cooldown.success", "crate", crate.getName(), "new-cooldown", newCooldown, "old-cooldown", oldCooldown);
        return CommandResult.success();
    }

}