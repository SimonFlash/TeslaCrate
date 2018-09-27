package com.mcsimonflash.sponge.teslacrate.component.reward;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Reference;
import com.mcsimonflash.sponge.teslacrate.api.component.Reward;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.component.prize.CommandPrize;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

public final class PrizeReward extends Reward<PrizeReward> {

    public static final Type<PrizeReward, Double> TYPE = new Type<>("Prize", PrizeReward::new, n -> !n.getNode("prize").isVirtual(), TeslaCrate.get().getContainer());

    private static final Reference<CommandPrize, String> EMPTY = CommandPrize.TYPE.create("empty").createReference("", SimpleConfigurationNode.root());

    private Reference<? extends Prize, ?> prize;

    private PrizeReward(String id) {
        this(id, EMPTY);
    }

    public PrizeReward(String id, Reference<? extends Prize, ?> prize) {
        super(id);
        this.prize = prize;
    }

    @Override
    public Text getName() {
        return prize.getComponent().getName();
    }

    @Override
    public Text getDescription() {
        return prize.getComponent().getDescription();
    }

    @Override
    public ItemStackSnapshot getDisplayItem() {
        return prize.getDisplayItem();
    }

    @Override
    public final void give(User user) {
        prize.getComponent().give(user, prize.getValue());
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Double value) {
        return prize.getDisplayItem();
    }

}
