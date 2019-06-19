package com.mcsimonflash.sponge.teslacrate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacore.tesla.Tesla;
import com.mcsimonflash.sponge.teslacrate.command.Base;
import com.mcsimonflash.sponge.teslacrate.component.crate.StandardCrate;
import com.mcsimonflash.sponge.teslacrate.component.effect.FireworkEffect;
import com.mcsimonflash.sponge.teslacrate.component.effect.ParticleEffect;
import com.mcsimonflash.sponge.teslacrate.component.effect.PotionEffect;
import com.mcsimonflash.sponge.teslacrate.component.effect.SoundEffect;
import com.mcsimonflash.sponge.teslacrate.component.key.CooldownKey;
import com.mcsimonflash.sponge.teslacrate.component.key.MoneyKey;
import com.mcsimonflash.sponge.teslacrate.component.key.PhysicalKey;
import com.mcsimonflash.sponge.teslacrate.component.key.VirtualKey;
import com.mcsimonflash.sponge.teslacrate.component.prize.CommandPrize;
import com.mcsimonflash.sponge.teslacrate.component.prize.ItemPrize;
import com.mcsimonflash.sponge.teslacrate.component.prize.MoneyPrize;
import com.mcsimonflash.sponge.teslacrate.component.reward.StandardReward;
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

@Plugin(id = "teslacrate", name = "TeslaCrate", version = "3.0.0-pr5", dependencies = @Dependency(id = "teslacore"), url = "https://ore.spongepowered.org/Simon_Flash/TeslaCrate", authors = "Simon_Flash")
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
        Registry.EFFECTS.registerType(FireworkEffect.TYPE);
        Registry.EFFECTS.registerType(ParticleEffect.TYPE);
        Registry.EFFECTS.registerType(PotionEffect.TYPE);
        Registry.EFFECTS.registerType(SoundEffect.TYPE);
        Registry.KEYS.registerType(CooldownKey.TYPE);
        Registry.KEYS.registerType(MoneyKey.TYPE);
        Registry.KEYS.registerType(PhysicalKey.TYPE);
        Registry.KEYS.registerType(VirtualKey.TYPE);
        Registry.PRIZES.registerType(CommandPrize.TYPE);
        Registry.PRIZES.registerType(ItemPrize.TYPE);
        Registry.PRIZES.registerType(MoneyPrize.TYPE);
        Registry.REWARDS.registerType(StandardReward.TYPE);
        getCommands().register(Base.class);
        Sponge.getCommandManager().register(getContainer(), getCommands().getInstance(com.mcsimonflash.sponge.teslacrate.command.crate.List.class).getSpec(), "crates");
        Sponge.getCommandManager().register(getContainer(), getCommands().getInstance(com.mcsimonflash.sponge.teslacrate.command.key.List.class).getSpec(), "keys");
        Sponge.getEventManager().registerListeners(getContainer(), new Listeners());
    }

    @Listener
    public final void onStarting(GameStartingServerEvent event) {
        Config.load();
    }

    @Listener
    public final void onReload(GameReloadEvent event) {
        getMessages().reload();
        Config.load();
    }

    public static TeslaCrate get() {
        return instance;
    }

    public static Text getMessage(CommandSource src, String key, Object... args) {
        return instance.getPrefix().concat(instance.getMessages().get(key, src.getLocale()).args((Object[]) args).toText());
    }

}
