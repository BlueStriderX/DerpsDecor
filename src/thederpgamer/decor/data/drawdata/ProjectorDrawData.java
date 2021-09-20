package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public abstract class ProjectorDrawData {

    public ProjectorDrawData() {
        
    }

    public abstract long getIndexAndOrientation();
    public abstract void setIndexAndOrientation(long indexAndOrientation);

    public abstract Vector3i getOffset();
    public abstract void setOffset(Vector3i offset);

    public abstract Vector3i getRotation();
    public abstract void setRotation(Vector3i rotation);

    public abstract  int getScale();
    public abstract  void setScale(int scale);

    public abstract boolean isChanged();
    public abstract void setChanged(boolean changed);

    public abstract Transform getTransform();
    public abstract void setTransform(Transform transform);

    /*
    public long indexAndOrientation;
    public Vector3i offset;
    public Vector3i rotation;
    public int scale;
    public boolean changed;
    public transient Transform transform = new Transform();

    public ProjectorDrawData(SegmentPiece segmentPiece) {
        if(segmentPiece != null) {
            indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
            transform = SegmentPieceUtils.getFullPieceTransform(segmentPiece);
        }
        scale = 1;
        offset = new Vector3i();
        rotation = new Vector3i();
        changed = true;
    }

    public ProjectorDrawData() {

    }

    public abstract void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException;
    public abstract void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException;

     */
}
