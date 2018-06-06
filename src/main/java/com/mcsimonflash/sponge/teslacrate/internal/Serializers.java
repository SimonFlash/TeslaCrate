package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.*;
import com.google.common.collect.*;
import com.mcsimonflash.sponge.teslacrate.component.*;
import com.mcsimonflash.sponge.teslalibs.configuration.*;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.*;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.enchantment.*;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Serializers {

    public static <T extends Referenceable<? extends T, ?>> Type<? extends T, ?, ?, ?> getType(ConfigurationNode node, Registry<T> registry) throws ConfigurationException {
        ConfigurationNode typeNode = node.getNode("type");
        if (!typeNode.isVirtual()) {
            return registry.getType(typeNode.getString("")).orElseThrow(() -> new ConfigurationException(typeNode, "No type found for %s.", typeNode.getString("undefined")));
        }
        List<Type<? extends T, ?, ?, ?>> types = registry.getTypes().values().stream()
                .distinct()
                .map(Tuple::getFirst)
                .filter(t -> t.matches(node))
                .collect(Collectors.toList());
        if (types.size() == 1) {
            return types.get(0);
        }
        return registry.getType("Standard").orElseThrow(() -> new ConfigurationException(node, "TypeSense matched %s types and a Standard type does not exist.", types.size()));
    }

    public static <T extends Referenceable<? extends T, ?>> T getComponent(String id, ConfigurationNode node, Registry<T> registry, PluginContainer container) {
        T component;
        if (node.hasMapChildren()) {
            component = Serializers.<T>getType(node, registry).create(id).deserialize(node).build();
            registry.register(component, container);
        } else {
            String key = (String) node.getKey();
            String refined = key.contains("/") ? key.substring(0, key.lastIndexOf('/')) : key;
            component = registry.get(refined).orElseThrow(() -> new ConfigurationException(node, "No component identified by " + refined));
        }
        return component;
    }

    public static ItemStackSnapshot deserializeItem(ConfigurationNode node) throws ConfigurationException {
        String id = node.getNode("id").getString("undefined");
        ItemType type = Sponge.getRegistry().getType(ItemType.class, id).orElseThrow(() -> new ConfigurationException(node.getNode("id"), "No item found for id `%s`.", id));
        int quantity = node.getNode("quantity").getInt(1);
        if (quantity <= 0 || quantity > type.getMaxStackQuantity()) {
            throw new ConfigurationException(node.getNode("quantity"), "Quantity is out of bounds.");
        }
        DataContainer container = ItemStack.of(type, quantity).toContainer();
        NodeUtils.ifAttached(node.getNode("data"), n -> container.set(DataQuery.of("UnsafeDamage"), n.getInt(0)));
        NodeUtils.ifAttached(node.getNode("nbt"), n -> {
            Object value = n.getValue();
            if (value instanceof Map) {
                Map data = container.getMap(DataQuery.of("UnsafeData")).map(Map.class::cast).orElseGet(Maps::newHashMap);
                data.putAll((Map) value);
                container.set(DataQuery.of("UnsafeData"), data);
            } else {
                throw new ConfigurationException(n, "Expected a map of values for nbt, but instead received " + value.getClass().getSimpleName() + ".");
            }
        });
        ItemStack.Builder builder = ItemStack.builder().fromContainer(container);
        List<Enchantment> enchantments = Lists.newArrayList();
        for (ConfigurationNode child : node.getNode("enchantments").getChildrenMap().values()) {
            String name = ((String) child.getKey()).replace("-", "_");
            EnchantmentType enchantment = Sponge.getRegistry().getType(EnchantmentType.class, name.contains(":") ? name : "minecraft:" + name).orElseThrow(() -> new ConfigurationException(child, "No enchantment found for id %s.", name));
            enchantments.add(Enchantment.of(enchantment, child.getInt(0)));
        }
        if (!enchantments.isEmpty()) {
            builder.add(Keys.ITEM_ENCHANTMENTS, enchantments);
        }
        return builder.build().createSnapshot();
    }

    public static Text deserializeText(ConfigurationNode node) {
        return TextSerializers.FORMATTING_CODE.deserialize(node.getString(""));
    }

    public static Vector3i deserializeVector2i(ConfigurationNode node) throws ConfigurationException {
        Integer[] components = deserializeComponents(node, Integer::parseInt);
        return Vector3i.from(components[0], components[1], components[2]);
    }

    public static Vector3d deserializeVector3d(ConfigurationNode node) throws ConfigurationException {
        Double[] components = deserializeComponents(node, Double::parseDouble);
        return Vector3d.from(components[0], components[1], components[2]);
    }

    private static <T> T[] deserializeComponents(ConfigurationNode node, Function<String, T> parser) {
        T[] components;
        if (node.hasListChildren()) {
            components = (T[]) node.getChildrenList().stream().map(n -> n.getString("")).map(parser).toArray();
        } else {
            try {
                components = (T[]) Arrays.stream(node.getString("").split(",")).map(parser).toArray();
            } catch (NumberFormatException e) {
                throw new ConfigurationException(node, "Unable to retrieve a list of doubles.");
            }
        }
        if (components.length != 3) {
            throw new ConfigurationException(node, "Expected 3 vector components, received %s.", components.length);
        }
        return components;
    }

}
