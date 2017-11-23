package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.Preconditions;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslacore.util.DefVal;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public class Key extends Component {

    private DefVal<ItemStack> item = DefVal.of(ItemStack.empty());
    private int quantity = 1;

    public Key(String name) {
        super(name);
    }

    public int check(User user) {
        return item.isPresent() ? user.getInventory().queryAny(item.getVal()).totalItems() : Config.getKeys(user, this);
    }

    public boolean give(User user, int quantity) {
        return item.isPresent() ? user.getInventory().offer(ItemStack.builder().fromContainer(item.getVal().toContainer()).quantity(quantity).build()).getRejectedItems().isEmpty() : Config.setKeys(user, this, check(user) + quantity);
    }

    public boolean take(User user, int quantity) {
        return item.isPresent() ? user.getInventory().queryAny(item.getVal()).poll(quantity).isPresent() : Config.setKeys(user, this, check(user) - quantity);
    }

    @Override
    public void deserialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        super.deserialize(node);
        quantity = node.getNode("quantity").getInt(1);
        NodeUtils.ifAttached(node.getNode("item"), n -> {
            if (n.hasMapChildren()) {
                setItem(ItemStack.builder()
                        .fromContainer(Serializers.deserializeItemStack(n).toContainer().set(DataQuery.of("UnsafeData", "TeslaCrate", "Key"), getName()))
                        .build());
            } else {
                throw new ConfigurationNodeException(n, "A physical key may not use a reference item.").asUnchecked();
            }
        });
    }

    @Override
    public void serialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        super.serialize(node);
        node.getNode("quantity").setValue(quantity == 1 ? null : quantity);
        item.ifPresent(i -> Serializers.serializeItemStack(node.getNode("item"), i));
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        Preconditions.checkArgument(quantity > 0, "Quantity must be greater than 0.");
        this.quantity = quantity;
    }

    public ItemStack getItem() {
        return item.get();
    }

    public void setItem(ItemStack item) {
        this.item.setVal(item);
    }

    @Override
    protected ItemStack getDefaultDisplayItem() {
        return item.isPresent() ? item.getVal() : Utils.createItem(ItemTypes.NAME_TAG, getDisplayName(), getDescription(), true);
    }

    @Override
    public List<Element> getMenuElements() {
        List<Element> elements = super.getMenuElements();
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Item", Utils.printItem(getItem()), false)));
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Quantity", String.valueOf(getQuantity()), false)));
        return elements;
    }

    @Override
    public String toString() {
        return super.toString() + "\nQuantity: " + quantity + (item.isPresent() ? "Item: " + Utils.printItem(item.getVal()): "");
    }

}