package com.mcsimonflash.sponge.teslacrate.component.opener;

import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class StandardOpener extends Opener {

    public static final StandardOpener INSTANCE = new StandardOpener();

    @Override
    public final void open(Player player, Location<World> location, Crate crate) {
        crate.give(player, location, crate.selectReward(player));
    }

}