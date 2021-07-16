package thederpgamer.decor.utils;

import javax.vecmath.Quat4f;

/**
 * Various math related utility functions.
 *
 * @author TheDerpGamer
 * @since 07/16/2021
 */
public class MathUtils {

    /**
     * Rounds a Quat4f for use in matrix rotation.
     * @param out The Quat to round.
     */
    public static void roundQuat(Quat4f out) {
        out.x = Math.round(out.x);
        out.y = Math.round(out.y);
        out.z = Math.round(out.z);
        out.w = Math.round(out.w);
    }
}
