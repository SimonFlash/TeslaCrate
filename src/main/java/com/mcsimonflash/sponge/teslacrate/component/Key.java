package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.Preconditions;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
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
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.List;

public class Key extends Component {

    private DefVal<ItemStack> item = DefVal.of(ItemStack.empty());
    private int quantity = 1;

    public Key(String name) {
        super(name);
    }

    public boolean isPhysical() {
        return item.isPresent();
    }

    public int check(User user) {
        return item.isPresent() ? user.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item.get())).totalItems() : Config.getKeys(user, this);
    }

    public boolean give(User user, int quantity) {
        return quantity == 0 || item.isPresent() ? Utils.offerItem(user, item.get(), quantity).getType() == InventoryTransactionResult.Type.SUCCESS : Config.setKeys(user, this, check(user) + quantity);
    }

    public boolean take(User user, int quantity) {
        return quantity == 0 || item.isPresent() ? user.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item.get())).poll(quantity).isPresent() : Config.setKeys(user, this, check(user) - quantity);
    }

    @Override
    public void deserialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        super.deserialize(node);
        setQuantity(node.getNode("quantity").getInt(1));
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
    public List<Element> getMenuElements(Element back) {
        List<Element> elements = super.getMenuElements(back);
        elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Type", isPhysical() ? "physical" : "virtual", false)));
        item.ifPresent(i -> elements.add(Element.of(Utils.createItem(ItemTypes.PAPER, "Item", Utils.printItem(i), false))));
        elements.add(Inventory.createDetail("Quantity", String.valueOf(getQuantity())));
        return elements;
    }

}