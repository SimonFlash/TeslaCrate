package com.mcsimonflash.sponge.teslacrate.internal;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mcsimonflash.sponge.teslacrate.api.component.Component;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.component.opener.InstantGuiOpener;
import com.mcsimonflash.sponge.teslacrate.component.opener.Opener;
import com.mcsimonflash.sponge.teslacrate.component.opener.RouletteGuiOpener;
import com.mcsimonflash.sponge.teslacrate.component.opener.StandardOpener;
import com.mcsimonflash.sponge.teslacrate.component.path.AnimationPath;
import com.mcsimonflash.sponge.teslacrate.component.path.CirclePath;
import com.mcsimonflash.sponge.teslacrate.component.path.HelixPath;
import com.mcsimonflash.sponge.teslacrate.component.path.SpiralPath;
import com.mcsimonflash.sponge.teslacrate.component.path.VortexPath;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationException;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public enum Serializers {;

    public static <T extends Component<T, ?>> Type<? extends T, ?> getType(ConfigurationNode node, Registry<T> registry) throws ConfigurationException {
        ConfigurationNode typeNode = node.getNode("type");
        if (!typeNode.isVirtual()) {
            return registry.getType(typeNode.getString("")).orElseThrow(() -> new ConfigurationException(typeNode, "No type found for %s.", typeNode.getString("undefined")));
        }
        List<Type<? extends T, ?>> types = registry.getTypes().getDistinct()
                .stream()
                .map(Tuple::getFirst)
                .filter(t -> t.matches(node))
                .collect(Collectors.toList());
        if (types.size() == 1) {
            return types.get(0);
        } else if (types.isEmpty()) {
            return registry.getType("Standard").orElseThrow(() -> new ConfigurationException(node, "TypeSense matched no types and a Standard type does not exist."));
        } else {
            throw new ConfigurationException(node, "TypeSense could not determine a unique type.");
        }
    }

    public static <T extends Component<T, ?>> T getComponent(String id, ConfigurationNode node, Registry<T> registry, PluginContainer container) {
        T component;
        if (node.hasMapChildren()) {
            Type<? extends T, ?> type = Serializers.<T>getType(node, registry);
            component = type.create(id);
            component.deserialize(node);
            registry.register(component, container);
        } else {
            String key = (String) node.getKey();
            String refined = key.contains("/") ? key.substring(0, key.lastIndexOf('/')) : key;
            component = registry.get(refined).orElseThrow(() -> new ConfigurationException(node, "No component identified by " + refined));
        }
        return component;
    }

    public static <T extends CatalogType> T catalogType(ConfigurationNode node, Class<T> type) throws ConfigurationException {
        return catalogType(node, node.getString("undefined"), "minecraft", type);
    }

    public static <T extends CatalogType> T catalogType(ConfigurationNode node, String id, String namespace, Class<T> type) throws ConfigurationException {
        return Sponge.getRegistry().getType(type, id).orElseGet(() -> Sponge.getRegistry().getType(type, id.contains(":") ? id.substring(id.indexOf(":") + 1) : namespace + ":" + id).orElseThrow(() ->
                new ConfigurationException(node, "No " + type.getSimpleName() + " found for id %s", id)));
    }

    public static ItemStackSnapshot itemStack(ConfigurationNode node) throws ConfigurationException {
        ItemType type = catalogType(node.getNode("type"), ItemType.class);
        int quantity = node.getNode("quantity").getInt(1);
        if (quantity <= 0 || quantity > type.getMaxStackQuantity()) {
            throw new ConfigurationException(node.getNode("quantity"), "Quantity is out of bounds.");
        }
        DataContainer container = ItemStack.of(type, quantity).toContainer();
        NodeUtils.ifAttached(node.getNode("data"), n -> container.set(DataQuery.of("UnsafeDamage"), n.getInt(0)));
        NodeUtils.ifAttached(node.getNode("nbt"), n -> {
            if (n.hasMapChildren()) {
                Map data = container.getMap(DataQuery.of("UnsafeData")).map(Map.class::cast).orElseGet(Maps::newHashMap);
                data.putAll((Map) n.getValue());
                container.set(DataQuery.of("UnsafeData"), data);
            } else {
                throw new ConfigurationException(n, "Expected a map of values for nbt, but instead received " + n.getValue().getClass().getSimpleName() + ".");
            }
        });
        ItemStack.Builder builder = ItemStack.builder().fromContainer(container);
        NodeUtils.ifAttached(node.getNode("name"), n -> builder.add(Keys.DISPLAY_NAME, Utils.toText(n.getString(""))));
        NodeUtils.ifAttached(node.getNode("lore"), n -> builder.add(Keys.ITEM_LORE, n.hasListChildren() ? n.getChildrenList().stream().map(l -> Utils.toText(l.getString(""))).collect(Collectors.toList()) : Lists.newArrayList(Utils.toText(n.getString("")))));
        node.getNode("keys").getChildrenMap().values().forEach(c -> {
            Key key = catalogType(c, ((String) c.getKey()).replace("-", "_"), "sponge", Key.class);
            try {
                builder.add(key, c.getValue(key.getElementToken()));
            } catch (ObjectMappingException e) {
                throw new ConfigurationException(c, e.getMessage());
            }
        });
        NodeUtils.ifAttached(node.getNode("enchantments"), n -> builder.add(Keys.ITEM_ENCHANTMENTS, n.getChildrenMap().values().stream()
                .map(e -> Enchantment.of(catalogType(e, ((String) e.getKey()).replace("-", "_"), "minecraft", EnchantmentType.class), e.getInt(0)))
                .collect(Collectors.toList())));
        return builder.build().createSnapshot();
    }

    public static <T extends Enum<T>> T enumeration(ConfigurationNode node, Class<T> type) {
        return Arrays.stream(type.getEnumConstants()).filter(e -> e.name().equalsIgnoreCase(node.getString(""))).findFirst().orElseThrow(() -> new ConfigurationException(node, "No " + type.getSimpleName() + " found for id %s", node.getString("undefined")));
    }

    public static Color color(ConfigurationNode node) throws ConfigurationException {
        try {
            return node.hasListChildren() ? Color.of(vector3i(node)) : Color.ofRgb(Integer.decode(node.getString("undefined")));
        } catch (NumberFormatException e) {
            throw new ConfigurationException(node, "Unable to decode " + node.getString("") + " as an integer.");
        }
    }

    public static Opener opener(ConfigurationNode node) throws ConfigurationException {
        switch (node.getString("").toLowerCase()) {
            case "instantgui": return InstantGuiOpener.INSTANCE;
            case "roulettegui": return RouletteGuiOpener.INSTANCE;
            case "standard": return StandardOpener.INSTANCE;
            default: throw new ConfigurationException(node, "No opener of type " + node.getString("") + ".");
        }
    }

    public static AnimationPath pathType(ConfigurationNode node) throws ConfigurationException {
        switch (node.getString("").toLowerCase()) {
            case "circle": return new CirclePath();
            case "helix": return new HelixPath();
            case "spiral": return new SpiralPath();
            case "vortex": return new VortexPath();
            default: throw new ConfigurationException(node.getNode("type"), "No path exists for type " + node.getNode("type").getString("undefined") + ".");
        }
    }

    public static AnimationPath path(ConfigurationNode node) throws ConfigurationException {
        AnimationPath path = pathType(node.getNode("type"));
        if (!node.getNode("axis").isVirtual() && path instanceof CirclePath) {
            ((CirclePath) path).setAxis(vector3d(node.getNode("axis")).toFloat());
        }
        path.setAnimated(node.getNode("animated").getBoolean(false));
        path.setInterval(node.getNode("interval").getInt(20));
        path.setPrecision(node.getNode("precision").getInt(120));
        path.setSegments(node.getNode("segments").getInt(1));
        path.setShift(node.getNode("shift").getFloat(0));
        path.setSpeed(node.getNode("speed").getFloat(1));
        NodeUtils.ifAttached(node.getNode("scale"), n -> path.setScale(vector3d(n).toFloat()));
        return path;
    }

    public static Vector3i vector3i(ConfigurationNode node) throws ConfigurationException {
        Integer[] components = vectorComponents(node, Integer::parseInt, Integer[]::new);
        return Vector3i.from(components[0], components[1], components[2]);
    }

    public static Vector3d vector3d(ConfigurationNode node) throws ConfigurationException {
        Double[] components = vectorComponents(node, Double::parseDouble, Double[]::new);
        return Vector3d.from(components[0], components[1], components[2]);
    }

    private static <T> T[] vectorComponents(ConfigurationNode node, Function<String, T> parser, IntFunction<T[]> array) {
        T[] components;
        if (node.hasListChildren()) {
            components = node.getChildrenList().stream().map(n -> n.getString("")).map(parser).toArray(array);
        } else {
            try {
                components = Arrays.stream(node.getString("").split(",")).map(parser).toArray(array);
            } catch (NumberFormatException e) {
                throw new ConfigurationException(node, "Unable to retrieve a list of numbers.");
            }
        }
        if (components.length != 3) {
            throw new ConfigurationException(node, "Expected 3 vector components, received %s.", components.length);
        }
        return components;
    }

}
