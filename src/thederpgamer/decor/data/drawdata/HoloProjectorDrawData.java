package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.data.graphics.image.ScalableImageSubSprite;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.utils.SegmentPieceUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public class HoloProjectorDrawData {

	public long indexAndOrientation;
	public Vector3i offset;
	public Vector3i rotation;
	public int scale;
	public String src;
	public boolean holographic;
	public boolean changed;

	public transient ScalableImageSubSprite[] subSprite;
	public transient Transform transform;
	public transient Sprite image;

	public transient int currentFrame;
	public transient Sprite[] frames;

	public HoloProjectorDrawData(long indexAndOrientation, Vector3i offset, Vector3i rotation, int scale, String src, boolean holographic, boolean changed) {
		this.indexAndOrientation = indexAndOrientation;
		this.offset = offset;
		this.rotation = rotation;
		this.scale = scale;
		this.src = src;
		this.changed = changed;
		this.holographic = holographic;
		this.transform = new Transform();
	}

	public HoloProjectorDrawData(SegmentPiece segmentPiece) {
		scale = 1;
		offset = new Vector3i();
		rotation = new Vector3i();
		src = "";
		holographic = true;
		changed = true;
		if (segmentPiece != null) {
			indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
			SegmentPieceUtils.getProjectorTransform(segmentPiece, offset, rotation, transform);
		}
	}

	public void copyTo(HoloProjectorDrawData drawData) {
		drawData.src = src;
		drawData.offset.set(offset);
		drawData.rotation.set(rotation);
		drawData.scale = scale;
		drawData.holographic = holographic;
		drawData.changed = true;
	}

	public void nextFrame() {
		if (!src.endsWith(".gif") || frames == null) currentFrame = 0;
		else {
			if (currentFrame < frames.length) currentFrame++;
			else currentFrame = 0;
		}
	}

	public Sprite getCurrentFrame() {
		if (!src.endsWith(".gif")) return image;
		else {
			if (frames == null) frames = ImageManager.getAnimatedImage(src);
			if (frames == null) return image;
			else {
				if (currentFrame < frames.length) return frames[currentFrame];
				else return frames[frames.length - 1];
			}
		}
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof HoloProjectorDrawData) {
			HoloProjectorDrawData drawData = (HoloProjectorDrawData) object;
			return drawData.src.equals(src) && drawData.offset.equals(offset) && drawData.rotation.equals(rotation) && drawData.scale == scale && drawData.holographic == holographic;
		} else return false;
	}
}
