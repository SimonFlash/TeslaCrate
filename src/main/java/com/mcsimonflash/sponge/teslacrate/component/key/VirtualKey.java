package com.mcsimonflash.sponge.teslacrate.component.key;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import org.spongepowered.api.entity.living.player.User;

public final class VirtualKey extends Key {

    public static final Type<VirtualKey, Integer> TYPE = new Type<>("Virtual", VirtualKey::new, n -> !n.getNode("virtual").isVirtual(), TeslaCrate.get().getContainer());

    private VirtualKey(String id) {
        super(id);
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

}