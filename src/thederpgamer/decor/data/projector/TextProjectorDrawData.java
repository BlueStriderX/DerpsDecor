package thederpgamer.decor.data.projector;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.manager.LogManager;
import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/06/2021
 */
public class TextProjectorDrawData extends ProjectorDrawData {

    public String text;
    public String color;
    public transient GUITextOverlay textOverlay;

    public TextProjectorDrawData(SegmentPiece segmentPiece) {
        super(segmentPiece);
        text = "";
        color = "FFFFFF";
    }

    public TextProjectorDrawData(PacketReadBuffer packetReadBuffer) {
        super();
        try {
            onTagDeserialize(packetReadBuffer);
        } catch(IOException exception) {
            LogManager.logException("Using default values because something went wrong while trying to deserialize text projector data", exception);
            text = "";
            color = "";
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
        packetWriteBuffer.writeString(text);
        packetWriteBuffer.writeString(color);
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        super.onTagDeserialize(packetReadBuffer);
        text = packetReadBuffer.readString();
        color = packetReadBuffer.readString();
    }
}
