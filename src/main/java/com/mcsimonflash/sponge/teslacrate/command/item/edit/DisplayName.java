package com.mcsimonflash.sponge.teslacrate.command.item.edit;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Item;
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
            .arguments(Arguments.map("item", Storage.items, Arguments.string("string")),
                    Arguments.remainingStrings("new-display-name"))
            .permission("teslacrate.command.item.edit.display-name.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Item item = args.<Item>getOne("item").get();
        String newDisplayName = args.<String>getOne("new-display-name").get();
        String oldDisplayName = item.getDisplayName();
        item.setDisplayName(newDisplayName);
        TeslaCrate.sendMessage(src, "teslacrate.command.item.edit.display-name.success", "item", item.getName(), "new-display-name", newDisplayName, "old-display-name", oldDisplayName);
        return CommandResult.success();
    }

}