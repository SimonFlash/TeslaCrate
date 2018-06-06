package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacore.registry.RegistryService;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslacrate.component.crate.Crate;
import com.mcsimonflash.sponge.teslacrate.component.effects.Effect;
import com.mcsimonflash.sponge.teslacrate.component.key.Key;
import com.mcsimonflash.sponge.teslacrate.component.prize.Prize;
import com.mcsimonflash.sponge.teslacrate.component.reward.Reward;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Tuple;

import java.util.*;

public final class Registry<T extends Referenceable<? extends T, ?>> {

    public static final Registry<Crate<?, ?>> CRATES = new Registry<>();
    public static final Registry<Effect<?, ?>> EFFECTS = new Registry<>();
    public static final Registry<Key<?>> KEYS = new Registry<>();
    public static final Registry<Prize<?, ?>> PRIZES = new Registry<>();
    public static final Registry<Reward<?>> REWARDS = new Registry<>();

    private final RegistryService<T> components = RegistryService.of();
    private final RegistryService<Type<? extends T, ?, ?, ?>> types = RegistryService.of();

    public final Optional<T> get(String id) {
        return components.getValue(id);
    }

    public final Optional<Type<? extends T, ?, ?, ?>> getType(String id) {
        return types.getValue(id);
    }

    public final Map<String, Tuple<T, PluginContainer>> getComponents() {
        return components.getAll();
    }

    public final Map<String, Tuple<Type<? extends T, ?, ?, ?>, PluginContainer>> getTypes() {
        return types.getAll();
    }

    public final boolean register(T component, PluginContainer container) {
        return components.register(component.getId(), component, container);
    }

    public final boolean registerType(Type<? extends T, ?, ?, ?> type, PluginContainer container) {
        return types.register(type.getId(), type, container);
    }

    static void clear() {
        CRATES.components.clear();
        EFFECTS.components.clear();
        KEYS.components.clear();
        PRIZES.components.clear();
        REWARDS.components.clear();
    }

}
