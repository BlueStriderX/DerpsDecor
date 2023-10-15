package thederpgamer.decor.systems.modules;

import api.utils.game.module.util.SimpleDataStorageMCModule;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.system.crew.CrewData;
import thederpgamer.decor.data.system.crew.CrewModuleData;
import thederpgamer.decor.element.ElementManager;

import java.util.Objects;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class CrewStationModule extends SimpleDataStorageMCModule {
	public CrewStationModule(SegmentController entity, ManagerContainer<?> managerContainer) {
		super(entity, managerContainer, DerpsDecor.getInstance(), Objects.requireNonNull(ElementManager.getBlock("NPC Station")).getId());
	}

	@Override
	public String getName() {
		return "CrewStation_ManagerModule";
	}

	@Override
	public void handle(Timer timer) {
		getCrewModuleData().handleUpdates();
	}

	@Override
	public void handleRemove(long abs) {
		super.handleRemove(abs);
		if(isOnServer()) {
			removeCrewBlock(abs);
			flagUpdatedData();
		}
	}

	public CrewModuleData getCrewModuleData() {
		if(!(data instanceof CrewModuleData)) data = new CrewModuleData();
		return (CrewModuleData) data;
	}

	public CrewData getData(SegmentPiece segmentPiece) {
		return getData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
	}

	public CrewData getData(long indexAndOrientation) {
		if(getCrewModuleData().indexExists(indexAndOrientation)) return getCrewModuleData().getData(indexAndOrientation);
		return createNewData(indexAndOrientation);
	}

	private CrewData createNewData(long indexAndOrientation) {
		long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
		SegmentPiece segmentPiece = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(absIndex);
		CrewData data = new CrewData(segmentPiece);
		data.indexAndOrientation = indexAndOrientation;
		getCrewModuleData().putData(indexAndOrientation, data);
		flagUpdatedData();
		return data;
	}
	
	public void setCrewBlock(long indexAndOrientation, CrewData data) {
		getCrewModuleData().putData(indexAndOrientation, data);
		data.needsUpdate = true;
		flagUpdatedData();
	}

	public void removeCrewBlock(long indexAndOrientation) {
		getCrewModuleData().removeBlock(indexAndOrientation);
	}

	public void resetAllCrewBlocks() {
		try {
			getCrewModuleData().resetAll();
			flagUpdatedData();
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
}
