package com.mcsimonflash.sponge.teslacrate.api.component;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public abstract class Effect<V> extends Referenceable<V> {

    protected Effect(String name) {
        super(name);
    }

    public abstract void run(Player player, Location<World> location, V value);

    @Override
    public Reference<? extends Referenceable<V>, V> createRef(String name) {
        return new Ref<>(name, this);
    }

    public static class Ref<T extends Effect<V>, V> extends Reference<T, V> {

        protected Ref(String name, T component) {
            super(name, component);
        }

        public void run(Player player, Location<World> location) {
            getComponent().run(player, location, getValue());
        }

    }

}