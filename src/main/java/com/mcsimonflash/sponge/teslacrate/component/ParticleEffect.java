package com.mcsimonflash.sponge.teslacrate.component;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.MoreObjects;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class ParticleEffect extends Effect<Vector3d> {

    public static final Type<ParticleEffect, Vector3d> TYPE = new Type<>("Particle", ParticleEffect::new, n -> !n.getNode("particle").isVirtual(), TeslaCrate.get().getContainer());

    private Vector3d offset = Vector3d.ZERO;
    private ParticleType particle = ParticleTypes.REDSTONE_DUST;

    private ParticleEffect(String name) {
        super(name);
    }

    public final Vector3d getOffset() {
        return offset;
    }

    public final void setOffset(Vector3d offset) {
        this.offset = offset;
    }

    public final ParticleType getParticle() {
        return particle;
    }

    public final void setParticle(ParticleType particle) {
        this.particle = particle;
    }

    @Override
    public final void run(Player player, Location<World> location, Vector3d value) {}

    @Override
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        NodeUtils.ifAttached(node.getNode("offset"), n -> setOffset(Serializers.deserializeVector3d(n)));
    }

    @Override
    public final Vector3d getRefValue() {
        return offset;
    }

    @Override
    public Ref createRef(String name) {
        return new Ref(name, this);
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "offset", offset)
                .add(indent + "particle", String.format("\"%s\"", particle.getId()));
    }

    public static final class Ref extends Effect.Ref<ParticleEffect, Vector3d> {

        private Ref(String name, ParticleEffect component) {
            super(name, component);
        }

        @Override
        public void deserialize(ConfigurationNode node) {
            super.deserialize(node);
            node.setValue(Serializers.deserializeVector3d(node));
        }

    }

}
