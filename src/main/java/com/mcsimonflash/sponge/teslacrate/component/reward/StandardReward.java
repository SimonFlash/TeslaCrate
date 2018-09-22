package com.mcsimonflash.sponge.teslacrate.component.reward;

import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Reference;
import com.mcsimonflash.sponge.teslacrate.api.component.Reward;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Registry;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.List;

public final class StandardReward extends Reward<StandardReward> {

    public static final Type<StandardReward, Double> TYPE = new Type<>("Standard", StandardReward::new, TeslaCrate.get().getContainer());

    private final List<Reference<? extends Prize, ?>> prizes = Lists.newArrayList();

    private StandardReward(String id) {
        super(id);
    }

    @Override
    public final void give(User user) {
        prizes.forEach(p -> p.getComponent().give(user, p.getValue()));
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        node.getNode("prizes").getChildrenMap().values().forEach(n -> {
            String id = getId() + ":prize:" + n.getKey();
            Prize<?, ?> prize = Serializers.getComponent(id, n, Registry.PRIZES, TeslaCrate.get().getContainer());
            prizes.add(prize.createReference(id, n));
        });
        super.deserialize(node);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Double value) {
        return Utils.createItem(ItemTypes.WRITTEN_BOOK, getName()).build().createSnapshot();
    }

}
