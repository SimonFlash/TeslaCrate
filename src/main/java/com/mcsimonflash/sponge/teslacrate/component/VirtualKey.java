package com.mcsimonflash.sponge.teslacrate.component;

import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import org.spongepowered.api.entity.living.player.User;

public final class VirtualKey extends Key {

    public static final Type<VirtualKey, Integer> TYPE = Type.create("Virtual", VirtualKey::new);

    private VirtualKey(String name) {
        super(name);
    }

    @Override
    public final int get(User user) {
        return 0;
    }

    @Override
    public final boolean check(User user, int quantity) {
        return false;
    }

    @Override
    public final boolean give(User user, int quantity) {
        return false;
    }

    @Override
    public final boolean take(User user, int quantity) {
        return false;
    }

}