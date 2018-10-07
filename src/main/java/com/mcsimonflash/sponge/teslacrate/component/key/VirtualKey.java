package com.mcsimonflash.sponge.teslacrate.component.key;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class VirtualKey extends Key<VirtualKey> {

    public static final Type<VirtualKey, Integer> TYPE = new Type<>("Virtual", VirtualKey::new, n -> !n.getNode("virtual").isVirtual(), TeslaCrate.get().getContainer());

    private int quantity;

    private VirtualKey(String id) {
        super(id);
    }

    @Override
    public Integer getValue() {
        return quantity;
    }

    @Override
    public final int get(User user) {
        return Config.getStoredKeys(user.getUniqueId(), this);
    }

    @Override
    public final boolean give(User user, int quantity) {
        return Config.setStoredKeys(user.getUniqueId(), this, get(user) + quantity);
    }

    @Override
    public final boolean take(User user, int quantity) {
        return Config.setStoredKeys(user.getUniqueId(), this, get(user) - quantity);
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        quantity = node.getNode("quantity").getInt(1);
        super.deserialize(node);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Integer value) {
        return Utils.createItem(ItemTypes.NAME_TAG, getName()).build().createSnapshot();
    }

}