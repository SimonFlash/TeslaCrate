package com.mcsimonflash.sponge.teslacrate.component.prize;

import com.mcsimonflash.sponge.teslacrate.component.Referenceable;
import com.mcsimonflash.sponge.teslacrate.component.Type;
import org.spongepowered.api.entity.living.player.User;

public abstract class Prize<T extends Prize<T, V>, V> extends Referenceable<T, V> {

    protected Prize(Builder<T, V, ?> builder) {
        super(builder);
    }

    public abstract boolean give(User user, V value);

    public final boolean give(User user) {
        return give(user, getValue());
    }

    public static abstract class Builder<T extends Prize<T, V>, V, B extends Builder<T, V, B>> extends Referenceable.Builder<T, V, B> {

        protected Builder(String id, Type<T, V, B, ?> type) {
            super(id, type);
        }

    }

}
