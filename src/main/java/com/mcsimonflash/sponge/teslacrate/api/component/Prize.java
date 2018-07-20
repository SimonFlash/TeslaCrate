package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

public abstract class Prize<V> extends Referenceable<V> {

    protected Prize(String id) {
        super(id);
    }

    public abstract boolean give(User user, V value);

    @Override
    public abstract Ref<? extends Prize<V>, V> createRef(String id);

    @Override
    protected ItemStack.Builder createDisplayItem(V value) {
        return Utils.createItem(ItemTypes.NETHER_STAR, getName(), Lists.newArrayList(getDescription()));
    }

    public abstract static class Ref<T extends Prize<V>, V> extends Reference<T, V> {

        protected Ref(String id, T component) {
            super(id, component);
        }

        public final void give(User user) {
            getComponent().give(user, getValue());
        }

    }

}