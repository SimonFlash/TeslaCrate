package com.mcsimonflash.sponge.teslacrate.objects.keys;

import org.spongepowered.api.entity.living.player.User;

public abstract class Key {

    public String Name;
    public String DisplayName;

    public Key(String name) {
        Name = name;
    }

    public abstract boolean hasKeys(User user, int quantity);
    public abstract boolean giveKeys(User user, int quantity);
    public abstract boolean takeKeys(User user, int quantity);
}
