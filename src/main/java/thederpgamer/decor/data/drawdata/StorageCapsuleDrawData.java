package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.utils.SegmentPieceUtils;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class StorageCapsuleDrawData implements Drawable {

	private final transient SegmentPiece segmentPiece;
	private final transient Transform spriteTransform = new Transform();
	private final transient Transform textTransform = new Transform();
	private transient Sprite sprite;
	private transient GUITextOverlay countOverlay;
	private transient boolean initialized;
	private short id;
	private int count;

	public StorageCapsuleDrawData(SegmentPiece segmentPiece) {
		this.segmentPiece = segmentPiece;
	}

	@Override
	public void onInit() {
		/*
		if(getInventory() instanceof StorageCapsuleInventory) {
			id = ((StorageCapsuleInventory) getInventory()).getType();
			count = ((StorageCapsuleInventory) getInventory()).getCount();
			sprite = IconDatabase.getBuildIconsSprite(ElementKeyMap.getInfo(id).getBuildIconNum());
			sprite.onInit();
			countOverlay = new GUITextOverlay(10, 10, GameClient.getClientState());
			countOverlay.setFont(FontLibrary.FontSize.SMALL.getFont());
			countOverlay.setTextSimple(String.valueOf(count));
			countOverlay.onInit();
			initialized = true;
		} else {
			id = 0;
			count = 0;
		}
		 */
	}

	@Override
	public void draw() {
		if(!initialized || !needsUpdating()) onInit();
		if(!isValid()) {
			GlobalDrawManager.getStorageCapsuleDrawer().remove(this);
			cleanUp();
			return;
		}

		SegmentPieceUtils.getFaceTransform(segmentPiece, new Vector3i(), new Vector3i(), spriteTransform);
		sprite.setTransform(spriteTransform);
		sprite.draw();

		SegmentPieceUtils.getFaceTransform(segmentPiece, new Vector3i(0, -0.75f, 0), new Vector3i(), textTransform);
		countOverlay.setTransform(textTransform);
		countOverlay.draw();
	}

	@Override
	public void cleanUp() {
		if(sprite != null) sprite.cleanUp();
	}

	@Override
	public boolean isInvisible() {
		return false;
	}

	private Inventory getInventory() {
		return ((ManagedSegmentController<?>) segmentPiece.getSegmentController()).getManagerContainer().getInventory(segmentPiece.getAbsoluteIndex());
	}

	private boolean needsUpdating() {
		/*
		Inventory inventory = getInventory();
		if(!(inventory instanceof StorageCapsuleInventory)) return false;
		else if(((StorageCapsuleInventory) inventory).getType() != id && id > 0) return true;
		else return ((StorageCapsuleInventory) inventory).getCount() != count && count > 0;
		 */
		return false;
	}

	private boolean isValid() {
		return segmentPiece != null && segmentPiece.isAlive() && segmentPiece.getSegmentController().getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex()) && segmentPiece.getSegmentController().getSegmentBuffer().getPointUnsave(segmentPiece.getAbsoluteIndex()).getType() == segmentPiece.getType();
	}

	public SegmentPiece getSegmentPiece() {
		return segmentPiece;
	}
}
