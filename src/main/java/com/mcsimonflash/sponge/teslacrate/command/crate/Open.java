package com.mcsimonflash.sponge.teslacrate.command.crate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.command.CmdUtils;
import com.mcsimonflash.sponge.teslalibs.command.Aliases;
import com.mcsimonflash.sponge.teslalibs.command.Command;
import com.mcsimonflash.sponge.teslalibs.command.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Aliases("open")
@Permission("teslacrate.command.crate.open.base")
public final class Open extends Command {

    @Inject
    private Open(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate crate open ", "Opens a crate for a player.", CmdUtils.PLAYER_ARG, CmdUtils.CRATE_ARG, CmdUtils.OPT_LOCATION_ARG))
                .elements(CmdUtils.PLAYER_ELEM, CmdUtils.CRATE_ELEM, CmdUtils.OPT_LOCATION_ELEM));
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
