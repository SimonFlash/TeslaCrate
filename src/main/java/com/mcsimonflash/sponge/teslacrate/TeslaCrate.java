package com.mcsimonflash.sponge.teslacrate;

import com.mcsimonflash.sponge.teslacore.tesla.Tesla;
import com.mcsimonflash.sponge.teslacrate.command.Base;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Interact;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;

@Plugin(id = "teslacrate", name = "TeslaCrate", version = "2.1.1", dependencies = @Dependency(id = "teslacore"), url = "https://forums.spongepowered.org/t/teslacrate-key-shockingly-powerful-crates-api-5-6-and-7/19116", authors = "Simon_Flash")
public class TeslaCrate extends Tesla {

    private static TeslaCrate instance;
    public static final Text PREFIX = Utils.toText("&8[&eTesla&6Crate&8] &7");

    @Inject
    public TeslaCrate(PluginContainer container) {
        super(container);
        instance = this;
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent event) {
        getCommands().register(Base.class);
        Sponge.getEventManager().registerListeners(getContainer(), new Interact());
    }

    @Listener
    public void onStart(GameStartingServerEvent event) {
        Config.load();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        Config.load();
    }

    public static Text getMessage(CommandSource src, String key, Object... args) {
        return PREFIX.concat(get().getMessages().get(key, src.getLocale()).args(args).toText());
    }

    public static void sendMessage(CommandSource src, String key, Object... args) {
        src.sendMessage(getMessage(src, key, args));
    }

    public static TeslaCrate get() {
        return instance;
    }

}