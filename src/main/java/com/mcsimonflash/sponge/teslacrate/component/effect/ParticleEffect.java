package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.flowpowered.math.vector.Vector3d;
import com.mcsimonflash.sponge.teslacrate.component.Reference;
import com.mcsimonflash.sponge.teslacrate.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.*;

public class ParticleEffect extends Effect<ParticleEffect, Vector3d> {

    public static final Type<ParticleEffect, Vector3d, Builder, RefBuilder> TYPE = Type.create("Particle", Builder::new, RefBuilder::new).build();

    private final Vector3d offset;

    private ParticleEffect(Builder builder) {
        super(builder);
        offset = builder.offset;
    }

    @Override
    public final void run(Player player, Location<World> location, Vector3d value) {}

    public final Vector3d getOffset() {
        return offset;
    }

    public static class Builder extends Effect.Builder<ParticleEffect, Vector3d, Builder> {

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
        public final ParticleEffect build() {
            return new ParticleEffect(this);
        }

    }

    public static class RefBuilder extends Reference.Builder<ParticleEffect, Vector3d, RefBuilder> {

        private RefBuilder(String id, ParticleEffect component) {
            super(id, component);
        }

        @Override
        public final RefBuilder deserialize(ConfigurationNode node) throws ConfigurationException {
            return super.deserialize(node).value(Serializers.deserializeVector3d(node));
        }

    }

}
