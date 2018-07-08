package com.mcsimonflash.sponge.teslacrate.component;

import com.mcsimonflash.sponge.teslacrate.api.component.Reward;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;

public final class StandardReward extends Reward {

    public static final Type<StandardReward, Double> TYPE = Type.create("Standard", StandardReward::new);

    private StandardReward(String name) {
        super(name);
    }

}
