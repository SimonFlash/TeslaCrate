package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.component.path.AnimationPath;
import com.mcsimonflash.sponge.teslacrate.component.path.CirclePath;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.Consumer;

public final class ParticleEffect extends Effect.Locatable<ParticleEffect> {

    public static final Type<ParticleEffect, Vector3d> TYPE = new Type<>("Particle", ParticleEffect::new, TeslaCrate.get().getContainer());

    private ParticleType type = ParticleTypes.REDSTONE_DUST;
    private Color color = Color.BLACK;
    private boolean rainbow = false;
    private AnimationPath path = new CirclePath();

    private ParticleEffect(String name) {
        super(name);
    }

    @Override
    public final void run(Location<World> location) {
        Task runner = start(location);
        Utils.createTask(t -> runner.cancel(), path.getInterval() * path.getPrecision(), 0, true);
    }

    public final Task start(Location<World> location) {
        float increment = path.getSpeed() * AnimUtils.shift(path.getPrecision());
        return Utils.createTask(new Consumer<Task>() {

            private float radians = path.getShift();

            @Override
            public void accept(Task task) {
                org.spongepowered.api.effect.particle.ParticleEffect effect = rainbow ? AnimUtils.particle(AnimUtils.rainbow(6 * radians / 7)) : org.spongepowered.api.effect.particle.ParticleEffect.builder()
                        .type(type).option(ParticleOptions.COLOR, color).build();
                for (Vector3f vec : path.getPositions(radians)) {
                    AnimUtils.spawn(location, effect, vec.mul(path.getScale()));
                }
                radians += increment;
            }

        }, 0, path.getInterval(), true);
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        if (node.getNode("particle").hasMapChildren()) {
            NodeUtils.ifAttached(node.getNode("particle", "type"), n -> type = Serializers.deserializeCatalogType(n, ParticleType.class));
            NodeUtils.ifAttached(node.getNode("particle", "color"), n -> color = Serializers.deserializeColor(n));
            rainbow = node.getNode("particle", "rainbow").getBoolean(false);
        } else {
            NodeUtils.ifAttached(node.getNode("particle"), n -> type = Serializers.deserializeCatalogType(n, ParticleType.class));
        }
        if (node.getNode("path").hasMapChildren()) {
            path = Serializers.deserializePath(node.getNode("path"));
        } else {
            NodeUtils.ifAttached(node.getNode("path"), n -> path = Serializers.deserializePathType(n));
        }
        super.deserialize(node);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Vector3d value) {
        return Utils.createItem(ItemTypes.REDSTONE, getName()).build().createSnapshot();
    }

}