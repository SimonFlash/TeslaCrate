package com.mcsimonflash.sponge.teslacrate.api.component;

import org.spongepowered.api.entity.living.player.User;

public abstract class Prize<T extends Prize<T, V>, V> extends Component<T, V> {

    protected Prize(String id) {
        super(id);
    }

    public abstract boolean give(User user, V value);

}