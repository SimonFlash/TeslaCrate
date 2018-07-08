package com.mcsimonflash.sponge.teslacrate.component;

import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class SoundEffect extends Effect<Double> {

    public static final Type<SoundEffect, Double> TYPE = Type.create("Sound", SoundEffect::new, n -> !n.getNode("sound").isVirtual());

    private SoundType sound = SoundTypes.ENTITY_SLIME_SQUISH;
    private double volume = 1.0;

    public SoundEffect(String name) {
        super(name);
    }

    public final SoundType getSound() {
        return sound;
    }

    public final void setSound(SoundType sound) {
        this.sound = sound;
    }

    public final double getVolume() {
        return volume;
    }

    public final void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public final void run(Player player, Location<World> location, Double value) {
        location.getExtent().playSound(sound, location.getPosition(), value);
    }

    @Override
    public final Double getRefValue() {
        return volume;
    }

    @Override
    public final Ref createRef(String name) {
        return new Ref(name, this);
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("sound", sound.getId())
                .add("volume", volume);
    }

    public static final class Ref extends Effect.Ref<SoundEffect, Double> {

        private Ref(String name, SoundEffect component) {
            super(name, component);
        }

        @Override
        public void deserialize(ConfigurationNode node) {
            super.deserialize(node);
            setValue(node.getDouble(getValue()));
        }

    }

}
