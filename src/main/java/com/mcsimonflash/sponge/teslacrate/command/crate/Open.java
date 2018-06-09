package com.mcsimonflash.sponge.teslacrate.command.crate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.crate.Crate;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.*;

import static com.mcsimonflash.sponge.teslacrate.command.CmdUtils.*;

@Aliases({"open"})
@Permission("teslacrate.command.crate.open.base")
public final class Open extends Command {

    @Inject
    private Open(Settings settings) {
        super(settings.usage(usage("teslacrate crate open ", "Opens a crate for a player.", PLAYER_ARG, CRATE_ARG, OPT_LOCATION_ARG)).elements(PLAYER_ELEM, CRATE_ELEM, OPT_LOCATION_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player player = args.<Player>getOne("player").get();
        Crate crate = args.<Crate>getOne("crate").get();
        Location<World> location = args.<Location<World>>getOne("location").orElse(player.getLocation());
        crate.open(player, location);
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.crate.open.success", "player", player.getName(), "crate", crate.getId(), "location", location.getExtent().getName() + location.getPosition()));
        return CommandResult.success();
    }

}
