package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.utils.SegmentPieceUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/06/2021
 */
public class TextProjectorDrawData implements ProjectorDrawData {

    public long indexAndOrientation;
    public Vector3i offset;
    public Vector3i rotation;
    public int scale;
    public boolean changed;
    public String text;
    public String color;

    public transient Transform transform;
    public transient GUITextOverlay textOverlay;

    public TextProjectorDrawData() {

    }

    public TextProjectorDrawData(SegmentPiece segmentPiece) {
        if(segmentPiece != null) {
            indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
            transform = SegmentPieceUtils.getFullPieceTransform(segmentPiece);
        }
        scale = 1;
        offset = new Vector3i();
        rotation = new Vector3i();
        changed = true;
        text = "Text";
        color = "FFFFFF";
    }

    @Override
    public long getIndexAndOrientation() {
        return indexAndOrientation;
    }

    @Override
    public void setIndexAndOrientation(long indexAndOrientation) {
        this.indexAndOrientation = indexAndOrientation;
    }

    @Override
    public Vector3i getOffset() {
        return offset;
    }

    @Override
    public void setOffset(Vector3i offset) {
        this.offset = offset;
    }

    @Override
    public Vector3i getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(Vector3i rotation) {
        this.rotation = rotation;
    }

    @Override
    public int getScale() {
        return scale;
    }

    @Override
    public void setScale(int scale) {
        this.scale = scale;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
