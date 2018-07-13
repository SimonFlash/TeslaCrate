package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.message.Message;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

public final class CommandPrize extends Prize<String> {

    public static final Type<CommandPrize, String> TYPE = new Type<>("Command", CommandPrize::new, n -> !n.getNode("command").isVirtual(), TeslaCrate.get().getContainer());

    private String command = "";
    private boolean server = true;
    private String value = "<value>";

    private CommandPrize(String id) {
        super(id);
    }

    public final String getCommand() {
        return command;
    }

    public final void setCommand(String command) {
        this.command = command;
    }

    public final boolean isServer() {
        return server;
    }

    public final void setServer(boolean server) {
        this.server = server;
    }

    public final String getValue() {
        return value;
    }

    public final void setValue(String value) {
        this.value = value;
    }

    @Override
    public final boolean give(User user, String value) {
        if (user.isOnline()) {
            Sponge.getCommandManager().process(server ? Sponge.getServer().getConsole() : user.getPlayer().get(), Message.of(getCommand()).args("value", value, "player", user.getName()).toString());
            return true;
        }
        return false;
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        setCommand(node.getNode("command").getString(""));
        setServer(node.getNode("server").getBoolean(true));
        setValue(node.getNode("value").getString("<value>"));
        super.deserialize(node);
    }

    @Override
    public final String getRefValue() {
        return command;
    }

    @Override
    public final Ref createRef(String id) {
        return new Ref(id, this);
    }

    @Override
    protected ItemStack.Builder createDisplayItem(String value) {
        return Utils.createItem(ItemTypes.FILLED_MAP, getName(), Lists.newArrayList(Utils.toText(getCommand().replace("<value>", value))));
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "command", String.format("\"%s\"", command))
                .add(indent + "server", server)
                .add(indent + "value", String.format("\"%s\"", value));
    }

    public static final class Ref extends Prize.Ref<CommandPrize, String> {

        protected Ref(String id, CommandPrize component) {
            super(id, component);
        }

        @Override
        public final void deserialize(ConfigurationNode node) {
            super.deserialize(node);
            setValue(node.getString("").isEmpty() ? getValue() : node.getString(""));
        }

    }

}