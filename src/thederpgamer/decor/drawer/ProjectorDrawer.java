package thederpgamer.decor.drawer;

import api.utils.draw.ModWorldDrawer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.utils.SegmentPieceUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mod world drawer for projector blocks.
 *
 * @author TheDerpGamer
 * @since 07/18/2021
 */
public class ProjectorDrawer extends ModWorldDrawer implements Drawable, Shaderable {

	private final ConcurrentHashMap<SegmentPiece, Object> drawMap = new ConcurrentHashMap<>();
	private float time;

	@Override
	public void cleanUp() {}

	@Override
	public boolean isInvisible() {
		return false;
	}

	@Override
	public void onInit() {}

	@Override
	public void update(Timer timer) {
		time += timer.getDelta() * 2f;
	}

	@Override
	public void draw() {
		if (!drawMap.isEmpty()) {
			int drawCount = 0;
			for (Map.Entry<SegmentPiece, Object> entry : drawMap.entrySet()) {
				if (drawCount >= ConfigManager.getMainConfig().getConfigurableInt("max-projector-draws-per-frame", 120)) return;
				SegmentPiece segmentPiece = entry.getKey();
				//if (!segmentPiece.getSegmentController().getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex()) || segmentPiece.getSegmentController().getSegmentBuffer().getPointUnsave(segmentPiece.getAbsoluteIndex()).getType() != segmentPiece.getType() || !segmentPiece.getSegmentController().isFullyLoadedWithDock() || !segmentPiece.getSegmentController().isInClientRange()) drawMap.remove(segmentPiece);
				if(!checkDraw(segmentPiece)) drawMap.remove(segmentPiece);
				else {
					if (entry.getValue() instanceof HoloProjectorDrawData) {
						HoloProjectorDrawData drawData = (HoloProjectorDrawData) entry.getValue();
						if (!checkDraw(segmentPiece)) {
							drawMap.remove(entry.getKey()); // Force an update next frame
							continue;
						}
						Sprite image;
						if (drawData.src.endsWith(".gif")) {
							drawData.nextFrame();
							image = drawData.getCurrentFrame();
						} else image = drawData.image;
						if (image != null) {
							if (drawData.holographic) {
								ShaderLibrary.scanlineShader.setShaderInterface(this);
								ShaderLibrary.scanlineShader.load();
							}
							SegmentPieceUtils.getProjectorTransform(segmentPiece, drawData.offset, drawData.rotation, drawData.transform);

							// Don't create new objects every frame
							// float maxDim = Math.max(image.getWidth(), image.getHeight());
							// ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[] {new
							// ScalableImageSubSprite(((float) drawData.scale / (maxDim * 5)) * -1,
							// drawData.transform)};
							image.setTransform(drawData.transform);
							Sprite.draw3D(image, drawData.subSprite, 1, Controller.getCamera());
							if (drawData.holographic) ShaderLibrary.scanlineShader.unload();
							drawCount++;
						}
					} else if (entry.getValue() instanceof TextProjectorDrawData) {
						TextProjectorDrawData drawData = (TextProjectorDrawData) entry.getValue();
						if (drawData.changed || !checkDraw(segmentPiece)) {
							drawMap.remove(entry.getKey()); // Force an update next frame
							continue;
						}
						if (drawData.textOverlay != null) {
							if (drawData.holographic) {
								ShaderLibrary.scanlineShader.setShaderInterface(this);
								ShaderLibrary.scanlineShader.load();
							}
							// if(drawData.textOverlay.getFont() == null)
							// drawData.textOverlay.setFont(ResourceManager.getFont("Monda-Extended-Bold",
							// drawData.scale + 10, Color.decode("0x" + drawData.color)));
							SegmentPieceUtils.getProjectorTransform(segmentPiece, drawData.offset, drawData.rotation, drawData.transform);
							drawData.textOverlay.setTransform(drawData.transform);
							drawData.textOverlay.draw();
							if (drawData.holographic) ShaderLibrary.scanlineShader.unload();
							drawCount++;
						}
					}
				}
			}
		}
	}

	private boolean checkDraw(SegmentPiece segmentPiece) {
		boolean canToggle = false;
		SegmentController segmentController = segmentPiece.getSegmentController();
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

	@Override
	public void onExit() {}

	@Override
	public void updateShader(DrawableScene drawableScene) {}

	@Override
	public void updateShaderParameters(Shader shader) {
		GlUtil.updateShaderFloat(shader, "uTime", time);
		GlUtil.updateShaderVector2f(shader, "uResolution", 20, 1000);
		GlUtil.updateShaderInt(shader, "uDiffuseTexture", 0);
	}

	public void addDraw(SegmentPiece segmentPiece, Object drawData) {
		drawMap.remove(segmentPiece);
		drawMap.put(segmentPiece, drawData);
	}
}
