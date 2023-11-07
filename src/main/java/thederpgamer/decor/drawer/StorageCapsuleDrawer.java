package thederpgamer.decor.drawer;

import api.utils.draw.ModWorldDrawer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.decor.data.drawdata.StorageCapsuleDrawData;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class StorageCapsuleDrawer extends ModWorldDrawer {

	private final Object2ObjectOpenHashMap<SegmentPiece, StorageCapsuleDrawData> drawMap = new Object2ObjectOpenHashMap<>();

	@Override
	public void onInit() {
	}

	@Override
	public void draw() {
		for(StorageCapsuleDrawData drawData : drawMap.values()) drawData.draw();
	}

	@Override
	public void update(Timer timer) {
	}

	@Override
	public void cleanUp() {
		for(StorageCapsuleDrawData drawData : drawMap.values()) drawData.cleanUp();
		drawMap.clear();
	}

	@Override
	public boolean isInvisible() {
		return false;
	}

	public void remove(StorageCapsuleDrawData storageCapsuleDrawData) {
		drawMap.remove(storageCapsuleDrawData.getSegmentPiece());
	}
}
