package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.mcsimonflash.sponge.teslacrate.api.component.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import com.mcsimonflash.sponge.teslalibs.message.Message;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public final class CommandPrize extends Prize<CommandPrize, String> {

    public static final Type<CommandPrize, String, Builder, RefBuilder> TYPE = Type.create("Command", Builder::new, RefBuilder::new).build();

    private final String command;
    private final String value;

    private CommandPrize(Builder builder) {
        super(builder);
        command = builder.command;
        value = builder.value;
    }

    @Override
    public final void give(Player player, String value) {
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), Message.of(getCommand()).args("value", value, "player", player.getName()).toString());
    }

    public final String getCommand() {
        return command;
    }

    public final String getValue() {
        return value;
    }

    public static final class Builder extends Prize.Builder<CommandPrize, String, Builder> {

        private String command;
        private String value;

        private Builder(String id) {
            super(id, TYPE);
        }

        public final Builder command(String command) {
            this.command = command;
            return this;
        }

        public final Builder value(String value) {
            this.value = value;
            return this;
        }

        @Override
        public final Builder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node)
                    .command(node.getNode("command").getString(""))
                    .value(node.getNode("value").getString(""));
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
            return super.deserialize(node).value(node.getString(component.getValue()));
        }

    }

}
