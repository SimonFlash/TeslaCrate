package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslalibs.message.Message;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;

public final class CommandPrize extends Prize<String> {

    public static final Type<CommandPrize, String> TYPE = Type.create("Command", CommandPrize::new, n -> !n.getNode("command").isVirtual());

    private String command = "";

    private CommandPrize(String name) {
        super(name);
    }

    public final String getCommand() {
        return command;
    }

    @Override
    public final boolean give(User user, String value) {
        if (user.isOnline()) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), Message.of(getCommand()).args("value", value, "player", user.getName()).toString());
            return true;
        }
        return false;
    }

    @Override
    public final String getRefValue() {
        return command;
    }

    @Override
    public final Ref createRef(String name) {
        return new Ref(name, this);
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("command", command);
    }

    public static final class Ref extends Prize.Ref<CommandPrize, String> {

        protected Ref(String name, CommandPrize component) {
            super(name, component);
        }

        @Override
        public final void deserialize(ConfigurationNode node) {
            super.deserialize(node);
            setValue(node.getString(getValue()));
        }

    }

}