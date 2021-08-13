package thederpgamer.decor.network.client;

import api.common.GameClient;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.projector.HoloProjectorDrawData;
import thederpgamer.decor.data.projector.ProjectorDrawData;
import thederpgamer.decor.data.projector.TextProjectorDrawData;
import thederpgamer.decor.network.server.UpdateProjectorDataPacket;
import thederpgamer.decor.utils.PlayerUtils;
import thederpgamer.decor.utils.ServerUtils;
import java.io.IOException;
import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/12/2021
 */
public class SendProjectorDataToServerPacket extends Packet {

    private ManagedUsableSegmentController<?> segmentController;
    private ProjectorDrawData drawData;

    public SendProjectorDataToServerPacket() {

    }

    public SendProjectorDataToServerPacket(ManagedUsableSegmentController<?> segmentController, ProjectorDrawData drawData) {
        this.segmentController = segmentController;
        this.drawData = drawData;
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        segmentController = (ManagedUsableSegmentController<?>) packetReadBuffer.readSendable();
        int type = packetReadBuffer.readInt();
        switch(type) {
            case DerpsDecor.HOLO_PROJECTOR:
                drawData = new HoloProjectorDrawData(packetReadBuffer);
                break;
            case DerpsDecor.TEXT_PROJECTOR:
                drawData = new TextProjectorDrawData(packetReadBuffer);
                break;
        }
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeSendable(segmentController);
        packetWriteBuffer.writeInt(getType(drawData));
        drawData.onTagSerialize(packetWriteBuffer);
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        UpdateProjectorDataPacket packet = new UpdateProjectorDataPacket(segmentController, drawData);
        HashMap<PlayerState, Float> playerMap = PlayerUtils.getPlayersInRange(PlayerUtils.getPlayerTransform(GameClient.getClientPlayerState()), ServerUtils.getSectorSize());
        for(PlayerState player : playerMap.keySet()) if(playerState != player) PacketUtil.sendPacket(player, packet);
    }

    private int getType(ProjectorDrawData drawData) {
        if(drawData instanceof HoloProjectorDrawData) return DerpsDecor.HOLO_PROJECTOR;
        else if(drawData instanceof TextProjectorDrawData) return DerpsDecor.TEXT_PROJECTOR;
        else return 0;
    }
}
