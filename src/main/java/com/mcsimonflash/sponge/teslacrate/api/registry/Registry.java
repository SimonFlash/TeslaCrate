package com.mcsimonflash.sponge.teslacrate.api.registry;

import com.mcsimonflash.sponge.teslacore.registry.RegistryService;
import com.mcsimonflash.sponge.teslacrate.api.component.*;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;

public final class Registry<T extends Referenceable<? extends T, ?>> {

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

    public final boolean register(T component, PluginContainer container) {
        return components.register(component.getId(), component, container);
    }

    public final boolean registerType(Type<? extends T, ?, ?, ?> type, PluginContainer container) {
        return types.register(type.getId(), type, container);
    }

}
