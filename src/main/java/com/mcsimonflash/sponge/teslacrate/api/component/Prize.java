package com.mcsimonflash.sponge.teslacrate.api.component;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.reward.PrizeReward;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.plugin.PluginContainer;

public abstract class Prize<T extends Prize<T, V>, V> extends Component<T, V> {

    protected Prize(String id) {
        super(id);
    }

    public abstract boolean give(User user, V value);

    @Override
    public void deserialize(ConfigurationNode node) {
        NodeUtils.ifAttached(node.getNode("reward"), n -> Registry.REWARDS.register(new PrizeReward(getId(), new Reference<T, V>(getId(), (T) this, getValue())),
                Sponge.getCauseStackManager().getCurrentCause().first(PluginContainer.class).orElse(TeslaCrate.get().getContainer())));
        super.deserialize(node);
    }

}