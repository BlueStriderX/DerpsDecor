package thederpgamer.decor.network.server;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.projector.HoloProjectorDrawData;
import thederpgamer.decor.data.projector.TextProjectorDrawData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.modules.HoloProjectorModule;
import thederpgamer.decor.modules.TextProjectorModule;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/12/2021
 */
public class UpdateProjectorDataPacket extends Packet {

    private ManagedUsableSegmentController<?> segmentController;
    private long indexAndOrientation;
    private int type;

    public UpdateProjectorDataPacket() {

    }

    public UpdateProjectorDataPacket(ManagedUsableSegmentController<?> segmentController, long indexAndOrientation, int type) {
        this.segmentController = segmentController;
        this.indexAndOrientation = indexAndOrientation;
        this.type = type;
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        segmentController = (ManagedUsableSegmentController<?>) packetReadBuffer.readSendable();
        indexAndOrientation = packetReadBuffer.readLong();
        type = packetReadBuffer.readInt();
        if(type == DerpsDecor.HOLO_PROJECTOR) {
            HoloProjectorModule module = (HoloProjectorModule) segmentController.getManagerContainer().getModMCModule(ElementManager.getBlock("Holo Projector").getId());
            HoloProjectorDrawData drawData = (HoloProjectorDrawData) module.getDrawData(indexAndOrientation);
            drawData.onTagDeserialize(packetReadBuffer);
        } else if(type == DerpsDecor.TEXT_PROJECTOR) {
            TextProjectorModule module = (TextProjectorModule) segmentController.getManagerContainer().getModMCModule(ElementManager.getBlock("Text Projector").getId());
            TextProjectorDrawData drawData = (TextProjectorDrawData) module.getDrawData(indexAndOrientation);
            drawData.onTagDeserialize(packetReadBuffer);
        }
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeSendable(segmentController);
        packetWriteBuffer.writeLong(indexAndOrientation);
        packetWriteBuffer.writeInt(type);
        if(type == DerpsDecor.HOLO_PROJECTOR) {
            HoloProjectorModule module = (HoloProjectorModule) segmentController.getManagerContainer().getModMCModule(ElementManager.getBlock("Holo Projector").getId());
            HoloProjectorDrawData drawData = (HoloProjectorDrawData) module.getDrawData(indexAndOrientation);
            drawData.onTagSerialize(packetWriteBuffer);
        } else if(type == DerpsDecor.TEXT_PROJECTOR) {
            TextProjectorModule module = (TextProjectorModule) segmentController.getManagerContainer().getModMCModule(ElementManager.getBlock("Text Projector").getId());
            TextProjectorDrawData drawData = (TextProjectorDrawData) module.getDrawData(indexAndOrientation);
            drawData.onTagSerialize(packetWriteBuffer);
        }
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {

    }
}
