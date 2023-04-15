package thederpgamer.decor.systems.modules;

import api.utils.game.module.util.SimpleDataStorageMCModule;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.HoloTableDrawData;
import thederpgamer.decor.data.graphics.mesh.SystemMesh;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.element.ElementManager;
import api.utils.SegmentPieceUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class HoloTableModule extends SimpleDataStorageMCModule {
	public HoloTableModule(SegmentController segmentController, ManagerContainer<?> managerContainer) {
		super(segmentController, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Holo Table").getId());
	}

	@Override
	public String getName() {
		return "HoloTable_ManagerModule";
	}

	@Override
	public void handle(Timer timer) {
		if(isOnServer()) return;
		HashMap<Long, HoloTableDrawData> drawDataMap = getProjectorMap();
		for(HoloTableDrawData drawData : drawDataMap.values()) {
			if(drawData == null) continue;
			assert drawData.tableIndex != 0;
			if(canDraw(drawData.tableIndex)) {
				if(drawData.systemMesh == null) {
					SegmentPiece table = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(drawData.tableIndex);
					SegmentPiece target = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(drawData.targetIndex);
					drawData.systemMesh = new SystemMesh(table, target);
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

	public HashMap<Long, HoloTableDrawData> getProjectorMap() {
		if(!(data instanceof HoloTableDrawMap)) data = new HoloTableDrawMap();
		return ((HoloTableDrawMap) data).map;
	}

	private boolean canDraw(long index) {
		boolean canToggle = false;
		SegmentController segmentController = getManagerContainer().getSegmentController();
		SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(index);
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

	public void removeDrawData(long indexAndOrientation) {
		getProjectorMap().remove(indexAndOrientation);
	}

	public short getProjectorId() {
		return ElementManager.getBlock("Holo Projector").getId();
	}

	public HoloTableDrawData getDrawData(SegmentPiece segmentPiece) {
		return getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
	}

	public HoloTableDrawData getDrawData(long indexAndOrientation) {
		if(getProjectorMap().containsKey(indexAndOrientation)) return getProjectorMap().get(indexAndOrientation);
		return createNewDrawData(indexAndOrientation);
	}

	private HoloTableDrawData createNewDrawData(long indexAndOrientation) {
		long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
		SegmentPiece segmentPiece = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(absIndex);
		ArrayList<SegmentPiece> segmentPieces = api.utils.SegmentPieceUtils.getControlledPieces(segmentPiece);
		if(segmentPieces.isEmpty() || segmentPieces.get(0) == null) return null;
		HoloTableDrawData drawData = new HoloTableDrawData(segmentPiece, segmentPieces.get(0));
		drawData.tableIndex = indexAndOrientation;
		getProjectorMap().put(indexAndOrientation, drawData);
		flagUpdatedData();
		return drawData;
	}

	public void setDrawData(long indexAndOrientation, HoloTableDrawData drawData) {
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

	private static class HoloTableDrawMap {
		public HashMap<Long, HoloTableDrawData> map;

		public HoloTableDrawMap() {
			map = new HashMap<>();
		}
	}
}
