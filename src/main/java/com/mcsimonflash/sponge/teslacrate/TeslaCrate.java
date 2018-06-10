package com.mcsimonflash.sponge.teslacrate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacore.tesla.Tesla;
import com.mcsimonflash.sponge.teslacrate.command.Base;
import com.mcsimonflash.sponge.teslacrate.component.crate.StandardCrate;
import com.mcsimonflash.sponge.teslacrate.component.key.PhysicalKey;
import com.mcsimonflash.sponge.teslacrate.component.prize.*;
import com.mcsimonflash.sponge.teslacrate.component.reward.StandardReward;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.*;
import org.spongepowered.api.text.Text;

@Plugin(id = "teslacrate", name = "TeslaCrate", version = "3.0.0-pr1", dependencies = @Dependency(id = "teslacore"), url = "https://ore.spongepowered.org/Simon_Flash/TeslaCrate", authors = "Simon_Flash")
public class TeslaCrate extends Tesla {

    private static TeslaCrate instance;

    @Inject
    private TeslaCrate(PluginContainer container) {
        super(container);
        instance = this;
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        getCommands().register(Base.class);
        Registry.CRATES.registerType(StandardCrate.TYPE, getContainer());
        //Registry.EFFECTS.registerType(ParticleEffect.TYPE, getContainer());
        //Registry.EFFECTS.registerType(SoundEffect.TYPE, getContainer());
        Registry.KEYS.registerType(PhysicalKey.TYPE, getContainer());
        //Registry.KEYS.registerType(VirtualKey.TYPE, getContainer());
        Registry.PRIZES.registerType(CommandPrize.TYPE, getContainer());
        Registry.PRIZES.registerType(ItemPrize.TYPE, getContainer());
        Registry.REWARDS.registerType(StandardReward.TYPE, getContainer());
        Sponge.getEventManager().registerListeners(getContainer(), new Listeners());
    }

    @Listener
    public void onStarting(GameStartingServerEvent event) {
        Config.load();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        Config.load();
    }

    public static TeslaCrate get() {
        return instance;
    }

    public static Text getMessage(CommandSource src, String key, Object... args) {
        return instance.getPrefix().concat(instance.getMessages().get(key, src.getLocale()).args((Object[]) args).toText());
    }

}
