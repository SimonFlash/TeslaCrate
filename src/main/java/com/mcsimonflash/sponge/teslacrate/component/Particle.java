package com.mcsimonflash.sponge.teslacrate.component;

import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslacrate.component.path.*;
import com.mcsimonflash.sponge.teslacrate.internal.Inventory;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslacrate.internal.Utils;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import com.mcsimonflash.sponge.teslalibs.configuration.NodeUtils;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;

import java.util.List;
import java.util.function.Function;

public class Particle extends Component {

    private Function<Float, ParticleEffect> effect;
    private AnimationPath path;
    private boolean animated;
    private int precision;
    private float speed;
    private float shift;
    private Vector3f scale;
    private Vector3f offset;

    public Particle(String name) {
        super(name);
    }

    @Override
    public void deserialize(ConfigurationNode node) throws ConfigurationNodeException.Unchecked {
        ParticleEffect.Builder builder = ParticleEffect.builder();
        String name = node.getNode("particle").getString("redstone_dust").replace("-", "_");
        builder.type(Sponge.getRegistry().getType(ParticleType.class, name.contains(":") ? name : "minecraft:" + name).orElseThrow(() -> new ConfigurationNodeException(node.getNode("particle"), "No particle found for id %s", node.getNode("particle").getString()).asUnchecked()));
        if (node.getNode("rainbow").getBoolean(false)) {
            effect = r -> AnimUtils.particle(AnimUtils.rainbow(10 * r / 7));
        } else {
            NodeUtils.ifAttached(node.getNode("color"), n -> builder.option(ParticleOptions.COLOR, Color.ofRgb(n.getInt(0))));
            ParticleEffect particle = builder.build();
            effect = r -> particle;
        }
        animated = node.getNode("animated").getBoolean(true);
        precision = node.getNode("precision").getInt(120);
        speed = node.getNode("speed").getFloat(1);
        shift = AnimUtils.TAU * node.getNode("shift").getFloat(0);
        scale = node.getNode("scale").isVirtual() ? Vector3f.ONE : Serializers.deserializeVector3d(node.getNode("scale")).toFloat();
        offset = node.getNode("offset").isVirtual() ? Vector3f.ZERO : Serializers.deserializeVector3d(node.getNode("offset")).toFloat();
        int segments = node.getNode("segments").getInt(1);
        if (node.getNode("type").getString("").equalsIgnoreCase("circle")) {
            path = new CirclePath(segments, node.getNode("axis").isVirtual() ? Vector3f.UNIT_Y : Serializers.deserializeVector3d(node.getNode("axis")).toFloat().normalize());
        } else if (node.getNode("type").getString("").equalsIgnoreCase("helix")) {
            path = new HelixPath(segments);
        } else if (node.getNode("type").getString("").equalsIgnoreCase("spiral")) {
            path = new SpiralPath(segments, precision);
        } else if (node.getNode("type").getString("").equalsIgnoreCase("vortex")) {
            path = new VortexPath(segments, precision);
        } else {
            throw new ConfigurationNodeException(node.getNode("type"), "No path type found for input %s", node.getNode("type").getString()).asUnchecked();
        }
        super.deserialize(node);
    }

    public ParticleEffect getEffect(float radians) {
        return effect.apply(radians);
    }

    public AnimationPath getPath() {
        return path;
    }

    public boolean isAnimated() {
        return animated;
    }

    public int getPrecision() {
        return precision;
    }

    public float getSpeed() {
        return speed;
    }

    public float getShift() {
        return shift;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Vector3f getOffset() {
        return offset;
    }

    @Override
    protected ItemStack getDefaultDisplayItem() {
        return Utils.createItem(ItemTypes.REDSTONE, getDisplayName(), (animated ? "Animated " : " ") + path.getClass().getSimpleName().replace("Path", ""), false);
    }

    @Override
    public List<Element> getMenuElements(Element back) {
        List<Element> elements = super.getMenuElements(back);
        elements.add(Inventory.createDetail("Animated", String.valueOf(animated)));
        elements.add(Inventory.createDetail("Precision", String.valueOf(precision)));
        elements.add(Inventory.createDetail("Shift", String.valueOf(shift)));
        elements.add(Inventory.createDetail("Scale", scale.toString()));
        elements.add(Inventory.createDetail("Offset", offset.toString()));
        return elements;
    }

}