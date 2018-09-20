package com.mcsimonflash.sponge.teslacrate.api.component;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.component.reward.StandardReward;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;

public abstract class Prize<V> extends Referenceable<V> {

    protected Prize(String id) {
        super(id);
    }

    public abstract boolean give(User user, V value);

    @Override
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        if (!node.getNode("reward").isVirtual()) {
            Reward reward = StandardReward.TYPE.create(getId());
            reward.addPrize(createRef(getId()));
            reward.setName(getName());
            reward.setDescription(getDescription());
            reward.setDisplayItem(getDisplayItem());
            reward.setAnnounce(node.getNode("reward", "announce").getBoolean(true));
            reward.setRepeatable(node.getNode("reward", "repeatable").getBoolean(true));
            reward.setWeight(node.getNode("reward", "weight").getDouble(0.0));
            Registry.REWARDS.register(reward, Sponge.getCauseStackManager().getCurrentCause().first(PluginContainer.class).orElse(TeslaCrate.get().getContainer()));
        }
    }

    @Override
    public abstract Ref<? extends Prize<V>, V> createRef(String id);

    @Override
    protected ItemStack.Builder createDisplayItem(V value) {
        return Utils.createItem(ItemTypes.NETHER_STAR, getName(), Lists.newArrayList(getDescription()));
    }

    public abstract static class Ref<T extends Prize<V>, V> extends Reference<T, V> {

        protected Ref(String id, T component) {
            super(id, component);
        }

        public final void give(User user) {
            getComponent().give(user, getValue());
        }

    }

}