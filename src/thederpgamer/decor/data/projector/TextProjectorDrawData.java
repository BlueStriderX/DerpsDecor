package thederpgamer.decor.data.projector;

import org.schema.game.common.data.SegmentPiece;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/06/2021
 */
public class TextProjectorDrawData extends ProjectorDrawData {

    public String text;
    public String color;

    public TextProjectorDrawData(SegmentPiece segmentPiece) {
        super(segmentPiece);
        text = "";
        color = "FFFFFF";
    }
}
