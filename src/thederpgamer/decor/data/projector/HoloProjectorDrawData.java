package thederpgamer.decor.data.projector;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.manager.LogManager;
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

    public HoloProjectorDrawData(PacketReadBuffer packetReadBuffer) {
        super();
        try {
            onTagDeserialize(packetReadBuffer);
        } catch(IOException exception) {
            LogManager.logException("Using default values because something went wrong while trying to deserialize holo projector data", exception);
            src = "";
            pieceTransform = new Transform();
            scale = 1;
            offset = new Vector3i();
            rotation = new Vector3i();
            changed = true;
        }
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
