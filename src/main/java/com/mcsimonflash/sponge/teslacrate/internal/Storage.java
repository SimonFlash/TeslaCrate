package com.mcsimonflash.sponge.teslacrate.internal;

import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.component.*;
import org.spongepowered.api.world.Location;

import java.util.Map;

public class Storage {

    public static final Map<Location, Registration> registry = Maps.newHashMap();
    public static final Map<String, Crate> crates = Maps.newHashMap();
    public static final Map<String, Key> keys = Maps.newHashMap();
    public static final Map<String, Reward> rewards = Maps.newHashMap();
    public static final Map<String, Command> commands = Maps.newHashMap();
    public static final Map<String, Item> items = Maps.newHashMap();
    public static final Map<String, Particle> particles = Maps.newHashMap();

}