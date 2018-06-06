package com.mcsimonflash.sponge.teslacrate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacore.tesla.Tesla;
import com.mcsimonflash.sponge.teslacrate.component.key.*;
import com.mcsimonflash.sponge.teslacrate.component.prize.*;
import com.mcsimonflash.sponge.teslacrate.component.reward.StandardReward;
import com.mcsimonflash.sponge.teslacrate.internal.*;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.*;

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
        Registry.KEYS.registerType(PhysicalKey.TYPE, getContainer());
        Registry.KEYS.registerType(VirtualKey.TYPE, getContainer());
        Registry.PRIZES.registerType(CommandPrize.TYPE, getContainer());
        Registry.PRIZES.registerType(ItemPrize.TYPE, getContainer());
        Registry.REWARDS.registerType(StandardReward.TYPE, getContainer());
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent event) {
        Config.load();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        Config.load();
    }

    public static TeslaCrate get() {
        return instance;
    }

}
