package thederpgamer.decor.systems.modules;

import api.common.GameCommon;
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
import thederpgamer.decor.manager.LogManager;

import java.io.IOException;
import java.util.ArrayList;
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
            if(GameCommon.isOnSinglePlayer()) { //Dumb network bullshit >:U
                if(entry.getKey()[0].getSegmentController().isFullyLoaded() && ! getDrawMap().containsKey(entry.getKey())) getDrawMap().put(entry.getKey(), entry.getValue());
            } else if(GameCommon.isClientConnectedToServer()) {
                if(entry.getKey()[0].getSegmentController().isInClientRange() && ! getDrawMap().containsKey(entry.getKey())) getDrawMap().put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void handlePlace(long absIndex, byte orientation) {
        super.handlePlace(absIndex, orientation);
    }

    @Override
    public void handleRemove(long absIndex) {
        super.handleRemove(absIndex);
        removeInvalidEntries();
    }


    @Override
    public void onReceiveDataServer(PacketReadBuffer packetReadBuffer) throws IOException {
        if(!isOnServer()) return;
        onTagDeserialize(packetReadBuffer);
        syncToNearbyClients();
    }

    public void removeInvalidEntries() {
        for(Map.Entry<SegmentPiece[], StrutDrawData> entry : blockMap.entrySet()) {
            SegmentPiece pieceA = entry.getKey()[0];
            SegmentPiece pieceB = entry.getKey()[0];
            SegmentBufferInterface segmentBuffer =  pieceA.getSegmentController().getSegmentBuffer();
            if(!segmentBuffer.existsPointUnsave(pieceA.getAbsoluteIndex()) || segmentBuffer.getPointUnsave(pieceA.getAbsoluteIndex()).getType() != getBlockId() || !segmentBuffer.existsPointUnsave(pieceB.getAbsoluteIndex()) || segmentBuffer.getPointUnsave(pieceB.getAbsoluteIndex()).getType() != getBlockId()) {
                blockMap.remove(entry.getKey());
            }
        }
        updateToServer();
    }

    public void updateToServer() {
        if(isOnServer()) return;
        try {
            PacketWriteBuffer packetWriteBuffer = openCSBuffer();
            onTagSerialize(packetWriteBuffer);
            sendBufferToServer();
        } catch(IOException exception) {
            LogManager.logException("Something went wrong while trying to send strut data to server", exception);
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
        return "StrutConnector_ManagerModule";
    }

    private ConcurrentHashMap<SegmentPiece[], StrutDrawData> getDrawMap() {
        return GlobalDrawManager.getStrutDrawer().drawMap;
    }

    public int getConnectionCount(SegmentPiece segmentPiece) {
        return getConnections(segmentPiece).size();
    }

    public ArrayList<SegmentPiece> getConnections(SegmentPiece segmentPiece) {
        ArrayList<SegmentPiece> connections = new ArrayList<>();
        for(SegmentPiece[] segmentPieces : blockMap.keySet()) {
            if(segmentPieces[0].equals(segmentPiece) && !segmentPieces[1].equals(segmentPiece)) connections.add(segmentPieces[1]);
            else if(segmentPieces[1].equals(segmentPiece) && !segmentPieces[0].equals(segmentPiece)) connections.add(segmentPieces[0]);
        }
        return connections;
    }
}
