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

public class Gui implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Gui())
            .arguments(Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    GenericArguments.enumValue(Text.of("gui"), Effects.Gui.class))
            .permission("teslacrate.command.crate.edit.gui.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Crate crate = args.<Crate>getOne("crate").get();
        Effects.Gui newGui = args.<Effects.Gui>getOne("gui").get();
        Effects.Gui oldGui = crate.getGui();
        crate.setGui(newGui);
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.edit.gui.success", "crate", crate.getName(), "new-gui", newGui.name().toLowerCase(), "old-gui", oldGui.name().toLowerCase());
        return CommandResult.success();
    }

}