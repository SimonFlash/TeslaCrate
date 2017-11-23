package com.mcsimonflash.sponge.teslacrate.command.reward;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Reward;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacore.command.Arguments;
import com.mcsimonflash.sponge.teslacore.command.element.core.ValueElement;
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
                    Arguments.map("reward", Storage.rewards, ValueElement.next()))
            .permission("teslacrate.command.reward.give.base")
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        Reward reward = args.<Reward>getOne("reward").get();
        reward.give(player);
        TeslaCrate.sendMessage(src, "teslacrate.command.reward.give.success", "player", player.getName(), "reward", reward.getName());
        return CommandResult.success();
    }

}