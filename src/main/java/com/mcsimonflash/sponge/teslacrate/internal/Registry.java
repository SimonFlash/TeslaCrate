package com.mcsimonflash.sponge.teslacrate.internal;

import com.mcsimonflash.sponge.teslacrate.api.component.Component;
import com.mcsimonflash.sponge.teslacrate.api.component.Crate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Key;
import com.mcsimonflash.sponge.teslacrate.api.component.Prize;
import com.mcsimonflash.sponge.teslacrate.api.component.Reward;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslalibs.registry.RegistryService;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;

public final class Registry<T extends Component> {

    public static final Registry<Crate> CRATES = new Registry<>();
    public static final Registry<Effect> EFFECTS = new Registry<>();
    public static final Registry<Key> KEYS = new Registry<>();
    public static final Registry<Prize> PRIZES = new Registry<>();
    public static final Registry<Reward> REWARDS = new Registry<>();

    private final com.mcsimonflash.sponge.teslalibs.registry.Registry<T> componentRegistry = com.mcsimonflash.sponge.teslalibs.registry.Registry.of();
    private final com.mcsimonflash.sponge.teslalibs.registry.Registry<Type<? extends T, ?>> typeRegistry = com.mcsimonflash.sponge.teslalibs.registry.Registry.of();
    private final RegistryService<T> components = RegistryService.of(componentRegistry);
    private final RegistryService<Type<? extends T, ?>> types = RegistryService.of(typeRegistry);

    public final Optional<T> get(String id) {
        return components.getValue(id);
    }

    public final Optional<Type<? extends T, ?>> getType(String id) {
        return types.getValue(id);
    }

    public final RegistryService<T> getComponents() {
        return components;
    }

    public final RegistryService<Type<? extends T, ?>> getTypes() {
        return types;
    }

    public final boolean register(T component, PluginContainer container) {
        return componentRegistry.register(component.getId(), component, container);
    }

    public final boolean registerType(Type<? extends T, ?> type) {
        return typeRegistry.register(type.getName(), type, type.getContainer());
    }

    static void clear() {
        CRATES.componentRegistry.clear();
        EFFECTS.componentRegistry.clear();
        KEYS.componentRegistry.clear();
        PRIZES.componentRegistry.clear();
        REWARDS.componentRegistry.clear();
    }

}
