package com.mcsimonflash.sponge.teslacrate.component.key;

import com.mcsimonflash.sponge.teslacrate.component.Type;
import org.spongepowered.api.entity.living.player.User;

public final class VirtualKey extends Key<VirtualKey> {

    public static final Type<VirtualKey, Integer, Builder, RefBuilder> TYPE = Type.create("Virtual", Builder::new, RefBuilder::new).build();

    private VirtualKey(Builder builder) {
        super(builder);
    }

    @Override
    public int get(User user) {
        return 0;
    }

    @Override
    public boolean check(User user, int quantity) {
        return false;
    }

    @Override
    public boolean give(User user, int quantity) {
        return false;
    }

    @Override
    public boolean take(User user, int quantity) {
        return false;
    }


    public static final class Builder extends Key.Builder<VirtualKey, Builder> {

        private Builder(String id) {
            super(id, TYPE);
        }

        @Override
        public final VirtualKey build() {
            return new VirtualKey(this);
        }

    }

    public static final class RefBuilder extends Key.RefBuilder<VirtualKey, RefBuilder> {

        private RefBuilder(String id, VirtualKey component) {
            super(id, component);
        }

    }

}
