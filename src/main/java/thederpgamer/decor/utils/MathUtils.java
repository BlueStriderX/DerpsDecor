package thederpgamer.decor.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * Various math related utility functions.
 *
 * @author TheDerpGamer
 * @since 07/16/2021
 */
public class MathUtils {

  public static final RoundingMode roundingMode = RoundingMode.HALF_EVEN;

  /**
   * Rounds a Quat4f for use in matrix rotation.
   *
   * @param out The Quat to round.
   */
  public static void roundQuat(Quat4f out) {
    MathContext context = new MathContext(0, roundingMode);
    BigDecimal bdX = new BigDecimal(out.x);
    BigDecimal bdY = new BigDecimal(out.y);
    BigDecimal bdZ = new BigDecimal(out.z);
    BigDecimal bdW = new BigDecimal(out.w);
    out.set(
        bdX.round(context).floatValue(),
        bdY.round(context).floatValue(),
        bdZ.round(context).floatValue(),
        bdW.round(context).floatValue());
  }

  public static void roundVector(Vector3f out) {
    MathContext context = new MathContext(0, roundingMode);
    BigDecimal bdX = new BigDecimal(out.x);
    BigDecimal bdY = new BigDecimal(out.y);
    BigDecimal bdZ = new BigDecimal(out.z);
    out.set(
        bdX.round(context).floatValue(),
        bdY.round(context).floatValue(),
        bdZ.round(context).floatValue());
  }

  public static void roundMatrix(Matrix3f out) {
    MathContext context = new MathContext(0, roundingMode);
    out.normalize();
  }
}
