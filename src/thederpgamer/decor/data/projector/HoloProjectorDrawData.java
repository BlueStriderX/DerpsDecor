package thederpgamer.decor.data.projector;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.forms.Sprite;
import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public class HoloProjectorDrawData extends ProjectorDrawData {

    public String src;
    public transient Sprite image;
    public transient float[] dimensions;

    public HoloProjectorDrawData(SegmentPiece segmentPiece) {
        super(segmentPiece);
        src = "";
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        super.onTagSerialize(packetWriteBuffer);
        packetWriteBuffer.writeString(src);
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        super.onTagDeserialize(packetReadBuffer);
        src = packetReadBuffer.readString();
    }
}
