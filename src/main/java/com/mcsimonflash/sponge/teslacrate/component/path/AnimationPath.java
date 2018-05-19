package com.mcsimonflash.sponge.teslacrate.component.path;

import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslacrate.internal.Serializers;
import com.mcsimonflash.sponge.teslalibs.configuration.ConfigurationNodeException;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class AnimationPath {

    private final int segments;

    public AnimationPath(int segments) {
        this.segments = segments;
    }

    public int getSegments() {
        return segments;
    }

    public abstract Vector3f[] getPositions(float radians);

    public static AnimationPath getPath(ConfigurationNode node, int precision) throws ConfigurationNodeException.Unchecked {
        int segments = node.getNode("segments").getInt(1);
        switch (node.getNode("type").getString("undefined").toLowerCase()) {
            case "circle":
                return new CirclePath(segments, node.getNode("axis").isVirtual() ? Vector3f.UNIT_Y : Serializers.deserializeVector3d(node.getNode("axis")).toFloat().normalize());
            case "helix":
                return new HelixPath(segments);
            case "spiral":
                return new SpiralPath(segments, precision);
            case "vortex":
                return new VortexPath(segments, precision);
            default:
                throw new ConfigurationNodeException(node.getNode("type"), "No path type found for input %s", node.getNode("type").getString("undefined")).asUnchecked();
        }
    }

}