package com.mcsimonflash.sponge.teslacrate;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacrate.commands.*;
import com.mcsimonflash.sponge.teslacrate.commands.display.ListCrates;
import com.mcsimonflash.sponge.teslacrate.commands.display.ListKeys;
import com.mcsimonflash.sponge.teslacrate.commands.keys.GiveKey;
import com.mcsimonflash.sponge.teslacrate.commands.locations.DeleteLoc;
import com.mcsimonflash.sponge.teslacrate.commands.locations.Lookup;
import com.mcsimonflash.sponge.teslacrate.commands.locations.SetLoc;
import com.mcsimonflash.sponge.teslacrate.managers.Config;
import com.mcsimonflash.sponge.teslacrate.managers.Storage;
import com.mcsimonflash.sponge.teslacrate.managers.Util;
import com.mcsimonflash.sponge.teslacrate.objects.crates.Crate;
import com.mcsimonflash.sponge.teslacrate.objects.crates.CrateLocation;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

@Plugin(id = "teslacrate", name = "TeslaCrate", version = "mc1.10.2-v1.1.1", authors = "Simon_Flash", description = "Shockingly powerful crates")
public class TeslaCrate {

    private static TeslaCrate plugin;
    public static TeslaCrate getPlugin() {
        return plugin;
    }

    private static URL wiki;
    public static URL getWiki() {
        return wiki;
    }

    private static URL discord;
    public static URL getDiscord() {
        return discord;
    }

    @Inject
    private Logger logger;
    public Logger getLogger() {
        return logger;
    }

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path directory;
    public Path getDirectory() {
        return directory;
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        plugin = this;
        logger.info("+=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=+");
        logger.info("|     TeslaCrate -- Version 1.1.1     |");
        logger.info("|      Developed By: Simon_Flash      |");
        logger.info("+=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=+");
        try {
            wiki = new URL("https://github.com/SimonFlash/TeslaCrate/wiki");
        } catch (MalformedURLException ignored) {
            getLogger().error("Unable to locate TeslaCrate Wiki!");
        }
        try {
            discord = new URL("https://discordapp.com/invite/4wayq37");
        } catch (MalformedURLException ignored) {
            getLogger().error("Unable to locate Support Discord!");
        }
        Util.initialize();
        CommandSpec DeleteLoc = CommandSpec.builder()
                .executor(new DeleteLoc())
                .description(Text.of("Deletes a crate from a location"))
                .permission("teslacrate.deleteloc.base")
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("world-name"))),
                        GenericArguments.optional(GenericArguments.vector3d(Text.of("crate-pos"))))
                .build();
        CommandSpec GiveKey = CommandSpec.builder()
                .executor(new GiveKey())
                .description(Text.of("Gives a user crate keys"))
                .permission("teslacrate.givekey.base")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("key-name"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("quantity"))))
                .build();
        CommandSpec ListCrates = CommandSpec.builder()
                .executor(new ListCrates())
                .description(Text.of("Displays a list of crates"))
                .permission("teslacrate.listcrates.base")
                .build();
        CommandSpec ListKeys = CommandSpec.builder()
                .executor(new ListKeys())
                .description(Text.of("Displays a list of user keys"))
                .permission("teslacrate.listkeys.base")
                .arguments(
                        GenericArguments.optional(GenericArguments.user(Text.of("opt-user"))))
                .build();
        CommandSpec Lookup = CommandSpec.builder()
                .executor(new Lookup())
                .description(Text.of("Returns crates matches flags"))
                .permission("teslacrate.lookup.base")
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("flags"))))
                .build();
        CommandSpec SetLoc = CommandSpec.builder()
                .executor(new SetLoc())
                .description(Text.of("Sets a crate to a location"))
                .permission("teslacrate.setloc.base")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("crate-name"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("world-name"))),
                        GenericArguments.optional(GenericArguments.vector3d(Text.of("crate-pos"))))
                .build();
        CommandSpec Base = CommandSpec.builder()
                .executor(new Base())
                .description(Text.of("Opens in-game documentation"))
                .permission("teslacrate.base")
                .child(DeleteLoc, "DeleteLoc", "DelLoc", "Del", "dl")
                .child(GiveKey, "GiveKey", "Give", "gk")
                .child(ListCrates, "ListCrates", "Crates", "lc")
                .child(ListKeys, "ListKeys", "Keys", "lk")
                .child(Lookup, "Lookup", "lu")
                .child(SetLoc, "SetLoc", "Set", "sl")
                .build();
        Sponge.getCommandManager().register(plugin, Base, Lists.newArrayList("TeslaCrate", "TCrate", "Crate", "tc"));
        Sponge.getCommandManager().register(plugin, ListCrates, Lists.newArrayList("ListCrates", "Crates"));
        Sponge.getCommandManager().register(plugin, ListKeys, Lists.newArrayList("ListKeys", "Keys"));
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        Util.initialize();
        TeslaCrate.getPlugin().getLogger().info("Configuration Reloaded");
    }

    @Listener
    public void onInteractBlockPrimary(InteractBlockEvent.Primary event, @First Player player) {
        event.getTargetBlock().getLocation().ifPresent(loc -> {
            Crate crate = Storage.crateRegistry.get(new CrateLocation(player.getWorld().getName(), loc.getBlockPosition()));
            if (crate != null) {
                event.setCancelled(true);
                if (Util.notSpamClick(player)) {
                    player.sendMessage(Config.teslaPrefix.concat(Util.toText("&7Woah! This location is registered as a &f" + crate.Name + " &7crate!")));
                }
            }
        });
    }

    @Listener
    public void onInteractBlockSecondary(InteractBlockEvent.Secondary event, @First Player player) {
        event.getTargetBlock().getLocation().ifPresent(loc -> Util.processInteract(event, loc, player));
    }

    @Listener
    public void onInteractEntityEventSecondary(InteractEntityEvent.Secondary event, @First Player player) {
        Util.processInteract(event, event.getTargetEntity().getLocation(), player);
    }
}
