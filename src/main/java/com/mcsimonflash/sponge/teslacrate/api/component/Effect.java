package com.mcsimonflash.sponge.teslacrate.api.component;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public abstract class Effect<V> extends Referenceable<V> {

    protected Effect(String id) {
        super(id);
    }

    public abstract void run(Player player, Location<World> location, V value);

    @Override
    public Ref<? extends Effect<V>, V> createRef(String id) {
        return new Ref<>(id, this);
    }

    public static class Ref<T extends Effect<V>, V> extends Reference<T, V> {

        protected Ref(String id, T component) {
            super(id, component);
        }

        public void run(Player player, Location<World> location) {
            getComponent().run(player, location, getValue());
        }

    }

}