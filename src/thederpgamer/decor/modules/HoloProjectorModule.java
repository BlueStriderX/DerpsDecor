package thederpgamer.decor.modules;

import api.common.GameCommon;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import api.utils.game.module.ModManagerContainerModule;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.projector.HoloProjectorDrawData;
import thederpgamer.decor.data.projector.ProjectorDrawData;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.network.client.RequestProjectorDataPacket;
import thederpgamer.decor.utils.MathUtils;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
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

    public final ConcurrentHashMap<Long, HoloProjectorDrawData> projectorMap = new ConcurrentHashMap<>();
    private static final Vector3f coreOffset = new Vector3f(-16.0f, -16.0f, -16.0f);

    public HoloProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
        super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Holo Projector").getId());
    }

    @Override
    public void handle(Timer timer) {
        if(!((GameCommon.isOnSinglePlayer() && isOnServer()) || (!GameCommon.isOnSinglePlayer() && !isOnServer()))) return;
        for(Map.Entry<Long, HoloProjectorDrawData> entry : projectorMap.entrySet()) {
            long indexAndOrientation = entry.getKey();
            long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);
            HoloProjectorDrawData drawData = entry.getValue();

            if(!drawData.src.isEmpty()) {
                if(drawData.changed || drawData.image == null || drawData.dimensions == null) {
                    Sprite image = ImageManager.getImage(drawData.src);
                    if(image != null) {
                        drawData.image = image;
                        drawData.dimensions = new float[] {drawData.image.getWidth(), drawData.image.getHeight()};
                    }
                    drawData.changed = false;
                }

                //Get the position relative to the entity's origin (Not the local co-ord in the 32x32x32 chunk)
                Vector3f pos = new Vector3f();
                ElementCollection.getPosFromIndex(index, pos);
                pos.add(coreOffset); //Add the core offset

                //Transform the relative-to-entity position to a relative-to-world position
                segmentController.getWorldTransform().transform(pos);

                SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(index);
                if(canDraw(segmentPiece) && segmentPiece.isActive()) {
                    Transform transform = new Transform(drawData.pieceTransform);
                    Quat4f currentRot = new Quat4f();
                    transform.getRotation(currentRot);
                    Quat4f addRot = new Quat4f();
                    QuaternionUtil.setEuler(addRot, drawData.rotation.x / 100.0f, drawData.rotation.y / 100.0f, drawData.rotation.z / 100.0f);
                    currentRot.mul(addRot);
                    MathUtils.roundQuat(currentRot);
                    transform.setRotation(currentRot);
                    transform.origin.add(new Vector3f(drawData.offset.toVector3f()));
                    MathUtils.roundVector(transform.origin);
                    getProjectorDrawer().drawProjector(drawData, transform);
                    continue;
                }
            }
            projectorMap.remove(indexAndOrientation);
        }
    }

    @Override
    public void handlePlace(long abs, byte orientation) {
        super.handlePlace(abs, orientation);
        projectorMap.remove(abs);
        projectorMap.put(abs, createNewDrawData(abs));
        if(!GameCommon.isOnSinglePlayer() && !isOnServer()) {
            PacketUtil.sendPacketToServer(new RequestProjectorDataPacket((ManagedUsableSegmentController<?>) getManagerContainer().getSegmentController(), abs, DerpsDecor.HOLO_PROJECTOR));
        }
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        try {
            if(!projectorMap.isEmpty()) {
                packetWriteBuffer.writeInt(projectorMap.size());
                for(Map.Entry<Long, HoloProjectorDrawData> entry : projectorMap.entrySet()) {
                    try {
                        entry.getValue().onTagSerialize(packetWriteBuffer);
                    } catch(Exception exception1) {
                        exception1.printStackTrace();
                    }
                }
            } else packetWriteBuffer.writeInt(0);
        } catch(Exception exception2) {
            exception2.printStackTrace();
        }
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        try {
            int count = packetReadBuffer.readInt();
            if(count > 0) {
                while(count > 0) {
                    try {
                        long indexAndOrientation = packetReadBuffer.readLong();
                        HoloProjectorDrawData drawData = projectorMap.get(indexAndOrientation);
                        if(drawData == null) {
                            drawData = createNewDrawData(indexAndOrientation);
                            projectorMap.put(indexAndOrientation, drawData);
                        }
                        drawData.onTagDeserialize(packetReadBuffer);
                    } catch(Exception exception1) {
                        exception1.printStackTrace();
                    }
                    count --;
                }
            }
        } catch(Exception exception2) {
            exception2.printStackTrace();
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
        ProjectorDrawData drawData;
        if(projectorMap.containsKey(indexAndOrientation)) drawData = projectorMap.get(indexAndOrientation);
        else drawData = createNewDrawData(indexAndOrientation);
        return drawData;
    }

    @Override
    public void setDrawData(long indexAndOrientation, ProjectorDrawData drawData) {
        projectorMap.remove(indexAndOrientation);
        projectorMap.put(indexAndOrientation, (HoloProjectorDrawData) drawData);
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
        return new HoloProjectorDrawData(segmentPiece);
    }

    private ProjectorDrawer getProjectorDrawer() {
        return DerpsDecor.getInstance().projectorDrawer;
    }
}
