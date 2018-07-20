package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Key extends Referenceable<Integer> {

    private int quantity = 1;

    protected Key(String id) {
        super(id);
    }

    public final int getQuantity() {
        return quantity;
    }

    public final void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public abstract int get(User user);

    public abstract boolean give(User user, int quantity);

    public abstract boolean take(User user, int quantity);

    @Override
    @OverridingMethodsMustInvokeSuper
    public void deserialize(ConfigurationNode node) {
        setQuantity(node.getNode("quantity").getInt(1));
        super.deserialize(node);
    }

    @Override
    protected ItemStack.Builder createDisplayItem(Integer value) {
        return Utils.createItem(ItemTypes.NAME_TAG, getName(), Lists.newArrayList(getDescription(), Utils.toText("&6Quantity&8: &e" + value)));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent).add(indent + "quantity", quantity);
    }

    @Override
    public final Integer getRefValue() {
        return getQuantity();
    }

    @Override
    public final Ref createRef(String id) {
        return new Ref(id, this);
    }

    public final static class Ref extends Reference<Key, Integer> {

        private Ref(String id, Key component) {
            super(id, component);
        }

        @Override
        public final Integer deserializeValue(ConfigurationNode node) {
            return node.getInt(getComponent().getRefValue());
        }

    }

}