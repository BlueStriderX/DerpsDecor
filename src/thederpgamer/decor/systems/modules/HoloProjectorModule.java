package thederpgamer.decor.systems.modules;

import api.utils.game.module.util.SimpleDataStorageMCModule;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawMap;
import thederpgamer.decor.data.graphics.image.ScalableImageSubSprite;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.utils.MathUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/05/2021
 */
public class HoloProjectorModule extends SimpleDataStorageMCModule {

    public HoloProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
        super(
                ship,
                managerContainer,
                DerpsDecor.getInstance(),
                ElementManager.getBlock("Holo Projector").getId());
        if (!(data instanceof HoloProjectorDrawMap)) data = new HoloProjectorDrawMap();
    }

    @Override
    public void handle(Timer timer) {
        if (isOnServer()) return;
        for (Object obj : getProjectorMap().values()) {
            HoloProjectorDrawData drawData = (HoloProjectorDrawData) obj;
            long indexAndOrientation = drawData.indexAndOrientation;
            long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);

            if (drawData.src != null && !drawData.src.isEmpty()) {
                if (drawData.changed
                    || (drawData.image == null && !drawData.src.endsWith(".gif"))
                    || (drawData.frames == null && drawData.src.endsWith(".gif"))) {
                    if (drawData.src.endsWith(".gif"))
                        drawData.frames = ImageManager.getAnimatedImage(drawData.src);
                    else drawData.image = ImageManager.getImage(drawData.src);
                    drawData.changed = false;
                }

                if (segmentController.getSegmentBuffer().existsPointUnsave(index)) {
                    SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(index);
                    if (canDraw(segmentPiece) && !segmentPiece.isActive()) {
                        if (drawData.changed
                            || drawData.transform == null
                            || drawData.transform.origin.length() <= 0
                            || drawData.subSprite == null) {
                            if (drawData.image != null) {
                                float maxDim = Math.max(drawData.image.getWidth(), drawData.image.getHeight());
                                if (drawData.transform == null) drawData.transform = new Transform();
                                SegmentPieceUtils.getProjectorTransform(
                                        segmentPiece, drawData.offset, drawData.rotation, drawData.transform);
                                Quat4f currentRot = new Quat4f();
                                drawData.transform.getRotation(currentRot);
                                Quat4f addRot = new Quat4f();
                                QuaternionUtil.setEuler(
                                        addRot,
                                        drawData.rotation.x / 100.0f,
                                        drawData.rotation.y / 100.0f,
                                        drawData.rotation.z / 100.0f);
                                currentRot.mul(addRot);
                                MathUtils.roundQuat(currentRot);
                                drawData.transform.setRotation(currentRot);
                                drawData.transform.origin.add(new Vector3f(drawData.offset.toVector3f()));
                                MathUtils.roundVector(drawData.transform.origin);
                                drawData.subSprite =
                                        new ScalableImageSubSprite[] {
                                                new ScalableImageSubSprite(
                                                        ((float) drawData.scale / (maxDim * 5)) * -1, drawData.transform)
                                        };
                                drawData.changed = false;
                                getProjectorDrawer().addDraw(segmentPiece, drawData);
                            }
                        }
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

    public ConcurrentHashMap<Long, HoloProjectorDrawData> getProjectorMap() {
        if(!(data instanceof HoloProjectorDrawMap)) data = new HoloProjectorDrawMap();
        if(((HoloProjectorDrawMap) data).map == null) ((HoloProjectorDrawMap) data).map = new ConcurrentHashMap<>();
        return ((HoloProjectorDrawMap) data).map;
    }

    public short getProjectorId() {
        return ElementManager.getBlock("Holo Projector").getId();
    }

    public void removeDrawData(long indexAndOrientation) {
        getProjectorMap().remove(indexAndOrientation);
    }

    public Object getDrawData(long indexAndOrientation) {
        if(getProjectorMap().containsKey(indexAndOrientation)) return getProjectorMap().get(indexAndOrientation);
        return createNewDrawData(indexAndOrientation);
    }

    public Object getDrawData(SegmentPiece segmentPiece) {
        return getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
    }

    public void setDrawData(long indexAndOrientation, HoloProjectorDrawData drawData) {
        removeDrawData(indexAndOrientation);
        getProjectorMap().put(indexAndOrientation, drawData);
        flagUpdatedData();
    }

    private boolean canDraw(SegmentPiece segmentPiece) {
        if (segmentPiece.getSegmentController().isFullyLoaded() && segmentPiece.getSegmentController().getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex())) {
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
        getProjectorMap().put(indexAndOrientation, drawData);
        flagUpdatedData();
        return drawData;
    }

    private ProjectorDrawer getProjectorDrawer() {
        return GlobalDrawManager.getProjectorDrawer();
    }

    public void resetAllProjectors() {
        try {
            getProjectorMap().clear();
            flagUpdatedData();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
