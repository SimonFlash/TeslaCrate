package com.mcsimonflash.sponge.teslacrate.command.effect;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.effect.Effect;
import com.mcsimonflash.sponge.teslalibs.command.*;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.*;

@Aliases({"run"})
@Permission("teslacrate.command.effect.run.base")
public final class Run extends Command {

    @Inject
    private Run(Settings settings) {
        super(settings.usage(Text.of("/tc effect run")));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player player = args.<Player>getOne("player").get();
        Effect effect = args.<Effect>getOne("effect").get();
        Location<World> location = args.<Location<World>>getOne("location").orElse(player.getLocation());
        effect.run(player, location);
        src.sendMessage(TeslaCrate.getMessage(src, "teslacrate.command.effect.run.success", "player", player.getName(), "effect", effect.getId(), "location", location.getExtent().getName() + location.getPosition()));
        return CommandResult.success();
    }

}
