package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public final class FireworkEffect extends Effect.Locatable {

    public static final Type<FireworkEffect> TYPE = new Type<>("Firework", FireworkEffect::new, n -> !n.getNode("firework").isVirtual(), TeslaCrate.get().getContainer());

    private FireworkShape shape = FireworkShapes.BALL;
    private final List<Color> colors = Lists.newArrayList();
    private final List<Color> fades = Lists.newArrayList();
    private boolean flicker = false;
    private boolean trail = false;
    private int strength = 0;

    private FireworkEffect(String name) {
        super(name);
    }

    public final FireworkShape getShape() {
        return shape;
    }

    public final void setShape(FireworkShape shape) {
        this.shape = shape;
    }

    public final List<Color> getColors() {
        return colors;
    }

    public final void addColor(Color color) {
        colors.add(color);
    }

    public final List<Color> getFades() {
        return fades;
    }

    public final void addFade(Color fade) {
        fades.add(fade);
    }

    public final boolean isFlicker() {
        return flicker;
    }

    public final void setFlicker(boolean flicker) {
        this.flicker = flicker;
    }

    public final boolean isTrail() {
        return trail;
    }

    public final void setTrail(boolean trail) {
        this.trail = trail;
    }

    public final int getStrength() {
        return strength;
    }

    public final void setStrength(int strength) {
        this.strength = strength;
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
            NodeUtils.ifAttached(node.getNode("firework", "shape"), n -> setShape(Serializers.deserializeCatalogType(n, FireworkShape.class)));
            NodeUtils.ifAttached(node.getNode("firework", "color"), n -> addColor(Serializers.deserializeColor(n)));
            NodeUtils.ifAttached(node.getNode("firework", "fade"), n -> addFade(Serializers.deserializeColor(n)));
            setFlicker(node.getNode("firework", "flicker").getBoolean(false));
            setTrail(node.getNode("firework", "trail").getBoolean(false));
            setStrength(node.getNode("firework", "strength").getInt(0));
        } else {
            NodeUtils.ifAttached(node.getNode("firework"), n -> setShape(Serializers.deserializeCatalogType(n, FireworkShape.class)));
        }
        super.deserialize(node);
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "shape", shape.getId())
                .add(indent + "colors", colors.stream().map(Color::getRgb).map(Integer::toHexString).toArray())
                .add(indent + "fades", fades.stream().map(Color::getRgb).map(Integer::toHexString).toArray())
                .add(indent + "flicker", flicker)
                .add(indent + "trail", trail)
                .add(indent + "strength", strength);
    }

}