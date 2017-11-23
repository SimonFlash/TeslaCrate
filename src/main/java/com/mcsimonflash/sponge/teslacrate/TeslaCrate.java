package com.mcsimonflash.sponge.teslacrate;

import com.mcsimonflash.sponge.teslacore.tesla.Tesla;
import com.mcsimonflash.sponge.teslacrate.command.Base;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;

@Plugin(id = "teslacrate", name = "TeslaCrate", version = "2.0.0-legacy", dependencies = @Dependency(id = "teslacore"), authors = "Simon_Flash")
public class TeslaCrate extends Tesla {

    private static TeslaCrate tesla;
    public static TeslaCrate getTesla() {
        return tesla;
    }

    @Inject
    public TeslaCrate(PluginContainer container) {
        super(container);
        tesla = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        Config.load();
        Sponge.getCommandManager().register(Container, Base.COMMAND, "teslacrate", "crate");
        Sponge.getEventManager().registerListeners(Container, new Interact());
    }

    @Listener
    public void onStart(GameStartingServerEvent event) {
        Config.loadLocations();
    }

    public static Text getMessage(CommandSource src, String key, Object... args) {
        return tesla.Prefix.concat(tesla.Messages.get(key, src.getLocale()).args(args).toText());
    }

    public static void sendMessage(CommandSource src, String key, Object... args) {
        src.sendMessage(getMessage(src, key, args));
    }

}