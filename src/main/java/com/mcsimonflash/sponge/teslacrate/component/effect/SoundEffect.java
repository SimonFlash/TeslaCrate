package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.flowpowered.math.vector.Vector3d;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.*;

public class SoundEffect extends Effect<SoundEffect, Object> {

    public static final Type<SoundEffect, Object, Builder, RefBuilder> TYPE = Type.create("Sound", Builder::new, RefBuilder::new).build();

    private final Vector3d offset;

    private SoundEffect(Builder builder) {
        super(builder);
        offset = builder.offset;
    }

    @Override
    public final void run(Player player, Location<World> location, Object value) {
        location.getExtent().playSound(SoundTypes.RECORD_13, location.getPosition().add(offset), 1.0);
    }

    public Vector3d getOffset() {
        return offset;
    }

    public static class Builder extends Effect.Builder<SoundEffect, Object, Builder> {

        private Vector3d offset;

        private Builder(String id) {
            super(id, TYPE);
        }

        public final Builder offset(Vector3d offset) {
            this.offset = offset;
            return this;
        }

        @Override
        public final Builder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).offset(Serializers.deserializeVector3d(node));
        }

        @Override
        public final SoundEffect build() {
            return new SoundEffect(this);
        }

    }

    public static class RefBuilder extends Reference.Builder<SoundEffect, Object, RefBuilder> {

        private RefBuilder(String id, SoundEffect component) {
            super(id, component);
        }

        @Override
        public RefBuilder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).value(Serializers.deserializeVector3d(node));
        }

    }

}
