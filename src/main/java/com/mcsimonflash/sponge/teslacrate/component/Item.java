package com.mcsimonflash.sponge.teslacrate.component;

import com.mcsimonflash.sponge.teslacore.util.DefVal;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public class Item extends Component {

    private DefVal<ItemStack> item = DefVal.of(ItemStack.empty());

    public Item(String name) {
        super(name);
    }

    public void give(Player player, int quantity) {
        item.ifPresent(i -> Utils.offerItem(player, i, quantity));
    }

    @Override
    public void deserialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        super.deserialize(node);
        setItem(Serializers.deserializeItemStack(node));
    }

    public ItemStack getItem() {
        return item.get();
    }

    public void setItem(ItemStack item) {
        this.item.setVal(item);
    }

    @Override
    public List<Element> getMenuElements(Element back) {
        List<Element> elements = super.getMenuElements(back);
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Item", Utils.printItem(getItem()), false)));
        return elements;
    }

    @Override
    public ItemStack getDisplayItem() {
        return item.get();
    }

    @Override
    public String toString() {
        return super.toString() + "\nItem: " + Utils.printItem(getItem());
    }

}