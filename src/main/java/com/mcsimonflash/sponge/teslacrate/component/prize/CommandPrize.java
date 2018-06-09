package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import com.mcsimonflash.sponge.teslalibs.message.Message;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;

import static com.google.common.base.Preconditions.checkState;

public final class CommandPrize extends Prize<CommandPrize, String> {

    public static final Type<CommandPrize, String, Builder, RefBuilder> TYPE = Type.create("Command", Builder::new, RefBuilder::new)
            .matcher(n -> !n.getNode("command").isVirtual())
            .build();

    private final String command;

    private CommandPrize(Builder builder) {
        super(builder);
        command = builder.command;
    }

    @Override
    public final boolean give(User user, String value) {
        if (user.isOnline()) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), Message.of(getCommand()).args("value", value, "player", user.getName()).toString());
            return true;
        }
        return false;
    }

    public final String getCommand() {
        return command;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("command", command);
    }

    public static final class Builder extends Prize.Builder<CommandPrize, String, Builder> {

        private String command;

        private Builder(String id) {
            super(id, TYPE);
        }

        public final Builder command(String command) {
            this.command = command;
            return this;
        }

        @Override
        public final Builder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node)
                    .command(node.getNode("command").getString(""))
                    .value(node.getNode("value").getString(""));
        }

        @Override
        protected void resolve() throws IllegalStateException {
            super.resolve();
            checkState(!command.isEmpty(), "Command may not be empty.");
        }

        @Override
        public final CommandPrize build() {
            return new CommandPrize(this);
        }

    }

    public static final class RefBuilder extends Reference.Builder<CommandPrize, String, RefBuilder> {

        private RefBuilder(String id, CommandPrize component) {
            super(id, component);
        }

        @Override
        public final RefBuilder deserialize(ConfigurationNode node) throws ConfigurationException {
            String value = node.getString("");
            return super.deserialize(node).value(value.isEmpty() ? this.value : value);
        }

    }

}
