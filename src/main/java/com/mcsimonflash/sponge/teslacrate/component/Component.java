package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslacore.util.DefVal;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Component {

    private String name;
    private DefVal<String> description = DefVal.of("");
    private DefVal<String> displayName = DefVal.of("");
    private DefVal<ItemStack> displayItem = DefVal.of(ItemStack.empty());
    private boolean global = true;

    public Component(String name) {
        this.name = name;
        description.setDef(name);
        displayName.setDef(name.contains(":") ? name.substring(name.lastIndexOf(":") + 1) : name);
    }

    public void deserialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        setDescription(node.getNode("description").getString());
        setDisplayName(node.getNode("display-name").getString());
        NodeUtils.ifAttached(node.getNode("display-item"), n -> setDisplayItem(Serializers.deserializeItemStack(n)));
        displayItem.setDef(getDefaultDisplayItem());
        displayItem.ifPresent(i -> {
            if (displayName.isPresent() && !i.get(Keys.DISPLAY_NAME).isPresent()) {
                i.offer(Keys.DISPLAY_NAME, Utils.toText(getDisplayName()));
            }
            if (description.isPresent() && !i.get(Keys.ITEM_LORE).isPresent()) {
                i.offer(Keys.ITEM_LORE, Lists.newArrayList(Utils.toText(getDescription())));
            }
        });
    }

    public void serialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        description.ifPresent(d -> node.getNode("description").setValue(d));
        displayName.ifPresent(d -> node.getNode("display-name").setValue(d));
        displayItem.ifPresent(i -> Serializers.serializeItemStack(node.getNode("display-item"), i));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(@Nullable String description) {
        this.description.setVal(description);
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public void setDisplayName(@Nullable String displayName) {
        this.displayName.setVal(displayName);
    }

    public ItemStack getDisplayItem() {
        return displayItem.get();
    }

    public void setDisplayItem(@Nullable ItemStack displayItem) {
        this.displayItem.setVal(displayItem);
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    protected ItemStack getDefaultDisplayItem() {
        return Utils.createItem(ItemTypes.PAPER, getDisplayName(), getDescription(), true);
    }

    public List<Element> getMenuElements() {
        List<Element> elements = Lists.newArrayList();
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Name", getName(), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Description", getDescription(), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Display Name", getDisplayName(), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Display Item", Utils.printItem(getDisplayItem()), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Global", String.valueOf(isGlobal()), false)));
        return elements;
    }

    @Override
    public String toString() {
        return "Name: " + getName() + "\nDescription: " + getDescription() + "\nDisplayName: " + getDisplayName() + "\nDisplayItem: " + Utils.printItem(getDisplayItem()) + "\nGlobal: " + isGlobal();
    }

}