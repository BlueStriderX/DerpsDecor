package thederpgamer.decor.utils;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.FastMath;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.Element;
import javax.vecmath.Matrix3f;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/10/2021
 */
public class SegmentPieceUtils {

    //Rotation Helpers
    private static final Matrix3f mY = new Matrix3f();
    private static final Matrix3f mYB = new Matrix3f();
    private static final Matrix3f mYC = new Matrix3f();
    private static final Matrix3f mX = new Matrix3f();
    private static final Matrix3f mXB = new Matrix3f();
    private static final Matrix3f mXC = new Matrix3f();

    public static void initialize() {
        mY.setIdentity();
        mY.rotY(FastMath.HALF_PI);
        mYB.setIdentity();
        mYB.rotY(-FastMath.HALF_PI);
        mYC.setIdentity();
        mYC.rotY(FastMath.PI);

        mX.setIdentity();
        mX.rotX(FastMath.HALF_PI);
        mXB.setIdentity();
        mXB.rotX(-FastMath.HALF_PI);
        mXC.setIdentity();
        mXC.rotX(FastMath.PI);
    }

    /**
     * Gets the full Transform of a SegmentPiece, including both it's position and rotation.
     * @param segmentPiece The SegmentPiece to get the transform of
     * @return The full transform of the SegmentPiece
     */
    public static Transform getFullPieceTransform(SegmentPiece segmentPiece) {
        Transform transform = new Transform();
        transform.setIdentity();
        segmentPiece.getTransform(transform);

        float sNormalDir = 0.51f - 1.0f;
        float sVertical = 0.51f;
        float sHorizontal = 0.51f;
        
        int orientation = segmentPiece.getOrientation();
        switch(orientation) { 
            case(Element.FRONT):
                transform.basis.mul(mYC);
                transform.origin.x -= sHorizontal;
                transform.origin.y += sVertical;
                transform.origin.z += sNormalDir;
                break;
            case(Element.BACK):
                transform.origin.x += sHorizontal;
                transform.origin.y += sVertical;
                transform.origin.z -= sNormalDir;
                break;
            case(Element.TOP):
                transform.basis.mul(mX);
                transform.origin.x += sHorizontal;
                transform.origin.y += sNormalDir;
                transform.origin.z += sVertical;
                break;
            case(Element.BOTTOM):
                transform.basis.mul(mYC);
                transform.basis.mul(mXB);
                transform.origin.x -= sHorizontal;
                transform.origin.y -= sNormalDir;
                transform.origin.z += sVertical;
                break;
            case(Element.RIGHT):
                transform.basis.mul(mY);
                transform.origin.x -= sNormalDir;
                transform.origin.y += sVertical;
                transform.origin.z -= sHorizontal;
                break;
            case(Element.LEFT):
                transform.basis.mul(mYB);
                transform.origin.x += sNormalDir;
                transform.origin.y += sVertical;
                transform.origin.z += sHorizontal;
                break;
        }
        return transform;
    }
}
