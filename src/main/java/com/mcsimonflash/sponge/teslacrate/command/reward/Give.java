package com.mcsimonflash.sponge.teslacrate.command.reward;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.reward.Reward;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"give"})
@Permission("teslacrate.command.reward.give.base")
public final class Give extends Command {

    @Inject
    private Give(Settings settings) {
        super(settings.usage(usage("/teslacrate reward give ", "Gives a reward to a set of users.", USERS_ARG, PRIZE_ARG)).elements(USERS_ELEM, PRIZE_ELEM));
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
                .build(), "reward", reward.getId(), "value", reward.getValue()));
        return CommandResult.successCount(users.size());
    }

}
