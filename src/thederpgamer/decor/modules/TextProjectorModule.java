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
import thederpgamer.decor.data.projector.ProjectorDrawData;
import thederpgamer.decor.data.projector.TextProjectorDrawData;
import thederpgamer.decor.element.ElementManager;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/18/2021
 */
public class TextProjectorModule extends ModManagerContainerModule implements ProjectorInterface {

    public final ConcurrentHashMap<Long, TextProjectorDrawData> projectorMap;

    public TextProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
        super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Text Projector").getId());
        projectorMap = new ConcurrentHashMap<>();
    }

    @Override
    public void handle(Timer timer) {

    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeInt(projectorMap.size());
        for(Map.Entry<Long, TextProjectorDrawData> entry : projectorMap.entrySet()) {
            TextProjectorDrawData drawData = entry.getValue();
            packetWriteBuffer.writeLong(drawData.indexAndOrientation);
            packetWriteBuffer.writeString(drawData.text);
            packetWriteBuffer.writeString(drawData.color);
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
            String text = packetReadBuffer.readString();
            String color = packetReadBuffer.readString();
            Vector3i offset = packetReadBuffer.readVector();
            Vector3i rotation = packetReadBuffer.readVector();
            int scale = packetReadBuffer.readInt();
            TextProjectorDrawData drawData = (TextProjectorDrawData) getDrawData(indexAndOrientation);
            drawData.indexAndOrientation = indexAndOrientation;
            drawData.text = text;
            drawData.color = color;
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
        return "Text Projector";
    }

    @Override
    public short getProjectorId() {
        return ElementManager.getBlock("Text Projector").getId();
    }

    @Override
    public ProjectorDrawData getDrawData(long indexAndOrientation) {
        if(projectorMap.containsKey(indexAndOrientation)) return projectorMap.get(indexAndOrientation);
        else return createNewDrawData(indexAndOrientation);
    }

    @Override
    public void setDrawData(long indexAndOrientation, ProjectorDrawData drawData) {
        projectorMap.remove(indexAndOrientation);
        projectorMap.put(indexAndOrientation, (TextProjectorDrawData) drawData);
    }

    private TextProjectorDrawData createNewDrawData(long indexAndOrientation) {
        long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
        SegmentPiece segmentPiece = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(absIndex);
        TextProjectorDrawData drawData = new TextProjectorDrawData(segmentPiece);
        projectorMap.put(indexAndOrientation, drawData);
        return drawData;
    }
}
