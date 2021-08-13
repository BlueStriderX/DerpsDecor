package thederpgamer.decor.modules;

import api.common.GameClient;
import api.common.GameCommon;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.module.ModManagerContainerModule;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.projector.ProjectorDrawData;
import thederpgamer.decor.data.projector.TextProjectorDrawData;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.MathUtils;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
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

    public final ConcurrentHashMap<Long, TextProjectorDrawData> projectorMap = new ConcurrentHashMap<>();
    private static final Vector3f coreOffset = new Vector3f(-16.0f, -16.0f, -16.0f);

    public TextProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
        super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Text Projector").getId());
    }

    @Override
    public void handle(Timer timer) {
        if(!((GameCommon.isOnSinglePlayer() && isOnServer()) || (!GameCommon.isOnSinglePlayer() && !isOnServer()))) return;
        for(Map.Entry<Long, TextProjectorDrawData> entry : projectorMap.entrySet()) {
            long indexAndOrientation = entry.getKey();
            long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);
            TextProjectorDrawData drawData = entry.getValue();

            if(!drawData.text.isEmpty()) {
                if(drawData.changed || drawData.textOverlay == null) {
                    GUITextOverlay textOverlay = new GUITextOverlay(30, 10, GameClient.getClientState());
                    textOverlay.onInit();
                    textOverlay.setFont(ResourceManager.getFont("Monda-Bold", drawData.scale * 10, Color.decode("0x" + ((TextProjectorDrawData) drawData).color)));
                    textOverlay.setScale(-drawData.scale / 100.0f, -drawData.scale / 100.0f, -drawData.scale / 100.0f);
                    textOverlay.setTextSimple(drawData.text);
                    textOverlay.setBlend(true);
                    textOverlay.doDepthTest = true;
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
        /*
        if(!GameCommon.isOnSinglePlayer() && !isOnServer()) {
            PacketUtil.sendPacketToServer(new RequestProjectorDataPacket((ManagedUsableSegmentController<?>) getManagerContainer().getSegmentController(), abs, DerpsDecor.TEXT_PROJECTOR));
        }

         */
    }

    @Override
    public void onReceiveDataServer(PacketReadBuffer packetReadBuffer) throws IOException {
        onTagDeserialize(packetReadBuffer);
        syncToNearbyClients();
    }

    @Override
    public void updateToServer() {
        try {
            PacketWriteBuffer packetWriteBuffer = openCSBuffer();
            onTagSerialize(packetWriteBuffer);
            sendBufferToServer();
        } catch(IOException exception) {
            LogManager.logException("Something went wrong while trying to send text projector data to server", exception);
        }
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        try {
            if(!projectorMap.isEmpty()) {
                packetWriteBuffer.writeInt(projectorMap.size());
                for(Map.Entry<Long, TextProjectorDrawData> entry : projectorMap.entrySet()) {
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
                        TextProjectorDrawData drawData = projectorMap.get(indexAndOrientation);
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
        return "TextProjector";
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
    public ProjectorDrawData getDrawData(SegmentPiece segmentPiece) {
        return getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
    }

    @Override
    public void setDrawData(long indexAndOrientation, ProjectorDrawData drawData) {
        projectorMap.remove(indexAndOrientation);
        projectorMap.put(indexAndOrientation, (TextProjectorDrawData) drawData);
        updateToServer();
    }

    private boolean canDraw(SegmentPiece segmentPiece) {
        if(segmentPiece.getSegmentController().isFullyLoaded() && segmentPiece.getSegmentController().getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex())) {
            short type = segmentPiece.getSegmentController().getSegmentBuffer().getPointUnsave(segmentPiece.getAbsoluteIndex()).getType();
            return type == ElementManager.getBlock("Text Projector").getId();
        }
        return false;
    }

    private TextProjectorDrawData createNewDrawData(long indexAndOrientation) {
        long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
        SegmentPiece segmentPiece = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(absIndex);
        TextProjectorDrawData drawData = new TextProjectorDrawData(segmentPiece);
        drawData.indexAndOrientation = indexAndOrientation;
        projectorMap.put(indexAndOrientation, drawData);
        updateToServer();
        return drawData;
    }

    private ProjectorDrawer getProjectorDrawer() {
        return DerpsDecor.getInstance().projectorDrawer;
    }
}
