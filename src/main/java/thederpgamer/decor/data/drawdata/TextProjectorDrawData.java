package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.utils.SegmentPieceUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/06/2021
 */
public class TextProjectorDrawData implements ProjectorInterface {
	public long indexAndOrientation;
	public Vector3i offset;
	public Vector3i rotation;
	public int scale;
	public String text;
	public String color;
	public boolean holographic;
	public boolean changed;
	public int entityId;
	public transient Transform transform;
	public transient GUITextOverlay textOverlay;
	public transient SegmentPiece segmentPiece;

	public TextProjectorDrawData(long indexAndOrientation, Vector3i offset, Vector3i rotation, int scale, String text, String color, boolean holographic, boolean changed) {
		this.indexAndOrientation = indexAndOrientation;
		this.offset = offset;
		this.rotation = rotation;
		this.scale = scale;
		this.text = text;
		this.color = color;
		this.holographic = holographic;
		this.changed = changed;
		this.transform = new Transform();
		if(changed) ProjectorDrawer.needsUpdate = true;
	}

	public TextProjectorDrawData(SegmentPiece segmentPiece) {
		scale = 1;
		offset = new Vector3i();
		rotation = new Vector3i();
		text = "";
		color = "FFFFFF";
		holographic = true;
		changed = true;
		ProjectorDrawer.needsUpdate = true;
		if(segmentPiece != null) {
			this.segmentPiece = segmentPiece;
			indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
			SegmentPieceUtils.getFaceTransform(segmentPiece, offset, rotation, transform);
		}
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof TextProjectorDrawData) {
			TextProjectorDrawData drawData = (TextProjectorDrawData) object;
			return drawData.text.equals(text) && drawData.color.equals(color) && drawData.offset.equals(offset) && drawData.rotation.equals(rotation) && drawData.scale == scale && drawData.holographic == holographic;
		} else return false;
	}

	@Override
	public void copyTo(ProjectorInterface drawData) {
		if(drawData instanceof TextProjectorDrawData) {
			TextProjectorDrawData projectorDrawData = (TextProjectorDrawData) drawData;
			projectorDrawData.entityId = entityId;
			projectorDrawData.text = text;
			projectorDrawData.color = color;
			projectorDrawData.offset.set(offset);
			projectorDrawData.rotation.set(rotation);
			projectorDrawData.scale = scale;
			projectorDrawData.holographic = holographic;
			projectorDrawData.changed = true;
			ProjectorDrawer.needsUpdate = true;
		}
	}
}
