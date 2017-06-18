package com.mcsimonflash.sponge.teslacrate.objects.keys;

import com.mcsimonflash.sponge.teslacrate.managers.Storage;
import org.spongepowered.api.entity.living.player.User;

public class VirtualKey extends Key {

    public VirtualKey(String name) {
        super(name);
    }

    @Override
    public boolean hasKeys(User user, int quantity) {
        return Storage.getStoredKeys(user, this) >= quantity;
    }

    @Override
    public boolean giveKeys(User user, int quantity) {
        return Storage.setStoredKeys(user, this, Storage.getStoredKeys(user, this) + quantity);
    }

    @Override
    public boolean takeKeys(User user, int quantity) {
        return Storage.setStoredKeys(user, this, Storage.getStoredKeys(user, this) - quantity);
    }
}
