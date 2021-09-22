package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.utils.SegmentPieceUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public class HoloProjectorDrawData {

    public long indexAndOrientation;
    public Vector3i offset;
    public Vector3i rotation;
    public int scale;
    public boolean changed;
    public String src;

    public transient Transform transform;
    public transient Sprite image;

    public HoloProjectorDrawData(long indexAndOrientation, Vector3i offset, Vector3i rotation, int scale, boolean changed, String src) {
        this.indexAndOrientation = indexAndOrientation;
        this.offset = offset;
        this.rotation = rotation;
        this.scale = scale;
        this.changed = changed;
        this.src = src;
        this.transform = new Transform();
    }

    public HoloProjectorDrawData(SegmentPiece segmentPiece) {
        if(segmentPiece != null) {
            indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
            transform = SegmentPieceUtils.getFullPieceTransform(segmentPiece);
        }
        scale = 1;
        offset = new Vector3i();
        rotation = new Vector3i();
        changed = true;
        src = "";
    }
}
