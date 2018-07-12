package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Referenceable;
import com.mcsimonflash.sponge.teslacrate.api.component.Reward;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslalibs.registry.RegistryService;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;

public final class Registry<T extends Referenceable<?>> {

    public static final Registry<Crate> CRATES = new Registry<>();
    public static final Registry<Effect<?>> EFFECTS = new Registry<>();
    public static final Registry<Key> KEYS = new Registry<>();
    public static final Registry<Prize<?>> PRIZES = new Registry<>();
    public static final Registry<Reward> REWARDS = new Registry<>();

    private final com.mcsimonflash.sponge.teslalibs.registry.Registry<T> components = com.mcsimonflash.sponge.teslalibs.registry.Registry.of();
    private final com.mcsimonflash.sponge.teslalibs.registry.Registry<Type<? extends T, ?>> types = com.mcsimonflash.sponge.teslalibs.registry.Registry.of();

    public final Optional<T> get(String id) {
        return components.getValue(id);
    }

    public final Optional<Type<? extends T, ?>> getType(String id) {
        return types.getValue(id);
    }

    public final RegistryService<T> getComponents() {
        return RegistryService.of(components);
    }

    public final RegistryService<Type<? extends T, ?>> getTypes() {
        return RegistryService.of(types);
    }

    public final boolean register(T component, PluginContainer container) {
        return components.register(component.getId(), component, container);
    }

    public final boolean registerType(Type<? extends T, ?> type) {
        return types.register(type.getName(), type, type.getContainer());
    }

    static void clear() {
        CRATES.components.clear();
        EFFECTS.components.clear();
        KEYS.components.clear();
        PRIZES.components.clear();
        REWARDS.components.clear();
    }

}
