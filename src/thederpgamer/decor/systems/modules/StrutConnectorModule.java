package thederpgamer.decor.systems.modules;

import api.utils.game.module.util.SimpleDataStorageMCModule;
import org.apache.commons.lang3.tuple.MutablePair;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.system.strut.StrutData;
import thederpgamer.decor.data.system.strut.StrutSystemData;
import thederpgamer.decor.element.ElementManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public class StrutConnectorModule extends SimpleDataStorageMCModule {

	public StrutConnectorModule(SegmentController segmentController, ManagerContainer<?> managerContainer) {
		super(segmentController, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Strut Connector").getId());
		initData();
	}

	private void initData() {
		if(!(data instanceof StrutSystemData)) data = new StrutSystemData();
		if(((StrutSystemData) data).map == null) ((StrutSystemData) data).map = new ConcurrentHashMap<>();
	}

	public ConcurrentHashMap<MutablePair<Long, Long>, StrutData> getData() {
		initData();
		return ((StrutSystemData) data).map;
	}

	@Override
	public void handle(Timer timer) {

	}

	@Override
	public void handlePlace(long absIndex, byte orientation) {
		super.handlePlace(absIndex, orientation);
		updateEntries();
	}

	@Override
	public void handleRemove(long absIndex) {
		super.handleRemove(absIndex);
		updateEntries();
	}

	public void updateEntries() {
		for(Map.Entry<MutablePair<Long, Long>, StrutData> entry : getData().entrySet()) {
			SegmentPiece pieceA = segmentController.getSegmentBuffer().getPointUnsave(entry.getKey().getLeft());
			SegmentPiece pieceB = segmentController.getSegmentBuffer().getPointUnsave(entry.getKey().getRight());
			if(pieceA == null || pieceB == null || pieceA.equals(pieceB) || pieceA.getType() != getBlockId() || pieceB.getType() != getBlockId()) getData().remove(entry.getKey());
			//else GlobalDrawManager.getStrutDrawer().drawMap.putIfAbsent(new MutablePair<>(pieceA, pieceB), entry.getValue());
		}
	}

	@Override
	public String getName() {
		return "StrutModule";
	}

	public int getConnectionCount(SegmentPiece segmentPiece) {
		return getConnections(segmentPiece).size();
	}

	public ArrayList<SegmentPiece> getConnections(SegmentPiece segmentPiece) {
		ArrayList<SegmentPiece> connections = new ArrayList<>();
		for(MutablePair<Long, Long> pair : getData().keySet()) {
			SegmentPiece pieceA = segmentController.getSegmentBuffer().getPointUnsave(pair.getLeft());
			SegmentPiece pieceB = segmentController.getSegmentBuffer().getPointUnsave(pair.getRight());
			if(pieceA != null && pieceB != null && pieceA != pieceB) {
				if(segmentPiece.equals(pieceA)) connections.add(pieceB);
				else if(segmentPiece.equals(pieceB)) connections.add(pieceA);
			}
		}
		return connections;
	}
}
