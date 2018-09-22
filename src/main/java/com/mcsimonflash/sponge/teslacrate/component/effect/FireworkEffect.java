package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public final class FireworkEffect extends Effect.Locatable<FireworkEffect> {

    public static final Type<FireworkEffect, Vector3d> TYPE = new Type<>("Firework", FireworkEffect::new, TeslaCrate.get().getContainer());

    private FireworkShape shape = FireworkShapes.BALL;
    private final List<Color> colors = Lists.newArrayList();
    private final List<Color> fades = Lists.newArrayList();
    private boolean flicker = false;
    private boolean trail = false;
    private int strength = 0;

    private FireworkEffect(String name) {
        super(name);
    }

    @Override
    public final void run(Location<World> location) {
        Entity firework = location.getExtent().createEntity(EntityTypes.FIREWORK, location.getPosition());
        firework.offer(Keys.FIREWORK_EFFECTS, Lists.newArrayList(org.spongepowered.api.item.FireworkEffect.builder()
                .colors(colors).fades(fades).flicker(flicker).trail(trail).build()));
        firework.offer(Keys.EXPLOSION_RADIUS, Optional.of(0));
        firework.offer(Keys.FIREWORK_FLIGHT_MODIFIER, strength);
        location.getExtent().spawnEntity(firework);
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        if (node.getNode("firework").hasMapChildren()) {
            NodeUtils.ifAttached(node.getNode("firework", "shape"), n -> shape = Serializers.catalogType(n, FireworkShape.class));
            NodeUtils.ifAttached(node.getNode("firework", "color"), n -> colors.add(Serializers.color(n)));
            NodeUtils.ifAttached(node.getNode("firework", "fade"), n -> fades.add(Serializers.color(n)));
            flicker = node.getNode("firework", "flicker").getBoolean(false);
            trail = node.getNode("firework", "trail").getBoolean(false);
            strength = node.getNode("firework", "strength").getInt(0);
        } else {
            NodeUtils.ifAttached(node.getNode("firework"), n -> shape = Serializers.catalogType(n, FireworkShape.class));
        }
        super.deserialize(node);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Vector3d value) {
        return Utils.createItem(ItemTypes.FIREWORKS, getName()).build().createSnapshot();
    }

}