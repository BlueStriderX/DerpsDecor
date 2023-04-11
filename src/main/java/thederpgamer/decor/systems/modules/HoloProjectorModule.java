package thederpgamer.decor.systems.modules;

import api.utils.game.module.util.SimpleDataStorageMCModule;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.graphics.image.ScalableImageSubSprite;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.utils.MathUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/05/2021
 */
public class HoloProjectorModule extends SimpleDataStorageMCModule {
	public HoloProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
		super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Holo Projector").getId());
	}

	@Override
	public void handle(Timer timer) {
		if(isOnServer()) return;
		HashMap<Long, HoloProjectorDrawData> drawDataMap = getProjectorMap();
		for(HoloProjectorDrawData obj : drawDataMap.values()) {
			long indexAndOrientation = obj.indexAndOrientation;
			long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);
			if(obj.src != null && !obj.src.isEmpty()) {
				if(obj.changed || obj.getCurrentFrame() == null) {
					if(!obj.src.endsWith(".gif")) obj.image = ImageManager.getImage(obj.src);
					obj.changed = false;
				}
				if(segmentController.getSegmentBuffer().existsPointUnsave(index)) {
					SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(index);
					if(segmentPiece.getType() != getProjectorId()) {
						drawDataMap.remove(indexAndOrientation);
						continue;
					}
					if(canDraw(segmentPiece)) {
						if(obj.changed || obj.transform == null || obj.transform.origin.length() <= 0 || obj.subSprite == null) {
							if(obj.getCurrentFrame() != null) {
								float maxDim = Math.max(obj.image.getWidth(), obj.image.getHeight());
								if(obj.transform == null) obj.transform = new Transform();
								SegmentPieceUtils.getProjectorTransform(segmentPiece, obj.offset, obj.rotation, obj.transform);
								Quat4f currentRot = new Quat4f();
								obj.transform.getRotation(currentRot);
								Quat4f addRot = new Quat4f();
								QuaternionUtil.setEuler(addRot, obj.rotation.x / 100.0f, obj.rotation.y / 100.0f, obj.rotation.z / 100.0f);
								currentRot.mul(addRot);
								MathUtils.roundQuat(currentRot);
								obj.transform.setRotation(currentRot);
								obj.transform.origin.add(new Vector3f(obj.offset.toVector3f()));
								MathUtils.roundVector(obj.transform.origin);
								obj.subSprite = new ScalableImageSubSprite[] {new ScalableImageSubSprite(((float) obj.scale / (maxDim * 5)) * -1, obj.transform)};
								obj.changed = false;
							}
						}
					}
				}
			}
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

	public HashMap<Long, HoloProjectorDrawData> getProjectorMap() {
		if(!(data instanceof HoloProjectorDrawMap)) data = new HoloProjectorDrawMap();
		return ((HoloProjectorDrawMap) data).map;
	}

	private boolean canDraw(SegmentPiece segmentPiece) {
		boolean canToggle = false;
		SegmentController segmentController = segmentPiece.getSegmentController();
		SegmentPiece activator = SegmentPieceUtils.getFirstMatchingAdjacent(segmentPiece, ElementKeyMap.ACTIVAION_BLOCK_ID);
		if(activator != null) {
			ArrayList<SegmentPiece> controlling = SegmentPieceUtils.getControlledPiecesMatching(activator, segmentPiece.getType());
			if(!controlling.isEmpty()) {
				for(SegmentPiece controlled : controlling) {
					if(controlled.equals(segmentPiece)) {
						canToggle = true;
						break;
					}
				}
			}
		}
		return segmentController.getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex()) && segmentController.getSegmentBuffer().getPointUnsave(segmentPiece.getAbsoluteIndex()).getType() == segmentPiece.getType() && segmentController.isFullyLoadedWithDock() && segmentController.isInClientRange() && ((canToggle && activator.isActive()) || activator == null);
	}

	@Override
	public void handleRemove(long abs) {
		super.handleRemove(abs);
		removeDrawData(abs);
		flagUpdatedData();
	}

	public void removeDrawData(long indexAndOrientation) {
		getProjectorMap().remove(indexAndOrientation);
	}

	@Override
	public String getName() {
		return "HoloProjector_ManagerModule";
	}

	public short getProjectorId() {
		return ElementManager.getBlock("Holo Projector").getId();
	}

	public HoloProjectorDrawData getDrawData(SegmentPiece segmentPiece) {
		return getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
	}

	public HoloProjectorDrawData getDrawData(long indexAndOrientation) {
		if(getProjectorMap().containsKey(indexAndOrientation)) return getProjectorMap().get(indexAndOrientation);
		return createNewDrawData(indexAndOrientation);
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

	public void setDrawData(long indexAndOrientation, HoloProjectorDrawData drawData) {
		removeDrawData(indexAndOrientation);
		getProjectorMap().put(indexAndOrientation, drawData);
		flagUpdatedData();
	}

	private ProjectorDrawer getProjectorDrawer() {
		return GlobalDrawManager.getProjectorDrawer();
	}

	public void resetAllProjectors() {
		try {
			getProjectorMap().clear();
			flagUpdatedData();
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	private static class HoloProjectorDrawMap {
		public HashMap<Long, HoloProjectorDrawData> map;

		public HoloProjectorDrawMap() {
			map = new HashMap<>();
		}
	}
}
