package com.mcsimonflash.sponge.teslacrate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacore.tesla.Tesla;
import com.mcsimonflash.sponge.teslacrate.command.Base;
import com.mcsimonflash.sponge.teslacrate.component.CommandPrize;
import com.mcsimonflash.sponge.teslacrate.component.ItemPrize;
import com.mcsimonflash.sponge.teslacrate.component.ParticleEffect;
import com.mcsimonflash.sponge.teslacrate.component.PhysicalKey;
import com.mcsimonflash.sponge.teslacrate.component.SoundEffect;
import com.mcsimonflash.sponge.teslacrate.component.StandardCrate;
import com.mcsimonflash.sponge.teslacrate.component.StandardReward;
import com.mcsimonflash.sponge.teslacrate.component.VirtualKey;
import com.mcsimonflash.sponge.teslacrate.internal.Config;
import com.mcsimonflash.sponge.teslacrate.internal.Listeners;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

@Plugin(id = "teslacrate", name = "TeslaCrate", version = "3.0.0-pr2", dependencies = @Dependency(id = "teslacore"), url = "https://ore.spongepowered.org/Simon_Flash/TeslaCrate", authors = "Simon_Flash")
public final class TeslaCrate extends Tesla {

    private static TeslaCrate instance;

    @Inject
    private TeslaCrate(PluginContainer container) {
        super(container);
        instance = this;
    }

    @Listener
    public final void onInit(GameInitializationEvent event) {
        Registry.CRATES.registerType(StandardCrate.TYPE);
        Registry.EFFECTS.registerType(ParticleEffect.TYPE);
        Registry.EFFECTS.registerType(SoundEffect.TYPE);
        Registry.KEYS.registerType(PhysicalKey.TYPE);
        Registry.KEYS.registerType(VirtualKey.TYPE);
        Registry.PRIZES.registerType(CommandPrize.TYPE);
        Registry.PRIZES.registerType(ItemPrize.TYPE);
        Registry.REWARDS.registerType(StandardReward.TYPE);
        getCommands().register(Base.class);
        Sponge.getEventManager().registerListeners(getContainer(), new Listeners());
    }

    @Listener
    public final void onStarting(GameStartingServerEvent event) {
        Config.load();
    }

    @Listener
    public final void onReload(GameReloadEvent event) {
        Config.load();
    }

    public static TeslaCrate get() {
        return instance;
    }

    public static Text getMessage(CommandSource src, String key, Object... args) {
        return instance.getPrefix().concat(instance.getMessages().get(key, src.getLocale()).args((Object[]) args).toText());
    }

}
