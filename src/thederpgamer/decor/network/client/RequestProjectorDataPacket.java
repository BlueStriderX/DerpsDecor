package thederpgamer.decor.network.client;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.decor.network.server.UpdateProjectorDataPacket;
import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/12/2021
 */
public class RequestProjectorDataPacket extends Packet {

    private ManagedUsableSegmentController<?> segmentController;
    private long indexAndOrientation;
    private int type;

    public RequestProjectorDataPacket() {

    }

    public RequestProjectorDataPacket(ManagedUsableSegmentController<?> segmentController, long indexAndOrientation, int type) {
       this.segmentController = segmentController;
       this.indexAndOrientation = indexAndOrientation;
       this.type = type;
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        segmentController = (ManagedUsableSegmentController<?>) packetReadBuffer.readSendable();
        indexAndOrientation = packetReadBuffer.readLong();
        type = packetReadBuffer.readInt();
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeSendable(segmentController);
        packetWriteBuffer.writeLong(indexAndOrientation);
        packetWriteBuffer.writeInt(type);
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        PacketUtil.sendPacket(playerState, new UpdateProjectorDataPacket(segmentController, indexAndOrientation, type));
    }
}
