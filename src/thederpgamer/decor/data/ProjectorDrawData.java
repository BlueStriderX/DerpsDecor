package thederpgamer.decor.data;

import api.mod.config.PersistentObjectUtil;
import org.schema.game.common.data.SegmentPiece;
import thederpgamer.decor.DerpsDecor;
import java.io.Serializable;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class ProjectorDrawData implements Serializable {

    public long index;
    public int xPos;
    public int yPos;
    public int zPos;
    public int entityId;

    public int xOffset;
    public int yOffset;
    public int zOffset;
    public int scale;
    public String src;

    public ProjectorDrawData(SegmentPiece segmentPiece) {
        if(segmentPiece != null) {
            index = segmentPiece.getAbsoluteIndex();
            xPos = segmentPiece.x;
            yPos = segmentPiece.y;
            zPos = segmentPiece.z;
            entityId = segmentPiece.getSegmentController().getId();
            src = "";
            PersistentObjectUtil.addObject(DerpsDecor.getInstance().getSkeleton(), this);
            PersistentObjectUtil.save(DerpsDecor.getInstance().getSkeleton());
        }
    }

    @Override
    public int hashCode() {
        return entityId + (xPos * yPos * zPos);
    }

    public static int getHashCode(SegmentPiece segmentPiece) {
        return segmentPiece.getSegmentController().getId() + (segmentPiece.x * segmentPiece.y * segmentPiece.z);
    }

    public static int getHashCode(int entityId, int x, int y, int z) {
        return entityId + (x * y * z);
    }
}
