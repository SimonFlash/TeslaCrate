package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.mcsimonflash.sponge.teslacrate.component.*;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.*;

public abstract class Effect<T extends Effect<T, V>, V> extends Referenceable<T, V> {

    protected Effect(Builder<T, V, ?> builder) {
        super(builder);
    }

    public abstract void run(Player player, Location<World> location, V value);

    public void run(Player player, Location<World> location) {
        run(player, location, getValue());
    }

    public static abstract class Builder<T extends Effect<T, V>, V, B extends Builder<T, V, B>> extends Referenceable.Builder<T, V, B> {

        protected Builder(String id, Type<T, V, B, ?> type) {
            super(id, type);
        }

    }

}
