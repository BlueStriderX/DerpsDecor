package thederpgamer.decor.drawer;

import api.common.GameClient;
import api.common.GameCommon;
import api.utils.SegmentPieceUtils;
import api.utils.draw.ModWorldDrawer;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.DrawableScene;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.drawdata.ProjectorInterface;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.systems.modules.HoloProjectorModule;
import thederpgamer.decor.systems.modules.TextProjectorModule;

import java.util.ArrayList;
import java.util.Map;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class ProjectorDrawer extends ModWorldDrawer implements Shaderable {
	public static final int MAX_DRAWS_PER_FRAME = 240;
	private final ObjectArrayList<ProjectorInterface> drawQueue = new ObjectArrayList<>();
	private final Vector3i lastClientSector = new Vector3i();
	private final ObjectArrayList<SegmentController> loadedControllers = new ObjectArrayList<>();
	private float time;
	private long lastAdd;
	private short holoProjectorId;
	private short textProjectorId;
	public static boolean needsUpdate = true;

	@Override
	public void update(Timer timer) {
		time += timer.getDelta() * 2.0f;
	}

	@Override
	public void draw() {
		if(System.currentTimeMillis() - lastAdd > 5000) needsUpdate = true;
		if(needsUpdate) enqueueDraws();
		else {
			int drawCount = 0;
			for(ProjectorInterface projector : drawQueue) {
				if(drawCount >= MAX_DRAWS_PER_FRAME) break;
				if(projector instanceof HoloProjectorDrawData) {
					HoloProjectorDrawData drawData = (HoloProjectorDrawData) projector;
					Sprite image = drawData.image;
					if(image != null) {
						if(drawData.holographic) {
							ShaderLibrary.scanlineShader.setShaderInterface(this);
							ShaderLibrary.scanlineShader.load();
						}
						if(drawData.segmentPiece == null) drawData.segmentPiece = ((SegmentController) GameCommon.getGameObject(drawData.entityId)).getSegmentBuffer().getPointUnsave(ElementCollection.getPosIndexFrom4(drawData.indexAndOrientation));
						drawData.transform.set(thederpgamer.decor.utils.SegmentPieceUtils.getFaceTransform(drawData.segmentPiece, drawData.offset, drawData.rotation, drawData.transform));
						image.setTransform(drawData.transform);
						if(drawData.isGif()) {
							Sprite.draw3D(drawData.getCurrentFrame(), drawData.subSprite, 1, Controller.getCamera());
							drawData.nextFrame();
						} else Sprite.draw3D(image, drawData.subSprite, 1, Controller.getCamera());
						if(drawData.holographic) ShaderLibrary.scanlineShader.unload();
						drawCount++;
					}
				} else if(projector instanceof TextProjectorDrawData) {
					TextProjectorDrawData drawData = (TextProjectorDrawData) projector;
					GUITextOverlay text = drawData.textOverlay;
					if(text != null) {
						if(drawData.holographic) {
							ShaderLibrary.scanlineShader.setShaderInterface(this);
							ShaderLibrary.scanlineShader.load();
						}
						if(drawData.segmentPiece == null) drawData.segmentPiece = ((SegmentController) GameCommon.getGameObject(drawData.entityId)).getSegmentBuffer().getPointUnsave(ElementCollection.getPosIndexFrom4(drawData.indexAndOrientation));
						drawData.transform.set(thederpgamer.decor.utils.SegmentPieceUtils.getFaceTransform(drawData.segmentPiece, drawData.offset, drawData.rotation, drawData.transform));
						text.setTransform(drawData.transform);
						text.draw();
						if(drawData.holographic) ShaderLibrary.scanlineShader.unload();
						drawCount++;
					}
				}
			}
		}
	}

	private void enqueueDraws() {
		drawQueue.clear();
		updateLoaded();
		for(SegmentController segmentController : loadedControllers) {
			if(!(segmentController instanceof ManagedUsableSegmentController)) continue;
			Long2ObjectArrayMap<ProjectorInterface> map = new Long2ObjectArrayMap<>();
			getProjectorsForEntity(segmentController, map);
			for(Map.Entry<Long, ProjectorInterface> entry : map.long2ObjectEntrySet()) drawQueue.add(entry.getValue());
		}
		lastAdd = System.currentTimeMillis();
		needsUpdate = false;
	}

	private void updateLoaded() {
		lastClientSector.set(GameClient.getClientPlayerState().getCurrentSector());
		loadedControllers.clear();
		for(Sendable sendable : GameClient.getClientState().getLocalAndRemoteObjectContainer().getLocalObjects().values()) {
			if(sendable instanceof SegmentController && ((SegmentController) sendable).isFullyLoadedWithDock() && ((SegmentController) sendable).getSectorId() == GameClient.getClientPlayerState().getSectorId()) loadedControllers.add((SegmentController) sendable);
		}
	}

	private Long2ObjectArrayMap<ProjectorInterface> getProjectorsForEntity(SegmentController segmentController, Long2ObjectArrayMap<ProjectorInterface> map) {
		ManagerContainer<?> managerContainer = ((ManagedUsableSegmentController<?>) segmentController).getManagerContainer();
		if(managerContainer.getModMCModule(holoProjectorId) != null) {
			HoloProjectorModule holoProjectorModule = (HoloProjectorModule) managerContainer.getModMCModule(holoProjectorId);
			for(Map.Entry<Long, HoloProjectorDrawData> entry : holoProjectorModule.getProjectorMap().entrySet()) {
				SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(entry.getKey());
				if(segmentPiece == null || !checkDraw(segmentPiece) || segmentPiece.getType() != holoProjectorId) {
					holoProjectorModule.removeDrawData(entry.getKey());
					continue;
				}
				entry.getValue().entityId = segmentController.getId();
				thederpgamer.decor.utils.SegmentPieceUtils.getFaceTransform(segmentPiece, entry.getValue().offset, entry.getValue().rotation, entry.getValue().transform);
				map.put(entry.getKey(), entry.getValue());
			}
		}
		if(managerContainer.getModMCModule(textProjectorId) != null) {
			TextProjectorModule textProjectorModule = (TextProjectorModule) managerContainer.getModMCModule(textProjectorId);
			for(Map.Entry<Long, TextProjectorDrawData> entry : textProjectorModule.getProjectorMap().entrySet()) {
				SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(entry.getKey());
				if(segmentPiece == null || !checkDraw(segmentPiece) || segmentPiece.getType() != textProjectorId) {
					textProjectorModule.removeDrawData(entry.getKey());
					continue;
				}
				thederpgamer.decor.utils.SegmentPieceUtils.getFaceTransform(segmentPiece, entry.getValue().offset, entry.getValue().rotation, entry.getValue().transform);
				entry.getValue().entityId = segmentController.getId();
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
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
	public void cleanUp() {
	}

	@Override
	public boolean isInvisible() {
		return false;
	}

	@Override
	public void onInit() {
		holoProjectorId = ElementManager.getBlock("Holo Projector").getId();
		textProjectorId = ElementManager.getBlock("Text Projector").getId();
	}

	@Override
	public void onExit() {
	}

	@Override
	public void updateShader(DrawableScene drawableScene) {
	}

	@Override
	public void updateShaderParameters(Shader shader) {
		GlUtil.updateShaderFloat(shader, "uTime", time);
		GlUtil.updateShaderVector2f(shader, "uResolution", 20, 1000);
		GlUtil.updateShaderInt(shader, "uDiffuseTexture", 0);
	}
}
