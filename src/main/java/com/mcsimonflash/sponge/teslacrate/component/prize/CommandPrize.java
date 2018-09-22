package com.mcsimonflash.sponge.teslacrate.component.prize;

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
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class CommandPrize extends Prize<CommandPrize, String> {

    public static final Type<CommandPrize, String> TYPE = new Type<>("Command", CommandPrize::new, TeslaCrate.get().getContainer());

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

    @Override
    protected final String getValue() {
        return value;
    }

    @Override
    public final boolean give(User user, String value) {
        if (!command.isEmpty() && (user.isOnline() || (!online && source == Source.SERVER))) {
            Sponge.getCommandManager().process(source == Source.PLAYER ? user.getPlayer().get() : Sponge.getServer().getConsole(), Message.of(command).args("value", value, "player", user.getName()).toString());
            return true;
        }
        return false;
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        if (node.getNode("command").hasMapChildren()) {
            command = node.getNode("command", "command").getString("");
            value = node.getNode("command", "value").getString("<value>");
            NodeUtils.ifAttached(node.getNode("command", "source"), n -> source = Serializers.deserializeEnum(n, Source.class));
            online = node.getNode("command", "online").getBoolean(false);
        } else {
            command = node.getNode("command").getString("");
        }
        super.deserialize(node);
    }

    @Override
    public final String deserializeValue(ConfigurationNode node) {
        return node.getString("").isEmpty() ? value : node.getString("");
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(String value) {
        return Utils.createItem(ItemTypes.FILLED_MAP, getName()).build().createSnapshot();
    }

}