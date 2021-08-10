package thederpgamer.decor.data.projector;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import thederpgamer.decor.utils.SegmentPieceUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public abstract class ProjectorDrawData {

    public long indexAndOrientation;
    public Vector3i offset;
    public Vector3i rotation;
    public int scale;
    public transient Transform pieceTransform;
    public transient boolean changed;

    public ProjectorDrawData(SegmentPiece segmentPiece) {
        indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
        offset = new Vector3i();
        rotation = new Vector3i();
        scale = 1;
        pieceTransform = SegmentPieceUtils.getFullPieceTransform(segmentPiece);
        changed = true;
    }
}
