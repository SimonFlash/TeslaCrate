package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.Crate;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.concurrent.TimeUnit;

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
        if (crate.getParticle() != Effects.Particle.NONE) {
            particles = Task.builder()
                    .execute(new Effects.Particle.Runner(crate.getParticle(), location))
                    .async()
                    .interval(20, TimeUnit.MILLISECONDS)
                    .submit(TeslaCrate.getTesla().Container);
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

}