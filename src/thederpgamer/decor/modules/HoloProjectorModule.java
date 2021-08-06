package thederpgamer.decor.modules;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.module.ModManagerContainerModule;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.projector.HoloProjectorDrawData;
import thederpgamer.decor.data.projector.ProjectorDrawData;
import thederpgamer.decor.element.ElementManager;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/05/2021
 */
public class HoloProjectorModule extends ModManagerContainerModule implements ProjectorInterface {

    public final ConcurrentHashMap<Long, HoloProjectorDrawData> projectorMap;

    public HoloProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
        super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Holo Projector").getId());
        projectorMap = new ConcurrentHashMap<>();
    }

    @Override
    public void handle(Timer timer) {

    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeInt(projectorMap.size());
        for(Map.Entry<Long, HoloProjectorDrawData> entry : projectorMap.entrySet()) {
            HoloProjectorDrawData drawData = entry.getValue();
            packetWriteBuffer.writeLong(drawData.indexAndOrientation);
            packetWriteBuffer.writeString(drawData.src);
            packetWriteBuffer.writeVector(drawData.offset);
            packetWriteBuffer.writeVector(drawData.rotation);
            packetWriteBuffer.writeInt(drawData.scale);
        }
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        int count = packetReadBuffer.readInt();
        while(count >= 0) {
            long indexAndOrientation = packetReadBuffer.readLong();
            String src = packetReadBuffer.readString();
            Vector3i offset = packetReadBuffer.readVector();
            Vector3i rotation = packetReadBuffer.readVector();
            int scale = packetReadBuffer.readInt();
            HoloProjectorDrawData drawData = (HoloProjectorDrawData) getDrawData(indexAndOrientation);
            drawData.indexAndOrientation = indexAndOrientation;
            drawData.src = src;
            drawData.offset = offset;
            drawData.rotation = rotation;
            drawData.scale = scale;
            projectorMap.remove(indexAndOrientation);
            projectorMap.put(indexAndOrientation, drawData);
            count --;
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
        return "Holo Projector";
    }

    @Override
    public short getProjectorId() {
        return ElementManager.getBlock("Holo Projector").getId();
    }

    @Override
    public ProjectorDrawData getDrawData(long indexAndOrientation) {
        if(projectorMap.containsKey(indexAndOrientation)) return projectorMap.get(indexAndOrientation);
        else return createNewDrawData(indexAndOrientation);
    }

    @Override
    public void setDrawData(long indexAndOrientation, ProjectorDrawData drawData) {
        projectorMap.remove(indexAndOrientation);
        projectorMap.put(indexAndOrientation, (HoloProjectorDrawData) drawData);
    }

    private HoloProjectorDrawData createNewDrawData(long indexAndOrientation) {
        long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
        SegmentPiece segmentPiece = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(absIndex);
        HoloProjectorDrawData drawData = new HoloProjectorDrawData(segmentPiece);
        projectorMap.put(indexAndOrientation, drawData);
        return drawData;
    }
}
