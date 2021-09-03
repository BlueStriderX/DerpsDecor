package thederpgamer.decor.modules;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.module.ModManagerContainerModule;
import com.bulletphysics.linearmath.QuaternionUtil;
import org.schema.game.common.controller.SegmentBufferInterface;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.drawdata.ProjectorDrawData;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.utils.MathUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/05/2021
 */
public class HoloProjectorModule extends ModManagerContainerModule implements ProjectorInterface {

    public final ConcurrentHashMap<Long, HoloProjectorDrawData> projectorMap = new ConcurrentHashMap<>();

    public HoloProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
        super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Holo Projector").getId());
    }

    @Override
    public void handle(Timer timer) {
        if(isOnServer()) return;
        for(Map.Entry<Long, HoloProjectorDrawData> entry : projectorMap.entrySet()) {
            long indexAndOrientation = entry.getKey();
            long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);
            HoloProjectorDrawData drawData = entry.getValue();

            if(!drawData.src.isEmpty()) {
                if(drawData.changed || drawData.image == null) {
                    Sprite image = ImageManager.getImage(drawData.src);
                    if(image != null) drawData.image = image;
                }

                if(segmentController.getSegmentBuffer().existsPointUnsave(index)) {
                    SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(index);
                    if(canDraw(segmentPiece) && !segmentPiece.isActive()) {
                        if(drawData.changed || drawData.transform == null || drawData.transform.origin.length() <= 0) {
                            drawData.transform = SegmentPieceUtils.getFullPieceTransform(segmentPiece);
                            Quat4f currentRot = new Quat4f();
                            drawData.transform.getRotation(currentRot);
                            Quat4f addRot = new Quat4f();
                            QuaternionUtil.setEuler(addRot, drawData.rotation.x / 100.0f, drawData.rotation.y / 100.0f, drawData.rotation.z / 100.0f);
                            currentRot.mul(addRot);
                            MathUtils.roundQuat(currentRot);
                            drawData.transform.setRotation(currentRot);
                            drawData.transform.origin.add(new Vector3f(drawData.offset.toVector3f()));
                            MathUtils.roundVector(drawData.transform.origin);
                            drawData.changed = false;
                        }
                        getProjectorDrawer().addDraw(segmentPiece, drawData);
                    }
                }
            }
        }
    }

    @Override
    public void handlePlace(long abs, byte orientation) {
        super.handlePlace(abs, orientation);
        createNewDrawData(abs);
    }

    @Override
    public void handleRemove(long abs) {
        super.handleRemove(abs);
        projectorMap.remove(abs);
    }

    @Override
    public void onReceiveDataServer(PacketReadBuffer packetReadBuffer) throws IOException {
        if(!isOnServer()) return;
        onTagDeserialize(packetReadBuffer);
        syncToNearbyClients();
    }

    @Override
    public void updateToServer() {
        if(isOnServer()) return;
        try {
            PacketWriteBuffer packetWriteBuffer = openCSBuffer();
            onTagSerialize(packetWriteBuffer);
            sendBufferToServer();
        } catch(IOException exception) {
            LogManager.logException("Something went wrong while trying to send holo projector data to server", exception);
        }
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        try {
            removeInvalidEntries();
            if(!projectorMap.isEmpty()) {
                packetWriteBuffer.writeInt(projectorMap.size());
                for(Map.Entry<Long, HoloProjectorDrawData> entry : projectorMap.entrySet()) {
                    try {
                        entry.getValue().onTagSerialize(packetWriteBuffer);
                    } catch(Exception exception1) {
                        LogManager.logException("Something went wrong while trying to serialize holo projector data", exception1);
                    }
                }
            } else packetWriteBuffer.writeInt(0);
        } catch(Exception exception2) {
            exception2.printStackTrace();
        }
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        SegmentBufferInterface segmentBuffer = getManagerContainer().getSegmentController().getSegmentBuffer();
        int size = packetReadBuffer.readInt();
        for(int i = 0; i < size; i ++) {
            long indexAndOrientation = packetReadBuffer.readLong();
            long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
            SegmentPiece projectorPiece = segmentBuffer.getPointUnsave(absIndex);
            if(projectorPiece != null && projectorPiece.getType() == getBlockId()) {
                try {
                    HoloProjectorDrawData drawData = new HoloProjectorDrawData(packetReadBuffer);
                    drawData.indexAndOrientation = indexAndOrientation;
                    projectorMap.put(indexAndOrientation, drawData);
                    continue;
                } catch(Exception exception) {
                    LogManager.logException("Something went wrong while trying to deserialize holo projector data", exception);
                }
            }
            size--; //Skip invalid entry
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
        return "HoloProjector";
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
    public ProjectorDrawData getDrawData(SegmentPiece segmentPiece) {
        return getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
    }

    @Override
    public void setDrawData(long indexAndOrientation, ProjectorDrawData drawData) {
        projectorMap.remove(indexAndOrientation);
        projectorMap.put(indexAndOrientation, (HoloProjectorDrawData) drawData);
        updateToServer();
    }

    private void removeInvalidEntries() {
        short projectorId = getProjectorId();
        ArrayList<Long> toRemove = new ArrayList<>();
        for(Long absIndex : blocks.keySet()) {
            if(!segmentController.getSegmentBuffer().existsPointUnsave(absIndex) || segmentController.getSegmentBuffer().getPointUnsave(absIndex).getType() != projectorId) toRemove.add(absIndex);
        }

        for(Long entry : toRemove) {
            projectorMap.remove(ElementCollection.getPosIndexFrom4(entry));
            blocks.remove(entry);
        }
    }

    private boolean canDraw(SegmentPiece segmentPiece) {
        if(segmentPiece.getSegmentController().isFullyLoaded() && segmentPiece.getSegmentController().getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex())) {
            short type = segmentPiece.getSegmentController().getSegmentBuffer().getPointUnsave(segmentPiece.getAbsoluteIndex()).getType();
            return type == ElementManager.getBlock("Holo Projector").getId();
        }
        return false;
    }


    private HoloProjectorDrawData createNewDrawData(long indexAndOrientation) {
        long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
        SegmentPiece segmentPiece = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(absIndex);
        HoloProjectorDrawData drawData = new HoloProjectorDrawData(segmentPiece);
        drawData.indexAndOrientation = indexAndOrientation;
        projectorMap.put(indexAndOrientation, drawData);
        updateToServer();
        return drawData;
    }

    private ProjectorDrawer getProjectorDrawer() {
        return GlobalDrawManager.getProjectorDrawer();
    }
}
