package thederpgamer.decor.modules;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.module.ModManagerContainerModule;
import org.schema.game.common.controller.SegmentBufferInterface;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.StrutDrawData;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.element.ElementManager;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public class StrutConnectorModule extends ModManagerContainerModule {

    public final ConcurrentHashMap<SegmentPiece[], StrutDrawData> blockMap = new ConcurrentHashMap<>();

    public StrutConnectorModule(SegmentController segmentController, ManagerContainer<?> managerContainer) {
        super(segmentController, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Strut Connector").getId());
    }

    @Override
    public void handle(Timer timer) {
        for(Map.Entry<SegmentPiece[], StrutDrawData> entry : blockMap.entrySet()) {
            if(entry.getKey()[0].getSegmentController().isInClientRange() && ! getDrawMap().containsKey(entry.getKey())) {
                getDrawMap().put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeInt(blockMap.size());
        for(StrutDrawData drawData : blockMap.values()) drawData.onTagSerialize(packetWriteBuffer);
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        SegmentBufferInterface segmentBuffer = getManagerContainer().getSegmentController().getSegmentBuffer();
        int size = packetReadBuffer.readInt();
        for(int i = 0; i < size; i ++) {
            StrutDrawData drawData = new StrutDrawData(packetReadBuffer);
            SegmentPiece pieceA = segmentBuffer.getPointUnsave(drawData.pieceAIndex);
            SegmentPiece pieceB = segmentBuffer.getPointUnsave(drawData.pieceBIndex);
            if(pieceA != null && pieceA.getType() == getBlockId() && pieceB != null && pieceB.getType() == getBlockId()) {
                SegmentPiece[] key = new SegmentPiece[] {pieceA, pieceB};
                blockMap.remove(key);
                blockMap.put(key, drawData);
            } else size --; //Skip invalid entry
        }
    }

    @Override
    public double getPowerConsumedPerSecondResting() {
        return 0;
    }

    @Override
    public double getPowerConsumedPerSecondCharging() {
        return 0;
    }

    @Override
    public String getName() {
        return "Strut Connector";
    }

    private ConcurrentHashMap<SegmentPiece[], StrutDrawData> getDrawMap() {
        return GlobalDrawManager.getStrutDrawer().drawMap;
    }

    public int getConnectionCount(SegmentPiece segmentPiece) {
        int count = 0;
        for(SegmentPiece[] segmentPieces : blockMap.keySet()) {
            if(segmentPieces[0].equals(segmentPiece)) count ++;
            if(segmentPieces[1].equals(segmentPiece)) count ++;
        }
        return count;
    }
}
