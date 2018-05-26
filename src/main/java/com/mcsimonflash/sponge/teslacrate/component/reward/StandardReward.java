package com.mcsimonflash.sponge.teslacrate.component.reward;

import com.google.common.collect.*;
import com.mcsimonflash.sponge.teslacrate.api.component.*;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Set;

public final class StandardReward extends Reward<StandardReward> {

    public static final Type<StandardReward, Double, Builder, RefBuilder> TYPE = Type.create("Standard", Builder::new, RefBuilder::new).build();

    private final ImmutableSet<Reference<? extends Prize, ?>> prizes;

    private StandardReward(Builder builder) {
        super(builder);
        prizes = ImmutableSet.copyOf(builder.prizes);
    }

    @Override
    public final void give(Player player) {
        prizes.forEach(r -> r.getComponent().give(player, r.getValue()));
    }

    public final ImmutableSet<Reference<? extends Prize, ?>> getPrizes() {
        return prizes;
    }

    public static final class Builder extends Reward.Builder<StandardReward, Builder> {

        private final Set<Reference<? extends Prize, ?>> prizes = Sets.newHashSet();

        private Builder(String id) {
            super(id, TYPE);
        }

        public final Builder prize(Reference<? extends Prize, ?> prize) {
            prizes.add(prize);
            return this;
        }

        @Override
        public final StandardReward build() {
            return new StandardReward(this);
        }

    }

    public static final class RefBuilder extends Reference.Builder<StandardReward, Double, RefBuilder> {

        private RefBuilder(String id, StandardReward component) {
            super(id, component);
        }

        @Override
        public final RefBuilder deserialize(ConfigurationNode node) throws ConfigurationNodeException {
            return super.deserialize(node).value(node.getDouble(component.getWeight()));
        }

    }

}
