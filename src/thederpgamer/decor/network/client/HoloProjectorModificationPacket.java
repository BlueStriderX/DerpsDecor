package thederpgamer.decor.network.client;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.decor.data.ProjectorDrawData;
import thederpgamer.decor.utils.DataUtils;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/14/2021
 */
public class HoloProjectorModificationPacket extends Packet {

    public ProjectorDrawData drawData;

    public HoloProjectorModificationPacket() {

    }

    public HoloProjectorModificationPacket(ProjectorDrawData drawData) {
        this.drawData = drawData;
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        drawData = packetReadBuffer.readObject(ProjectorDrawData.class);
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeObject(drawData);
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        DataUtils.modProjector(drawData, playerState.getName());
    }
}
