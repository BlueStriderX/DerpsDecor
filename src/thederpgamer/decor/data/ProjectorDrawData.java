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
    public long entityId;

    public int xOffset;
    public int yOffset;
    public int zOffset;
    public float scale;
    public String src;

    public ProjectorDrawData(SegmentPiece segmentPiece) {
        if(segmentPiece != null) {
            index = segmentPiece.getAbsoluteIndex();
            entityId = segmentPiece.getSegmentController().getDbId();
            src = "";
            PersistentObjectUtil.addObject(DerpsDecor.getInstance().getSkeleton(), this);
            PersistentObjectUtil.save(DerpsDecor.getInstance().getSkeleton());
        }
    }
}
