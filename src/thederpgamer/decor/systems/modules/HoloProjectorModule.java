package thederpgamer.decor.systems.modules;

import api.utils.game.module.util.SimpleDataStorageMCModule;
import com.bulletphysics.linearmath.QuaternionUtil;
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
import thederpgamer.decor.utils.MathUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/05/2021
 */
public class HoloProjectorModule extends SimpleDataStorageMCModule implements ProjectorInterface {

    public HoloProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
        super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Holo Projector").getId());
        if(!(data instanceof ArrayList)) data = new ArrayList<>();
    }

    @Override
    public void handle(Timer timer) {
        if(isOnServer()) return;
        for(ProjectorDrawData projectorData : getProjectorList()) {
            long indexAndOrientation = projectorData.getIndexAndOrientation();
            long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);
            HoloProjectorDrawData drawData = (HoloProjectorDrawData) projectorData;

            if(drawData.src != null) {
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
        return "HoloProjector_ManagerModule";
    }

    @Override
    public ArrayList<ProjectorDrawData> getProjectorList() {
        return (ArrayList<ProjectorDrawData>) data;
    }

    @Override
    public short getProjectorId() {
        return ElementManager.getBlock("Holo Projector").getId();
    }

    @Override
    public void removeDrawData(long indexAndOrientation) {
        ArrayList<ProjectorDrawData> toRemove = new ArrayList<>();
        for(ProjectorDrawData projectorData : getProjectorList()) {
            if(projectorData.getIndexAndOrientation() == indexAndOrientation) toRemove.add(projectorData);
        }
        for(ProjectorDrawData projectorData : toRemove) getProjectorList().remove(projectorData);
    }

    @Override
    public ProjectorDrawData getDrawData(long indexAndOrientation) {
        for(ProjectorDrawData drawData : getProjectorList()) if(drawData.getIndexAndOrientation() == indexAndOrientation) return drawData;
        return createNewDrawData(indexAndOrientation);
    }

    @Override
    public ProjectorDrawData getDrawData(SegmentPiece segmentPiece) {
        return getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
    }

    @Override
    public void setDrawData(long indexAndOrientation, ProjectorDrawData drawData) {
        removeDrawData(indexAndOrientation);
        getProjectorList().add(drawData);
        flagUpdatedData();
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
        getProjectorList().add(drawData);
        flagUpdatedData();
        return drawData;
    }

    private ProjectorDrawer getProjectorDrawer() {
        return GlobalDrawManager.getProjectorDrawer();
    }
}
