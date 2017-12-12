package com.mcsimonflash.sponge.teslacrate.internal;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.mcsimonflash.sponge.teslacrate.component.Component;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

public class Serializers {

    public static <T extends Component> T deserializeChild(ConfigurationNode node, Map<String, T> storage, Supplier<T> supplier, String type) throws ConfigurationNodeException.Unchecked {
        T component;
        if (node.hasMapChildren()) {
            component = supplier.get();
            component.deserialize(node);
            component.setGlobal(false);
            storage.put(component.getName().toLowerCase(), component);
        } else {
            component = storage.get(node.getKey());
            if (component == null) {
                throw new ConfigurationNodeException(node, "Unknown reference %s %s.", type, node.getKey()).asUnchecked();
            } else if (!component.isGlobal()) {
                throw new ConfigurationNodeException(node, "The reference %s %s is not global.", type, node.getKey()).asUnchecked();
            }
        }
        return component;
    }

    public static <T extends Component> void serializeChild(ConfigurationNode base, T component, Object value) {
        ConfigurationNode child = base.getNode(component.getName().contains(":") ? component.getName().substring(component.getName().lastIndexOf(":") + 1) : component.getName());
        if (component.isGlobal()) {
            child.setValue(value);
        } else {
            component.serialize(child);
        }
    }

    public static <T extends Enum<T>> T deserializeEnum(ConfigurationNode node, Class<T> enumType, String format) throws ConfigurationNodeException.Unchecked {
        String name = node.getString("none").replace("-", "_");
        return Arrays.stream(enumType.getEnumConstants()).filter(e -> e.name().equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new ConfigurationNodeException(node, format, name).asUnchecked());
    }

    public static <T extends Enum<T>> void serializeEnum(ConfigurationNode node, T value) {
        node.setValue(value.name().toLowerCase().replace("_", "-"));
    }

    public static ItemStack deserializeItemStack(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        if (node.getNode("custom-serializers").getBoolean(Config.isCustomSerializers())) {
            String id = node.getNode("id").getString("undefined");
            ItemType type = Sponge.getRegistry().getType(ItemType.class, id).orElseThrow(() -> new ConfigurationNodeException(node.getNode("id"), "No item found for id `%s`.", id).asUnchecked());
            int quantity = node.getNode("quantity").getInt(1);
            if (quantity <= 0 || quantity > type.getMaxStackQuantity()) {
                throw new ConfigurationNodeException(node.getNode("quantity"), "Quantity is out of bounds.").asUnchecked();
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
                    throw new ConfigurationNodeException(n, "Expected a map of values for nbt, but instead received " + value.getClass().getSimpleName() + ".").asUnchecked();
                }
            });
            ItemStack.Builder builder = ItemStack.builder().fromContainer(container);
            for (ConfigurationNode child : node.getNode("keys").getChildrenMap().values()) {
                String name = (((String) child.getKey()).contains(":") ? (String) child.getKey() : "sponge:" + child.getKey()).replace("-", "_");
                Key key = Sponge.getRegistry().getAllOf(Key.class).stream().filter(k -> k.getId().equals(name)).findAny().orElseThrow(() -> new ConfigurationNodeException(child, "No key found for id `%s`.", name).asUnchecked());
                try {
                    builder.add(key, child.getValue(key.getElementToken()));
                } catch (ObjectMappingException e) {
                    throw new ConfigurationNodeException(child, e).asUnchecked();
                }
            }
            return builder.build();
        } else {
            try {
                return node.getValue(TypeToken.of(ItemStack.class));
            } catch (ObjectMappingException e) {
                throw new ConfigurationNodeException(node, e).asUnchecked();
            }
        }
    }

    public static void serializeItemStack(ConfigurationNode node, ItemStack item) throws ConfigurationNodeException.Unchecked {
        if (node.getNode("custom-serializers").getBoolean(Config.isCustomSerializers())) {
            node.getNode("id").setValue(item.getItem().getId());
            if (item.getQuantity() != 1) {
                node.getNode("quantity").setValue(item.getQuantity());
            }
            int data = item.toContainer().getInt(DataQuery.of("UnsafeDamage")).orElse(0);
            if (data != 0) {
                node.getNode("data").setValue(data);
            }
            for (Key key : item.getKeys()) {
                ConfigurationNode child = node.getNode("keys", key.getId().substring(key.getId().startsWith("sponge:") ? 7 : 0).replace("_", "-"));
                try {
                    child.setValue(key.getElementToken(), item.get(key).get());
                } catch (ObjectMappingException e) {
                    throw new ConfigurationNodeException(node.getNode("keys", key.getId()), e).asUnchecked();
                }
            }
        } else {
            try {
                node.setValue(TypeToken.of(ItemStack.class), item);
            } catch (ObjectMappingException e) {
                throw new ConfigurationNodeException(node, e).asUnchecked();
            }
        }
    }

}