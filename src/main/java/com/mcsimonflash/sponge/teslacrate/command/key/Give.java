package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.key.Key;
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
@Permission("teslacrate.command.key.give.base")
public final class Give extends Command {

    @Inject
    private Give(Settings settings) {
        super(settings.usage(usage("/teslacrate key give ", "Gives a number of keys to a user.", USERS_ARG, KEY_ARG, QUANTITY_ARG)).elements(USERS_ELEM, KEY_ELEM, QUANTITY_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Collection<User> users = args.getAll("users");
        Key key = args.<Key>getOne("key").get();
        Integer quantity = args.<Integer>getOne("quantity").get();
        if (users.isEmpty()) {
            throw new CommandException(getMessage(src, "no-users", users, key, quantity));
        } else if (users.size() == 1) {
            if (!key.give(users.iterator().next(), quantity)) {
                throw new CommandException(getMessage(src, "failure", users, key, quantity));
            }
            return CommandResult.success();
        } else {
            Collection<User> successful = users.stream().filter(u -> key.give(u, quantity)).collect(Collectors.toList());
            if (successful.size() != users.size()) {
                Text message = getMessage(src, "failure", users.stream().filter(u -> !successful.contains(u)).collect(Collectors.toList()), key, quantity);
                if (successful.isEmpty()) {
                    throw new CommandException(message);
                }
                src.sendMessage(message);
            }
            src.sendMessage(getMessage(src, "success", users, key, quantity));
            return CommandResult.successCount(successful.size());
        }
    }

    private static Text getMessage(CommandSource src, String name, Collection<User> users, Key key, Integer quantity) {
        return TeslaCrate.getMessage(src, "teslacrate.command.key.give." + name, "user", users.size() == 1 ? users.iterator().next().getName() : Text.builder("[users]")
                .onHover(TextActions.showText(Text.of(users.stream().map(User::getName).collect(Collectors.toList()))))
                .build(), "key", key.getId(), "quantity", quantity);
    }

}
