package com.mcsimonflash.sponge.teslacrate.command.reward;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Reward;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.Collection;
import java.util.stream.Collectors;

@Aliases({"give"})
@Permission("teslacrate.command.reward.give.base")
public final class Give extends Command {

    @Inject
    private Give(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate reward give ", "Gives a reward to a set of users.", CmdUtils.USERS_ARG, CmdUtils.REWARD_ARG)).elements(CmdUtils.USERS_ELEM, CmdUtils.REWARD_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Collection<User> users = args.getAll("users");
        if (users.isEmpty()) {
            throw new CommandException(TeslaCrate.getMessage(src, "teslacrate.command.no-users"));
        }
        Reward reward = args.<Reward>getOne("reward").get();
        users.forEach(reward::give);
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.reward.give.success", "user", users.size() == 1 ? users.iterator().next().getName() : Text.builder("[users]")
                .onHover(TextActions.showText(Text.of(users.stream().map(User::getName).collect(Collectors.toList()))))
                .build(), "reward", reward.getId(), "value", reward.getRefValue()));
        return CommandResult.successCount(users.size());
    }

}
