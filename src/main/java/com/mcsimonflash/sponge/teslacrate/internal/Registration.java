package com.mcsimonflash.sponge.teslacrate.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.component.effect.ParticleEffect;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

public final class Registration {

    private final String id;
    private final Location<World> location;
    private final Crate crate;
    private final List<Task> effects = Lists.newArrayList();

    public Registration(String id, Location<World> location, Crate crate) {
        this.id = id;
        this.location = location;
        this.crate = crate;
    }

    public final String getId() {
        return id;
    }

    public final Location<World> getLocation() {
        return location;
    }

    public final Crate getCrate() {
        return crate;
    }

    public final void startEffects() {
        stopEffects();
        crate.getEffects().getOrDefault(Effect.Trigger.PASSIVE, ImmutableList.of()).stream()
                .filter(e -> e.getComponent() instanceof ParticleEffect)
                .forEach(e -> effects.add(((ParticleEffect) e.getComponent()).start(location)));
    }

    public final void stopEffects() {
        effects.forEach(Task::cancel);
        effects.clear();
    }

}
