package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import com.mcsimonflash.sponge.teslalibs.message.Message;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

public final class CommandPrize extends Prize<String> {

    public static final Type<CommandPrize> TYPE = new Type<>("Command", CommandPrize::new, n -> !n.getNode("command").isVirtual(), TeslaCrate.get().getContainer());

    public enum Source {
        PLAYER, SERVER
    }

    private String command = "";
    private String value = "<value>";
    private Source source = Source.SERVER;
    private boolean online = true;

    private CommandPrize(String id) {
        super(id);
    }

    public final String getCommand() {
        return command;
    }

    public final void setCommand(String command) {
        this.command = command;
    }

    public final String getValue() {
        return value;
    }

    public final void setValue(String value) {
        this.value = value;
    }

    public final Source getSource() {
        return source;
    }

    public final void setSource(Source source) {
        this.source = source;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public final boolean give(User user, String value) {
        if (!command.isEmpty() && (user.isOnline() || (!online && source == Source.SERVER))) {
            Sponge.getCommandManager().process(source == Source.PLAYER ? user.getPlayer().get() : Sponge.getServer().getConsole(), Message.of(getCommand()).args("player", user.getName(), "value", value).toString());
            return true;
        }
        return false;
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        if (node.getNode("command").hasMapChildren()) {
            setCommand(node.getNode("command", "command").getString(""));
            setValue(node.getNode("command", "value").getString("<value>"));
            NodeUtils.ifAttached(node.getNode("command", "source"), n -> setSource(Serializers.deserializeEnum(n, Source.class)));
            setOnline(node.getNode("command", "online").getBoolean(false));
        } else {
            setCommand(node.getNode("command").getString(""));
        }
        super.deserialize(node);
    }

    @Override
    protected ItemStack.Builder createDisplayItem(String value) {
        return Utils.createItem(ItemTypes.FILLED_MAP, getName(), Lists.newArrayList(Utils.toText(getCommand().replace("<value>", value))));
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "command", String.format("\"%s\"", command))
                .add(indent + "value", String.format("\"%s\"", value))
                .add(indent + "source", source.name().toLowerCase())
                .add(indent + "online", online);
    }

    @Override
    public final String getRefValue() {
        return command;
    }

    @Override
    public final Ref createRef(String id) {
        return new Ref(id, this);
    }


    public static final class Ref extends Prize.Ref<CommandPrize, String> {

        private Ref(String id, CommandPrize component) {
            super(id, component);
        }

        @Override
        public final String deserializeValue(ConfigurationNode node) {
            return node.getString("").isEmpty() ? getValue() : node.getString("");
        }

    }

}