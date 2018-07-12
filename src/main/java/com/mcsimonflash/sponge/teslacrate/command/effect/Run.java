package com.mcsimonflash.sponge.teslacrate.command.effect;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
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

@Aliases({"run"})
@Permission("teslacrate.command.effect.run.base")
public final class Run extends Command {

    @Inject
    private Run(Settings settings) {
        super(settings.usage(CmdUtils.usage("/teslacrate effect run ", "Runs an effect for a player", CmdUtils.PLAYER_ARG, CmdUtils.EFFECT_ARG, CmdUtils.LOCATION_ARG))
                .elements(CmdUtils.PLAYER_ELEM, CmdUtils.EFFECT_ELEM, CmdUtils.LOCATION_ELEM));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player player = args.<Player>getOne("player").get();
        Effect effect = args.<Effect>getOne("effect").get();
        Location<World> location = args.<Location<World>>getOne("location").orElse(player.getLocation());
        effect.run(player, location, effect.getRefValue());
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.effect.run.success", "player", player.getName(), "effect", effect.getId(), "location", location.getExtent().getName() + location.getPosition()));
        return CommandResult.success();
    }

}
