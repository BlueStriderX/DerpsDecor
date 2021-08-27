package thederpgamer.decor.modules;

import api.common.GameClient;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.module.ModManagerContainerModule;
import com.bulletphysics.linearmath.QuaternionUtil;
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
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
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

    public TextProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
        super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Text Projector").getId());
    }

    @Override
    public void handle(Timer timer) {
        if(isOnServer()) return;
        for(Map.Entry<Long, TextProjectorDrawData> entry : projectorMap.entrySet()) {
            long indexAndOrientation = entry.getKey();
            long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);
            TextProjectorDrawData drawData = entry.getValue();

            if(!drawData.text.isEmpty()) {
                if(drawData.changed || drawData.textOverlay == null) {
                    GUITextOverlay textOverlay = new GUITextOverlay(30, 10, GameClient.getClientState());
                    textOverlay.onInit();
                    int trueSize = drawData.scale + 10;
                    textOverlay.setFont(ResourceManager.getFont("Monda-Bold", trueSize, Color.decode("0x" + drawData.color)));
                    textOverlay.setScale(-trueSize / 1000.0f, -trueSize / 1000.0f, -trueSize / 1000.0f);
                    textOverlay.setTextSimple(drawData.text);
                    textOverlay.setBlend(true);
                    textOverlay.doDepthTest = true;
                    drawData.textOverlay = textOverlay;
                }

                if(segmentController.getSegmentBuffer().existsPointUnsave(index)) {
                    SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(index);
                    if(canDraw(segmentPiece) && !segmentPiece.isActive()) {
                        if(drawData.changed || drawData.transform == null || drawData.transform.origin.length() <= 0) {
                            drawData.transform = SegmentPieceUtils.getFullPieceTransform(segmentPiece, drawData);
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
            LogManager.logException("Something went wrong while trying to send text projector data to server", exception);
        }
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        try {
            removeInvalidEntries();
            if(!projectorMap.isEmpty()) {
                packetWriteBuffer.writeInt(getSize());
                for(Map.Entry<Long, TextProjectorDrawData> entry : projectorMap.entrySet()) {
                    try {
                        entry.getValue().onTagSerialize(packetWriteBuffer);
                    } catch(Exception exception1) {
                        LogManager.logException("Something went wrong while trying to serialize text projector data", exception1);
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
                        TextProjectorDrawData drawData = new TextProjectorDrawData(packetReadBuffer);
                        drawData.indexAndOrientation = indexAndOrientation;
                        projectorMap.put(indexAndOrientation, drawData);
                    } catch(Exception exception1) {
                        LogManager.logException("Something went wrong while trying to deserialize text projector data", exception1);
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
        return projectorMap.get(indexAndOrientation);
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
