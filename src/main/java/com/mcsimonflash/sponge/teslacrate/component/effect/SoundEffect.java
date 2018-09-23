package com.mcsimonflash.sponge.teslacrate.component.effect;

import com.flowpowered.math.vector.Vector3d;
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
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class SoundEffect extends Effect.Locatable<SoundEffect> {

    public static final Type<SoundEffect, Vector3d> TYPE = new Type<>("Sound", SoundEffect::new, n -> !n.getNode("sound").isVirtual(), TeslaCrate.get().getContainer());

    private SoundType type = SoundTypes.ENTITY_SLIME_SQUISH;
    private double volume = 1.0;
    private double pitch = 1.0;

    private SoundEffect(String name) {
        super(name);
    }

    @Override
    public final void run(Location<World> location) {
        location.getExtent().playSound(type, location.getPosition(), volume, pitch);
    }

    @Override
    public final void deserialize(ConfigurationNode node) {
        if (node.getNode("sound").hasMapChildren()) {
            NodeUtils.ifAttached(node.getNode("sound", "type"), n -> type = Serializers.catalogType(n, SoundType.class));
            volume = node.getNode("sound", "volume").getDouble(1.0);
            pitch = node.getNode("sound", "pitch").getDouble(1.0);
        } else {
            NodeUtils.ifAttached(node.getNode("sound"), n -> type = Serializers.catalogType(n, SoundType.class));
        }
        super.deserialize(node);
    }

    @Override
    protected final ItemStackSnapshot createDisplayItem(Vector3d offset) {
        return Utils.createItem(ItemTypes.RECORD_13, getName()).build().createSnapshot();
    }

}