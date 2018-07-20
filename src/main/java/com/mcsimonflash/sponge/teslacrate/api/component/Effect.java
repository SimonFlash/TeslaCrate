package com.mcsimonflash.sponge.teslacrate.api.component;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Effect<V> extends Referenceable<V> {

    protected Effect(String id) {
        super(id);
    }

    public abstract void run(Player player, Location<World> location, V value);

    @Override
    public abstract Ref<? extends Effect<V>, V> createRef(String id);

    @Override
    protected ItemStack.Builder createDisplayItem(V value) {
        return Utils.createItem(ItemTypes.FIREWORKS, getName(), Lists.newArrayList(getDescription()));
    }

    public abstract static class Ref<T extends Effect<V>, V> extends Reference<T, V> {

        protected Ref(String id, T component) {
            super(id, component);
        }

        public void run(Player player, Location<World> location) {
            getComponent().run(player, location, getValue());
        }

    }

    public abstract static class Locatable extends Effect<Vector3d> {

        public enum Target {
            PLAYER, LOCATION
        }

        private Vector3d offset = Vector3d.ZERO;
        private Target target = Target.LOCATION;

        protected Locatable(String id) {
            super(id);
        }

        public final Vector3d getOffset() {
            return offset;
        }

        public final void setOffset(Vector3d offset) {
            this.offset = offset;
        }

        public final Target getTarget() {
            return target;
        }

        public final void setTarget(Target target) {
            this.target = target;
        }

        public abstract void run(Location<World> location);

        @Override
        public final void run(Player player, Location<World> location, Vector3d offset) {
            run((getTarget() == Target.PLAYER ? player.getLocation() : location).add(offset));
        }

        @Override
        @OverridingMethodsMustInvokeSuper
        public void deserialize(ConfigurationNode node) {
            NodeUtils.ifAttached(node.getNode("offset"), n -> setOffset(Serializers.deserializeVector3d(n)));
            NodeUtils.ifAttached(node.getNode("target"), n -> setTarget(Serializers.deserializeEnum(n, Target.class)));
            super.deserialize(node);
        }

        @Override
        @OverridingMethodsMustInvokeSuper
        protected MoreObjects.ToStringHelper toStringHelper(String indent) {
            return super.toStringHelper(indent)
                    .add("offset", offset)
                    .add("target", target);
        }

        @Override
        public final Vector3d getRefValue() {
            return offset;
        }

        @Override
        public final Ref createRef(String id) {
            return new Ref(id, this);
        }

        public static final class Ref extends Effect.Ref<Locatable, Vector3d> {

            private Ref(String id, Locatable component) {
                super(id, component);
            }

            @Override
            public final Vector3d deserializeValue(ConfigurationNode node) {
                return Serializers.deserializeVector3d(node);
            }

        }

    }

}