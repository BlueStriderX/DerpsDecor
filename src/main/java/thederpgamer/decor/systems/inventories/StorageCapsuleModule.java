package thederpgamer.decor.systems.inventories;

import api.utils.game.module.util.SimpleDataStorageMCModule;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.factory.CargoCapacityElementManagerInterface;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.system.storagecapsule.StorageCapsuleData;
import thederpgamer.decor.data.system.storagecapsule.StorageCapsuleSystemData;
import thederpgamer.decor.element.ElementManager;

import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/16/2022]
 */
public class StorageCapsuleModule extends SimpleDataStorageMCModule implements CargoCapacityElementManagerInterface {
	public StorageCapsuleModule(SegmentController segmentController, ManagerContainer<?> managerContainer) {
		super(segmentController, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Storage Capsule").getId());
		initData();
	}

	private void initData() {
		if(!(data instanceof StorageCapsuleSystemData)) data = new StorageCapsuleSystemData();
		if(((StorageCapsuleSystemData) data).map == null) ((StorageCapsuleSystemData) data).map = new HashMap<>();
	}

	@Override
	public void handle(Timer timer) {
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
	public void handleRemove(long abs) {
		super.handleRemove(abs);
		flagUpdatedData();
	}

	@Override
	public String getName() {
		return "StorageCapsuleModule";
	}

	public HashMap<Long, StorageCapsuleData> getData() {
		initData();
		return ((StorageCapsuleSystemData) data).map;
	}
}
