package com.mcsimonflash.sponge.teslacrate.component;

import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Reward;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;

public final class StandardReward extends Reward {

    public static final Type<StandardReward, Double> TYPE = new Type<>("Standard", StandardReward::new, n -> false, TeslaCrate.get().getContainer());

    private StandardReward(String id) {
        super(id);
    }

}
