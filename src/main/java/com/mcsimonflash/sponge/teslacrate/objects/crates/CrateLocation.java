package com.mcsimonflash.sponge.teslacrate.objects.crates;

import com.flowpowered.math.vector.Vector3i;

import java.util.Objects;

public class CrateLocation {

    public String World;
    public Vector3i Vec3i;

    public CrateLocation(String world, Vector3i vec3i) {
        World = world;
        Vec3i = vec3i;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof CrateLocation && (World.equals(((CrateLocation) o).World) && Vec3i.equals(((CrateLocation) o).Vec3i));
    }

    @Override
    public int hashCode() {
        return Objects.hash(World, Vec3i);
    }

    public String print() {
        return World + "," + Vec3i.getX() + "," + Vec3i.getY() + "," + Vec3i.getZ();
    }

    public static CrateLocation buildCrateLoc(String locStr) throws IllegalArgumentException {
        String[] locArr = locStr.split(",");
        if (locArr.length == 4) {
            try {
                return new CrateLocation(locArr[0], new Vector3i(Integer.parseInt(locArr[1]), Integer.parseInt(locArr[2]), Integer.parseInt(locArr[3])));
            } catch (NumberFormatException ignored) {
                throw new IllegalArgumentException("Unable to parse position! | Position:[" + locArr[1] + "," + locArr[2] + "," + locArr[3] + "] Location:[" + locStr + "]");
            }
        } else {
            throw new IllegalArgumentException("Insufficient number of arguments! (Expected 4) | Location:[" + locStr + "]");
        }
    }
}
