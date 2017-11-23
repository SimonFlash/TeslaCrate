package com.mcsimonflash.sponge.teslacrate.component;

import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslacore.util.DefVal;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class Command extends Component {

    private DefVal<String> command = DefVal.of("undefined");
    private DefVal<String> value = DefVal.of("<value>");
    private boolean server = true;

    public Command(String name) {
        super(name);
    }

    public void give(Player player, String value) {
        command.ifPresent(c -> Sponge.getCommandManager().process(server ? Sponge.getServer().getConsole() : player, c.replace("<value>", value).replace("<player>", player.getName())));
    }

    @Override
    public void deserialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        setCommand(node.getNode("command").getString());
        setValue(node.getNode("value").getString());
        setServer(node.getNode("server").getBoolean(true));
        super.deserialize(node);
    }

    @Override
    public void serialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        super.serialize(node);
        command.ifPresent(c -> node.getNode("command").setValue(c));
        value.ifPresent(v -> node.getNode("value").setValue(v));
        node.getNode("server").setValue(server ? null : false);
    }

    public String getCommand() {
        return command.get();
    }

    public void setCommand(@Nullable String command) {
        this.command.setVal(command != null && command.startsWith("/") ? command.substring(1) : command);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(@Nullable String value) {
        this.value.setVal(value);
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    @Override
    protected ItemStack getDefaultDisplayItem() {
        return Utils.createItem(ItemTypes.FILLED_MAP, getDisplayName(), getCommand(), true);
    }

    @Override
    public List<Element> getMenuElements() {
        List<Element> elements = super.getMenuElements();
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Command", getCommand(), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Value", getValue(), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Server", String.valueOf(isServer()), false)));
        return elements;
    }

    @Override
    public String toString() {
        return super.toString() + "\nCommand: " + getCommand() + "\nValue: " + getValue() + "\nServer: " + isServer();
    }

}