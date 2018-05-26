package com.mcsimonflash.sponge.teslacrate.api.configuration;

import com.google.common.collect.*;
import com.mcsimonflash.sponge.teslacrate.api.component.*;
import com.mcsimonflash.sponge.teslacrate.api.registry.Registry;
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

import java.util.*;

public final class Serializers {

    public static <T extends Referenceable<? extends T, ?>> T deserialize(String id, ConfigurationNode node, Registry<T> registry) throws ConfigurationException {
        return registry.getType(node.getNode("type").getString("")).orElseThrow(() -> new ConfigurationException(node.getNode("type"), "No type found for %s.", node.getNode("type").getString("undefined"))).create(id).deserialize(node).build();
    }

    public static <T extends Referenceable<? extends T, ?>> Reference<? extends T, ?> deserializeRef(String id, ConfigurationNode node, Registry<T> registry, PluginContainer container) throws ConfigurationException {
        T component;
        if (node.hasMapChildren()) {
            component = deserialize(id, node, registry);
            registry.register(component, container);
        } else {
            String key = (String) node.getKey();
            String refined = key.contains("/") ? key.substring(0, key.lastIndexOf('/')) : key;
            component = registry.get(refined).orElseThrow(() -> new ConfigurationException(node, "No component registered with id " + refined));
        }
        return component.createRef(id).deserialize(node).build();
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

}
