package thederpgamer.decor.data.drawdata;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
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

    public HoloProjectorDrawData(SegmentPiece segmentPiece) {
        super(segmentPiece);
    }

    public HoloProjectorDrawData(PacketReadBuffer packetReadBuffer) {
        super();
        try {
            onTagDeserialize(packetReadBuffer);
        } catch(IOException exception) {
            LogManager.logException("Using default values because something went wrong while trying to deserialize holo projector data", exception);
            src = "";
            scale = 1;
            offset = new Vector3i();
            rotation = new Vector3i();
            changed = true;
        }
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        //super.onTagSerialize(packetWriteBuffer);
        if(src == null) src = "";
        packetWriteBuffer.writeLong(indexAndOrientation);
        packetWriteBuffer.writeVector(offset);
        packetWriteBuffer.writeVector(rotation);
        packetWriteBuffer.writeInt(scale);
        packetWriteBuffer.writeBoolean(changed);
        packetWriteBuffer.writeString(src);
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        //super.onTagDeserialize(packetReadBuffer);
        offset = packetReadBuffer.readVector();
        rotation = packetReadBuffer.readVector();
        scale = packetReadBuffer.readInt();
        changed = packetReadBuffer.readBoolean();
        src = packetReadBuffer.readString();
    }
}
