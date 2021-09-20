package thederpgamer.decor.systems.modules;

import api.common.GameClient;
import api.utils.game.module.util.SimpleDataStorageMCModule;
import com.bulletphysics.linearmath.QuaternionUtil;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.DrawDataMap;
import thederpgamer.decor.data.drawdata.ProjectorDrawData;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.MathUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/18/2021
 */
public class TextProjectorModule extends SimpleDataStorageMCModule implements ProjectorInterface {

    public TextProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
        super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Text Projector").getId());
        if(!(data instanceof DrawDataMap)) data = new DrawDataMap();
    }

    @Override
    public void handle(Timer timer) {
        if(isOnServer()) return;
        for(ProjectorDrawData projectorData : getProjectorMap().values()) {
            long indexAndOrientation = projectorData.getIndexAndOrientation();
            long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);
            TextProjectorDrawData drawData = (TextProjectorDrawData) projectorData;

            if(drawData.text != null && drawData.color != null) {
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
    public void handleRemove(long abs) {
        super.handleRemove(abs);
        removeDrawData(abs);
        flagUpdatedData();
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
        return "TextProjector_ManagerModule";
    }

    @Override
    public ConcurrentHashMap<Long, ProjectorDrawData> getProjectorMap() {
        if(!(data instanceof DrawDataMap)) data = new DrawDataMap();
        if(((DrawDataMap) data).map == null) ((DrawDataMap) data).map = new ConcurrentHashMap<>();
        return ((DrawDataMap) data).map;
    }

    @Override
    public short getProjectorId() {
        return ElementManager.getBlock("Text Projector").getId();
    }

    @Override
    public void removeDrawData(long indexAndOrientation) {
        getProjectorMap().remove(indexAndOrientation);
    }

    @Override
    public ProjectorDrawData getDrawData(long indexAndOrientation) {
        if(getProjectorMap().containsKey(indexAndOrientation)) return getProjectorMap().get(indexAndOrientation);
        return createNewDrawData(indexAndOrientation);
    }

    @Override
    public ProjectorDrawData getDrawData(SegmentPiece segmentPiece) {
        return getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
    }

    @Override
    public void setDrawData(long indexAndOrientation, ProjectorDrawData drawData) {
        removeDrawData(indexAndOrientation);
        getProjectorMap().put(indexAndOrientation, drawData);
        flagUpdatedData();
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
        getProjectorMap().put(indexAndOrientation, drawData);
        flagUpdatedData();
        return drawData;
    }

    private ProjectorDrawer getProjectorDrawer() {
        return GlobalDrawManager.getProjectorDrawer();
    }
}
