package com.mcsimonflash.sponge.teslacrate.command.key;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.Collection;
import java.util.stream.Collectors;

@Aliases("give")
@Permission("teslacrate.command.key.give.base")
public final class Give extends Command {

    @Inject
    private Give(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate key give ", "Gives a number of keys to a user.", CmdUtils.USER_SELECTOR_ARG, CmdUtils.KEY_ARG, CmdUtils.QUANTITY_ARG))
                .elements(CmdUtils.USER_SELECTOR_ELEM, CmdUtils.KEY_ELEM, CmdUtils.QUANTITY_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Collection<User> users = args.getAll("users");
        Key key = args.<Key>getOne("key").get();
        Integer quantity = args.<Integer>getOne("quantity").get();
        if (users.isEmpty()) {
            throw new CommandException(getMessage(src, "no-users", users, key, quantity));
        } else if (users.size() == 1) {
            User user = users.iterator().next();
            if (!key.give(user, quantity)) {
                throw new CommandException(getMessage(src, "failure", users, key, quantity));
            } else if (src != user) {
                src.sendMessage(getMessage(src, "success", users, key, quantity));
            }
            Sponge.getServer().getPlayer(user.getUniqueId()).ifPresent(p -> p.sendMessage(TeslaCrate.getMessage(p, "teslacrate.command.key.give.receive", "key", key.getId(), "quantity", quantity)));
            return CommandResult.success();
        } else {
            Collection<User> successful = users.stream().filter(u -> key.give(u, quantity)).collect(Collectors.toList());
            if (successful.size() != users.size()) {
                Text message = getMessage(src, "failure", users.stream().filter(u -> !successful.contains(u)).collect(Collectors.toList()), key, quantity);
                if (successful.isEmpty()) {
                    throw new CommandException(message);
                }
                src.sendMessage(message);
            } else {
                src.sendMessage(getMessage(src, "success", users, key, quantity));
            }
            successful.stream()
                    .filter(User::isOnline)
                    .map(u -> u.getPlayer().get())
                    .forEach(p -> p.sendMessage(TeslaCrate.getMessage(p, "teslacrate.command.key.give.receive", "key", key.getId(), "quantity", quantity)));
            return CommandResult.successCount(successful.size());
        }
    }

    private static Text getMessage(CommandSource src, String name, Collection<User> users, Key key, Integer quantity) {
        return TeslaCrate.getMessage(src, "teslacrate.command.key.give." + name, "users", users.size() == 1 ? users.iterator().next().getName() : Text.builder("[users]")
                .onHover(TextActions.showText(Text.joinWith(Utils.toText("&7, "), users.stream().map(u -> Utils.toText("&f" + u.getName())).collect(Collectors.toList()))))
                .build(), "key", key.getId(), "quantity", quantity);
    }

}
