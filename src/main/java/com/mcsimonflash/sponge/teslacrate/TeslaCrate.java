package com.mcsimonflash.sponge.teslacrate;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.teslacore.tesla.Tesla;
import org.spongepowered.api.plugin.*;

@Plugin(id = "teslacrate", name = "TeslaCrate", version = "3.0.0-pr1", dependencies = @Dependency(id = "teslacore"), url = "https://ore.spongepowered.org/Simon_Flash/TeslaCrate", authors = "Simon_Flash")
public class TeslaCrate extends Tesla {

    private static TeslaCrate instance;

    @Inject
    private TeslaCrate(PluginContainer container) {
        super(container);
        instance = this;
    }

    public static TeslaCrate get() {
        return instance;
    }

}
