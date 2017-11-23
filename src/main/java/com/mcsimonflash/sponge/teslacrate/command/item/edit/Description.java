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

public class Description implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Description())
            .arguments(Arguments.map("item", Storage.items, Arguments.string("string")),
                    Arguments.remainingStrings("new-description"))
            .permission("teslacrate.command.item.edit.description.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Item item = args.<Item>getOne("item").get();
        String newDescription = args.<String>getOne("new-description").get();
        String oldDescription = item.getDescription();
        item.setDescription(newDescription);
        TeslaCrate.sendMessage(src, "teslacrate.command.item.edit.description.success", "item", item.getName(), "new-description", newDescription, "old-description", oldDescription);
        return CommandResult.success();
    }

}