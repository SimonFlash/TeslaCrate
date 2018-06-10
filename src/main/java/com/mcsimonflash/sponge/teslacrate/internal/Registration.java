package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.component.crate.Crate;
import org.spongepowered.api.world.*;

public final class Registration {

    private final String id;
    private final Location<World> location;
    private final Crate crate;

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

}
