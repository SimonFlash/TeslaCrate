package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mcsimonflash.sponge.teslacrate.TeslaCrate;
import com.mcsimonflash.sponge.teslacrate.api.component.Effect;
import com.mcsimonflash.sponge.teslacrate.api.component.Type;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class ParticleEffect extends Effect.Locatable {

    public static final Type<ParticleEffect, Vector3d> TYPE = new Type<>("Particle", ParticleEffect::new, n -> !n.getNode("particle").isVirtual(), TeslaCrate.get().getContainer());

    private ParticleType type = ParticleTypes.REDSTONE_DUST;
    private Color color = Color.BLACK;
    private boolean rainbow;

    private ParticleEffect(String name) {
        super(name);
    }

    public final ParticleType getType() {
        return type;
    }

    public final void setType(ParticleType type) {
        this.type = type;
    }

    public final Color getColor() {
        return color;
    }

    public final void setColor(Color color) {
        this.color = color;
    }

    public final boolean isRainbow() {
        return rainbow;
    }

    public final void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    @Override
    public final void run(Location<World> location) {
        location.getExtent().spawnParticles(org.spongepowered.api.effect.particle.ParticleEffect.builder()
                .type(type).build(), location.getPosition());
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        NodeUtils.ifAttached(node.getNode("particle"), p -> {
            NodeUtils.ifAttached(p.getNode("type"), n -> setType(Serializers.deserializeCatalogType(n, ParticleType.class)));
            NodeUtils.ifAttached(p.getNode("color"), n -> setColor(Serializers.deserializeColor(n)));
            setRainbow(p.getNode("rainbow").getBoolean(false));
        });
        super.deserialize(node);
    }

    @Override
    protected final ItemStack.Builder createDisplayItem(Vector3d value) {
        return Utils.createItem(ItemTypes.REDSTONE, getName(), Lists.newArrayList(getDescription()));
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "type", type.getId())
                .add(indent + "color", Integer.toHexString(color.getRgb()))
                .add(indent + "rainbow", rainbow);
    }

}
