package com.mcsimonflash.sponge.teslacrate.component.opener;

import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public abstract class Opener {

    public abstract void open(Player player, Location<World> location, Crate<?> crate);

}