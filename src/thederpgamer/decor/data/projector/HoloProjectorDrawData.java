package thederpgamer.decor.data.projector;

import org.schema.game.common.data.SegmentPiece;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public class HoloProjectorDrawData extends ProjectorDrawData {

    public String src;

    public HoloProjectorDrawData(SegmentPiece segmentPiece) {
        super(segmentPiece);
        src = "";
    }
}
