package com.mcsimonflash.sponge.teslacrate.component.reward;

import com.mcsimonflash.sponge.teslacrate.component.Type;
import org.spongepowered.api.entity.living.player.*;

public final class StandardReward extends Reward<StandardReward> {

    public static final Type<StandardReward, Double, Builder, RefBuilder> TYPE = Type.create("Standard", Builder::new, RefBuilder::new).build();

    private StandardReward(Builder builder) {
        super(builder);
    }

    @Override
    public final void give(User user) {
        getPrizes().forEach(r -> r.getComponent().give(user, r.getValue()));
    }

    public static final class Builder extends Reward.Builder<StandardReward, Builder> {

        private Builder(String id) {
            super(id, TYPE);
        }

        @Override
        public final StandardReward build() {
            return new StandardReward(this);
        }

    }

    public static final class RefBuilder extends Reward.RefBuilder<StandardReward, RefBuilder> {

        private RefBuilder(String id, StandardReward component) {
            super(id, component);
        }

    }

}
