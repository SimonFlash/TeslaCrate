package com.mcsimonflash.sponge.teslacrate.api.component;

import com.flowpowered.math.vector.Vector3d;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Effect<T extends Effect<T, V>, V> extends Component<T, V> {

    public enum Action {
        PASSIVE, ON_OPEN, ON_RECEIVE, ON_REJECT, ON_PREVIEW
    }

    protected Effect(String id) {
        super(id);
    }

    public abstract void run(Player player, Location<World> location, V value);

    public abstract static class Locatable<T extends Effect.Locatable<T>> extends Effect<T, Vector3d> {

        public enum Target {
            PLAYER, LOCATION
        }

        private Vector3d offset = Vector3d.ZERO;
        private Locatable.Target target = Locatable.Target.LOCATION;

        protected Locatable(String id) {
            super(id);
        }

        @Override
        protected Vector3d getValue() {
            return offset;
        }

        public abstract void run(Location<World> location);

        @Override
        public final void run(Player player, Location<World> location, Vector3d offset) {
            run((target == Locatable.Target.PLAYER ? player.getLocation() : location).add(offset));
        }

        @Override @OverridingMethodsMustInvokeSuper
        public void deserialize(ConfigurationNode node) {
            NodeUtils.ifAttached(node.getNode("offset"), n -> offset = Serializers.deserializeVector3d(n));
            NodeUtils.ifAttached(node.getNode("target"), n -> target = Serializers.deserializeEnum(n, Target.class));
            super.deserialize(node);
        }

        public Vector3d deserializeValue(ConfigurationNode node) {
            return Serializers.deserializeVector3d(node);
        }

    }

}