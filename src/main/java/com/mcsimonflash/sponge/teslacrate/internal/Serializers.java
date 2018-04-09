package com.mcsimonflash.sponge.teslacrate.internal;

import com.google.common.collect.Lists;
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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
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
            component = storage.get(((String) node.getKey()).toLowerCase());
            if (component == null) {
                throw new ConfigurationNodeException(node, "Unknown reference %s %s.", type, node.getKey()).asUnchecked();
            } else if (!component.isGlobal()) {
                throw new ConfigurationNodeException(node, "The reference %s %s is not global.", type, node.getKey()).asUnchecked();
            }
        }
        return component;
    }

    public static <T extends Enum<T>> T deserializeEnum(ConfigurationNode node, Class<T> enumType, String format) throws ConfigurationNodeException.Unchecked {
        String name = node.getString("none").replace("-", "_");
        return Arrays.stream(enumType.getEnumConstants()).filter(e -> e.name().equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new ConfigurationNodeException(node, format, name).asUnchecked());
    }

    public static ItemStack deserializeItemStack(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        if (node.getNode("custom-serializers").getBoolean(Config.customSerialization)) {
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
                Key key = Sponge.getRegistry().getType(Key.class, name).orElseThrow(() -> new ConfigurationNodeException(child, "No key found for id %s.", name).asUnchecked());
                wrapOME(child, c -> builder.add(key, c.getValue(key.getElementToken())));
            }
            List<Enchantment> enchantments = Lists.newArrayList();
            for (ConfigurationNode child : node.getNode("enchantments").getChildrenMap().values()) {
                String name = ((String) child.getKey()).replace("-", "_");
                EnchantmentType enchantment = Sponge.getRegistry().getType(EnchantmentType.class, name.contains(":") ? name : "minecraft:" + name).orElseThrow(() -> new ConfigurationNodeException(child, "No enchantment found for id %s.", name).asUnchecked());
                enchantments.add(Enchantment.of(enchantment, child.getInt(0)));
            }
            if (!enchantments.isEmpty()) {
                builder.add(Keys.ITEM_ENCHANTMENTS, enchantments);
            }
            return builder.build();
        } else {
            ItemStack.Builder builder = ItemStack.builder();
            wrapOME(node, n -> builder.from(n.getValue(TypeToken.of(ItemStack.class))));
            return builder.build();
        }
    }

    private static void wrapOME(ConfigurationNode node, ThrowableConsumer<ConfigurationNode, ObjectMappingException> consumer) throws ConfigurationNodeException.Unchecked {
        try {
            consumer.accept(node);
        } catch (ObjectMappingException e) {
            throw new ConfigurationNodeException(node, e).asUnchecked();
        }
    }

    public interface ThrowableConsumer<T, E extends Exception> {

        void accept(T value) throws E;

    }

}