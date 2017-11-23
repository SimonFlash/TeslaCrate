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

public class DisplayName implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new DisplayName())
            .arguments(Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    Arguments.remainingStrings("display-name"))
            .permission("teslacrate.command.crate.edit.display-name.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Crate crate = args.<Crate>getOne("crate").get();
        String newDisplayName = args.<String>getOne("new-display-name").get();
        String oldDisplayName = crate.getDisplayName();
        crate.setDisplayName(newDisplayName);
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.edit.display-name.success", "crate", crate.getName(), "new-display-name", newDisplayName, "old-display-name", oldDisplayName);
        return CommandResult.success();
    }

}