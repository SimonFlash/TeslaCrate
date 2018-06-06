package com.mcsimonflash.sponge.teslacrate.component.crate;

import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslacrate.component.reward.Reward;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.*;

public class StandardCrate extends Crate<StandardCrate, Object> {

    public static final Type<StandardCrate, Object, Builder, RefBuilder> TYPE = Type.create("Standard", Builder::new, RefBuilder::new).build();

    private StandardCrate(Builder builder) {
        super(builder);
    }

    @Override
    public final void give(Player player, Location<World> location) {
        double rand = Math.random() * getRewards().stream().mapToDouble(Reference::getValue).sum();
        for (Reference<? extends Reward, Double> reward : getRewards()) {
            if ((rand -= reward.getValue()) <= 0) {
                reward.getComponent().give(player);
                return;
            }
        }
    }

    public static final class Builder extends Crate.Builder<StandardCrate, Object, Builder> {

        private Builder(String id) {
            super(id, TYPE);
        }

        @Override
        public final StandardCrate build() {
            return new StandardCrate(this);
        }

    }

    public static final class RefBuilder extends Reference.Builder<StandardCrate, Object, RefBuilder> {

        private RefBuilder(String id, StandardCrate component) {
            super(id, component);
        }

    }

}
