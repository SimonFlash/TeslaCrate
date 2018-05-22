package com.mcsimonflash.sponge.teslacrate.api.component;

import org.spongepowered.api.entity.living.player.Player;

public abstract class Prize<T extends Prize<T, V>, V> extends Component<T> {

    private final Type<T, V, ?, ?> type;

    protected Prize(Builder<T, V, ?> builder) {
        super(builder);
        type = builder.type;
    }

    public abstract void give(Player player, V value);

    public final Type<T, V, ?, ?> getType() {
        return type;
    }

    protected static abstract class Builder<T extends Prize<T, V>, V, B extends Builder<T, V, B>> extends Component.Builder<T, B> {

        private final Type<T, V, B, ?> type;

        protected Builder(String id, Type<T, V, B, ?> type) {
            super(id);
            this.type = type;
        }

    }

}
