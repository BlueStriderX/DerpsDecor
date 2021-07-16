package thederpgamer.decor.utils;

import javax.vecmath.Quat4f;
import java.text.DecimalFormat;

/**
 * Various math related utility functions.
 *
 * @author TheDerpGamer
 * @since 07/16/2021
 */
public class MathUtils {

    /**
     * Rounds a Quat4f to a single decimal place for use in matrix rotation.
     * @param out The Quat to round.
     */
    public static void roundQuat(Quat4f out) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        out.x = Float.parseFloat(decimalFormat.format(out.x));
        out.y = Float.parseFloat(decimalFormat.format(out.y));
        out.z = Float.parseFloat(decimalFormat.format(out.z));
        out.w = Float.parseFloat(decimalFormat.format(out.w));
    }
}
