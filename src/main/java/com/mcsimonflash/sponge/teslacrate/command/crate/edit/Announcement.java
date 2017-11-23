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

public class Announcement implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Announcement())
            .arguments(Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    Arguments.remainingStrings("new-announcement"))
            .permission("teslacrate.command.crate.edit.announcement.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Crate crate = args.<Crate>getOne("crate").get();
        String newAnnouncement = args.<String>getOne("new-announcement").get();
        String oldAnnouncement = crate.getAnnouncement();
        crate.setAnnouncement(newAnnouncement);
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.edit.announcement.success", "crate", crate.getName(), "new-announcement", newAnnouncement, "old-announcement", oldAnnouncement);
        return CommandResult.success();
    }

}