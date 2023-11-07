package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.data.graphics.image.ScalableImageSubSprite;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.utils.SegmentPieceUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public class HoloProjectorDrawData implements ProjectorInterface {
	public long indexAndOrientation;
	public Vector3i offset;
	public Vector3i rotation;
	public int scale;
	public String src;
	public boolean holographic;
	public boolean changed;
	public int entityId;
	public transient ScalableImageSubSprite[] subSprite;
	public transient Transform transform;
	public transient Sprite image;
	public transient int currentFrame;
	public transient Sprite[] frames;
	public transient SegmentPiece segmentPiece;

	public HoloProjectorDrawData(long indexAndOrientation, Vector3i offset, Vector3i rotation, int scale, String src, boolean holographic, boolean changed) {
		this.indexAndOrientation = indexAndOrientation;
		this.offset = offset;
		this.rotation = rotation;
		this.scale = scale;
		this.src = src;
		this.changed = changed;
		this.holographic = holographic;
		this.transform = new Transform();
		if(changed) ProjectorDrawer.needsUpdate = true;
	}

	public HoloProjectorDrawData(SegmentPiece segmentPiece) {
		scale = 1;
		offset = new Vector3i();
		rotation = new Vector3i();
		src = "";
		holographic = true;
		changed = true;
		if(segmentPiece != null) {
			this.segmentPiece = segmentPiece;
			indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
			SegmentPieceUtils.getFaceTransform(segmentPiece, offset, rotation, transform);
		}
		ProjectorDrawer.needsUpdate = true;
	}

	public void copyTo(ProjectorInterface drawData) {
		if(drawData instanceof HoloProjectorDrawData) {
			HoloProjectorDrawData projectorDrawData = (HoloProjectorDrawData) drawData;
			projectorDrawData.entityId = entityId;
			projectorDrawData.src = src;
			projectorDrawData.offset.set(offset);
			projectorDrawData.rotation.set(rotation);
			projectorDrawData.scale = scale;
			projectorDrawData.holographic = holographic;
			projectorDrawData.changed = true;
			ProjectorDrawer.needsUpdate = true;
		}
	}

	public void nextFrame() {
		if(!src.endsWith(".gif") || frames == null) currentFrame = 0;
		else {
			if(currentFrame < frames.length - 1) currentFrame++;
			else currentFrame = 0;
			image = frames[currentFrame];
		}
	}

	public Sprite getCurrentFrame() {
		if(!src.endsWith(".gif")) return image;
		else {
			if(frames == null) frames = ImageManager.getAnimatedImage(src);
			if(frames == null) return image;
			else {
				if(currentFrame < frames.length) image = frames[currentFrame];
				else image = frames[frames.length - 1];
				return image;
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

	public boolean isGif() {
		return src.toLowerCase().endsWith(".gif");
	}
}
