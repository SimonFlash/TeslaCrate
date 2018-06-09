package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.component.Referenceable;
import com.mcsimonflash.sponge.teslacrate.component.Type;
import com.mcsimonflash.sponge.teslacrate.component.crate.Crate;
import com.mcsimonflash.sponge.teslacrate.component.effect.Effect;
import com.mcsimonflash.sponge.teslacrate.component.key.Key;
import com.mcsimonflash.sponge.teslacrate.component.prize.Prize;
import com.mcsimonflash.sponge.teslacrate.component.reward.Reward;
import com.mcsimonflash.sponge.teslalibs.registry.RegistryService;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Tuple;

import java.util.Map;
import java.util.Optional;

public final class Registry<T extends Referenceable<? extends T, ?>> {

    public static final Registry<Crate<?, ?>> CRATES = new Registry<>();
    public static final Registry<Effect<?, ?>> EFFECTS = new Registry<>();
    public static final Registry<Key<?>> KEYS = new Registry<>();
    public static final Registry<Prize<?, ?>> PRIZES = new Registry<>();
    public static final Registry<Reward<?>> REWARDS = new Registry<>();

    private final com.mcsimonflash.sponge.teslalibs.registry.Registry<T> registry = com.mcsimonflash.sponge.teslalibs.registry.Registry.of();
    private final RegistryService<T> components = RegistryService.of(registry);
    private final RegistryService<Type<? extends T, ?, ?, ?>> types = RegistryService.of(com.mcsimonflash.sponge.teslalibs.registry.Registry.of());

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
        CRATES.registry.clear();
        EFFECTS.registry.clear();
        KEYS.registry.clear();
        PRIZES.registry.clear();
        REWARDS.registry.clear();
    }

}
