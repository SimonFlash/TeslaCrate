package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Storage;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Reward extends Component {

    private Map<Command, String> commands = Maps.newHashMap();
    private Map<Item, Integer> items = Maps.newHashMap();
    private boolean announce = true;
    private double weight = 0.0;

    public Reward(String name) {
        super(name);
    }

    public void give(Player player) {
        commands.forEach((c, v) -> c.give(player, v));
        items.forEach((i, q) -> i.give(player, q));
    }

    @Override
    public void deserialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        setAnnounce(node.getNode("announce").getBoolean(true));
        setWeight(node.getNode("weight").getDouble(0.0));
        node.getNode("commands").getChildrenMap().values().forEach(n -> {
            Command command = Serializers.deserializeChild(n, Storage.commands, () -> new Command(getName() + ":" + n.getKey()), "command");
            addCommand(command, n.hasMapChildren() || n.getString("").isEmpty() ? command.getValue() : n.getString());
        });
        node.getNode("items").getChildrenMap().values().forEach(n -> {
            Item item = Serializers.deserializeChild(n, Storage.items, () -> new Item(getName() + ":" + n.getKey()), "item");
            addItem(item, n.hasMapChildren() || n.getInt() <= 0 ? item.getItem().getQuantity() : n.getInt());
        });
        super.deserialize(node);
    }

    public void addCommand(Command command, String value) {
        commands.put(command, value);
    }

    public void addItem(Item item, Integer quantity) {
        items.put(item, quantity);
    }

    public boolean isAnnounce() {
        return announce;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        Preconditions.checkArgument(weight >= 0);
        this.weight = weight;
    }

    @Override
    protected ItemStack getDefaultDisplayItem() {
        Set<Component> components = Sets.union(commands.keySet(), items.keySet());
        if (components.size() == 1) {
            Component component = components.iterator().next();
            return component.getDisplayItem().getType() != ItemTypes.NONE ? component.getDisplayItem() : Utils.createItem(ItemTypes.MAP, getDisplayName(), component instanceof Command ? ((Command) component).getCommand() : "", true);
        } else {
            return Utils.createItem(ItemTypes.BOOK, getDisplayName(), components.stream().map(Component::getDisplayName).collect(Collectors.toList()), true);
        }
    }

    @Override
    public List<Element> getMenuElements(Element back) {
        List<Element> elements = super.getMenuElements(back);
        elements.add(Inventory.createDetail("Weight", String.valueOf(getWeight())));
        Element self = Inventory.createComponent(this, back);
        commands.forEach((c, v) -> elements.add(Element.of(Utils.createItem(ItemTypes.FILLED_MAP, c.getName(), "value=" + v, false), a -> Inventory.page(c.getName(), c.getMenuElements(self), self).open(a.getPlayer(), 0))));
        items.forEach((i, q) -> elements.add(Element.of(Utils.createItem(ItemTypes.MAP, i.getName(), "quantity=" + q, false), a -> Inventory.page(i.getName(), i.getMenuElements(self), self).open(a.getPlayer(), 0))));
        return elements;
    }

    @Override
    public String toString() {
        return super.toString() + "\nCommands: " + String.join(", ", commands.keySet().stream().map(Component::getName).collect(Collectors.toList())) + "\nItems: " + String.join(", ", commands.keySet().stream().map(Component::getName).collect(Collectors.toList())) + "\nWeight: " + getWeight();
    }

}