package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.collect.Sets;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import com.mcsimonflash.sponge.teslacrate.component.Particle;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Registration {

    private Location<World> location;
    private Crate crate;
    private Task particles;

    public Registration(Location<World> location, Crate crate) {
        this.location = location;
        this.crate = crate;
    }

    public void startParticles() {
        stopParticles();
        if (!crate.getParticles().isEmpty()) {
            List<Consumer<Task>> runners = crate.getParticles().stream().map(p -> new Runner(p, location)).collect(Collectors.toList());
            particles = Task.builder()
                    .execute(runners.size() == 1 ? runners.get(0) : t -> runners.forEach(r -> r.accept(t)))
                    .async()
                    .interval(Config.particleRefresh, TimeUnit.MILLISECONDS)
                    .submit(TeslaCrate.get().getContainer());
        }
    }

    public void stopParticles() {
        if (particles != null) {
            particles.cancel();
        }
    }

    public Location<World> getLocation() {
        return location;
    }

    public Crate getCrate() {
        return crate;
    }

    public static class Runner implements Consumer<Task> {

        private final Particle particle;
        private final Location<World> location;
        private final Set<Vector3f> positions = Sets.newHashSet();
        private final float increment;
        private float radians;

        public Runner(Particle particle, Location<World> location) {
            this.particle = particle;
            this.location = location;
            this.increment = particle.getSpeed() * AnimUtils.shift(particle.getPrecision());
            this.radians = particle.getShift();
            if (!particle.isAnimated()) {
                for (float radians : AnimUtils.shift(0, AnimUtils.shift(particle.getPrecision()))) {
                    for (Vector3f vec : particle.getPath().getPositions(radians)) {
                        positions.add(vec.mul(particle.getScale()).add(particle.getOffset()));
                    }
                }
            }
        }

        @Override
        public void accept(Task task) {
            ParticleEffect effect = particle.getEffect(radians);
            if (particle.isAnimated()) {
                for (Vector3f vec : particle.getPath().getPositions(radians)) {
                    AnimUtils.spawn(location, effect, vec.mul(particle.getScale()).add(particle.getOffset()));
                }
            } else {
                positions.forEach(p -> AnimUtils.spawn(location, effect, p));
            }
            radians += increment;
        }

    }

}