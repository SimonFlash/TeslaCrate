package com.mcsimonflash.sponge.teslacrate.objects;

import com.flowpowered.math.vector.Vector3i;
import com.mcsimonflash.sponge.teslacrate.objects.exceptions.LocationFormatException;

public class CrateLocation {

    public CrateLocation(String world, Vector3i vec3i) {
        World = world;
        Vec3i = vec3i;
    }

    public String World;
    public Vector3i Vec3i;

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof CrateLocation && (World.equals(((CrateLocation) o).World) && Vec3i.equals(((CrateLocation) o).Vec3i));
    }

    @Override
    public int hashCode() {
        return 31 * World.hashCode() + Vec3i.hashCode();
    }

    public String print() {
        return World + "," + Vec3i.getX() + "," + Vec3i.getY() + "," + Vec3i.getZ();
    }

    public static CrateLocation buildCrateLoc(String[] locArr) throws LocationFormatException {
        if (locArr.length == 4) {
            try {
                return new CrateLocation(locArr[0], new Vector3i(Integer.parseInt(locArr[1]), Integer.parseInt(locArr[2]), Integer.parseInt(locArr[3])));
            } catch (NumberFormatException ignored) {
                throw new LocationFormatException("Unable to parse coordinates " + locArr[1] + "," + locArr[2] + "," + locArr[3] + "!");
            }
        } else {
            throw new LocationFormatException("Insufficient number of arguments! (Expected 4)");
        }
    }
}
