package com.mcsimonflash.sponge.teslacrate.command.crate;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacrate.component.Reward;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class Give implements CommandExecutor {

    public static final CommandSpec COMMAND = CommandSpec.builder()
            .executor(new Give())
            .arguments(GenericArguments.player(Text.of("player")),
                    Arguments.map("crate", Storage.crates, Arguments.string("string")),
                    GenericArguments.optional(Arguments.map("reward", Storage.rewards, Arguments.string("string"))))
            .permission("teslacrate.command.crate.give.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        Crate crate = args.<Crate>getOne("crate").get();
        Reward reward = args.<Reward>getOne("reward").orElse(crate.getRandomReward());
        crate.give(player, reward, player.getLocation());
        TeslaCrate.sendMessage(src, "teslacrate.command.crate.give.success", "player", player.getName(), "crate", crate.getName());
        return CommandResult.success();
    }

}