package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.forms.Mesh;
import thederpgamer.decor.utils.SegmentPieceUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class ShapeProjectorDrawData implements ProjectorInterface {
	private final Vector3i offset = new Vector3i();
	private final Vector3i rotation = new Vector3i();
	private final transient Transform transform = new Transform();
	private long indexAndOrientation;
	private int scale;
	private boolean filled;
	private String color;
	private boolean holographic;
	private boolean changed;
	private transient Mesh mesh;

	public ShapeProjectorDrawData(long indexAndOrientation, int scale, boolean filled, String color, boolean holographic) {
		this.indexAndOrientation = indexAndOrientation;
		this.scale = scale;
		this.filled = filled;
		this.color = color;
		this.holographic = holographic;
	}

	public ShapeProjectorDrawData(SegmentPiece segmentPiece) {
		scale = 1;
		filled = false;
		color = "FFFFFF";
		holographic = true;
		changed = true;
		if(segmentPiece != null) {
			indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
			SegmentPieceUtils.getProjectorTransform(segmentPiece, offset, rotation, transform);
		}
	}

	public long getIndexAndOrientation() {
		return indexAndOrientation;
	}

	public void setIndexAndOrientation(long indexAndOrientation) {
		this.indexAndOrientation = indexAndOrientation;
	}

	public Vector3i getOffset() {
		return offset;
	}

	public Vector3i getRotation() {
		return rotation;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isHolographic() {
		return holographic;
	}

	public void setHolographic(boolean holographic) {
		this.holographic = holographic;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public Transform getTransform() {
		return transform;
	}

	public Mesh getShape() {
		return mesh;
	}

	@Override
	public void copyTo(ProjectorInterface drawData) {
		if(drawData instanceof ShapeProjectorDrawData) {
			ShapeProjectorDrawData projectorDrawData = (ShapeProjectorDrawData) drawData;
			projectorDrawData.indexAndOrientation = indexAndOrientation;
			projectorDrawData.offset.set(offset);
			projectorDrawData.rotation.set(rotation);
			projectorDrawData.scale = scale;
			projectorDrawData.filled = filled;
			projectorDrawData.color = color;
			projectorDrawData.holographic = holographic;
		}
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof ShapeProjectorDrawData) {
			ShapeProjectorDrawData drawData = (ShapeProjectorDrawData) object;
			return drawData.indexAndOrientation == indexAndOrientation && drawData.offset.equals(offset) && drawData.rotation.equals(rotation) && drawData.scale == scale && drawData.filled == filled && drawData.color.equals(color) && drawData.holographic == holographic;
		} else return false;
	}

	public class ShapeProjectorDrawMap {
		public ConcurrentHashMap<Long, ShapeProjectorDrawData> map;

		public ShapeProjectorDrawMap() {
			map = new ConcurrentHashMap<>();
		}
	}
}
