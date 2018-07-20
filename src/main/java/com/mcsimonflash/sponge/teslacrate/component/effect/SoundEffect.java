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
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class SoundEffect extends Effect.Locatable {

    public static final Type<SoundEffect, Vector3d> TYPE = new Type<>("Sound", SoundEffect::new, n -> !n.getNode("sound").isVirtual(), TeslaCrate.get().getContainer());

    private SoundType type = SoundTypes.ENTITY_SLIME_SQUISH;
    private double volume = 1.0;
    private double pitch = 1.0;

    private SoundEffect(String name) {
        super(name);
    }

    public final SoundType getType() {
        return type;
    }

    public final void setType(SoundType type) {
        this.type = type;
    }

    public final double getVolume() {
        return volume;
    }

    public final void setVolume(double volume) {
        this.volume = volume;
    }

    public final double getPitch() {
        return pitch;
    }

    public final void setPitch(double pitch) {
        this.pitch = pitch;
    }

    @Override
    public final void run(Location<World> location) {
        location.getExtent().playSound(type, location.getPosition(), volume, pitch);
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        NodeUtils.ifAttached(node.getNode("sound"), s -> {
            NodeUtils.ifAttached(s.getNode("type"), n -> setType(Serializers.deserializeCatalogType(n, SoundType.class)));
            setVolume(s.getNode("volume").getDouble(1.0));
            setPitch(s.getNode("pitch").getDouble(1.0));
        });
        super.deserialize(node);
    }

    @Override
    protected final ItemStack.Builder createDisplayItem(Vector3d offset) {
        return Utils.createItem(ItemTypes.RECORD_13, getName(), Lists.newArrayList(getDescription()));
    }

    @Override
    protected final MoreObjects.ToStringHelper toStringHelper(String indent) {
        return super.toStringHelper(indent)
                .add(indent + "type", type.getId())
                .add(indent + "volume", volume)
                .add(indent + "pitch", pitch);
    }

}
