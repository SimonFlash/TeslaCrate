package com.mcsimonflash.sponge.teslacrate.command;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.argument.Arguments;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

@Aliases({"info"})
@Permission("teslacrate.command.info.base")
public final class Info extends Command {

    @Inject
    private Info(Settings settings) {
        super(settings.usage(CmdUtils.usage("teslacrate info ", "Displays info about a component", Utils.toText("&e<type>"), Utils.toText("&e<id>")))
                .elements(Arguments.choices(ImmutableMap.of("crate", Registry.CRATES, "effect", Registry.EFFECTS, "key", Registry.KEYS, "prize", Registry.PRIZES, "reward", Registry.REWARDS), ImmutableMap.of()).toElement("type"), Arguments.string().toElement("id")));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.of(args.<Registry<?>>getOne("type").get().get(args.<String>getOne("id").get()).orElseThrow(() -> new CommandException(Text.of("No component exists with id " + args.<String>getOne("id").get() + ".")))));
        return CommandResult.success();
    }

}
