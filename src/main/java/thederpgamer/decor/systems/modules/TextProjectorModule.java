package thederpgamer.decor.systems.modules;

import api.common.GameClient;
import api.utils.game.module.util.SimpleDataStorageMCModule;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.data.drawdata.TextProjectorDrawMap;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.MathUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/18/2021
 */
public class TextProjectorModule extends SimpleDataStorageMCModule {

	public TextProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
		super(
				ship,
				managerContainer,
				DerpsDecor.getInstance(),
				ElementManager.getBlock("Text Projector").getId());
		if (!(data instanceof TextProjectorDrawMap)) data = new TextProjectorDrawMap();
	}

	@Override
	public void handle(Timer timer) {
		if(isOnServer()) return;
		final Long2ObjectMap<TextProjectorDrawData> drawDataMap = getProjectorMap();
		new Thread() {
			@Override
			public void run() {
				for(TextProjectorDrawData obj : getProjectorMap().values()) {
					long indexAndOrientation = obj.indexAndOrientation;
					long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);

					if (obj.text != null && obj.color != null && !obj.text.isEmpty()) {
						if (obj.changed || obj.textOverlay == null || obj.color.isEmpty()) {
							GUITextOverlay textOverlay = new GUITextOverlay(30, 10, GameClient.getClientState());
							textOverlay.onInit();
							int trueSize = obj.scale + 10;
							try {
								textOverlay.setFont(ResourceManager.getFont("Monda-Extended-Bold", trueSize, Color.decode("0x" + obj.color)));
							} catch (Exception exception) {
								exception.printStackTrace();
								textOverlay.setFont(ResourceManager.getFont("Monda-Extended-Bold", trueSize, Color.white));
								obj.color = "FFFFFF";
							}
							textOverlay.setScale(-trueSize / 1000.0f, -trueSize / 1000.0f, -trueSize / 1000.0f);
							String text = obj.text;
							//Process regex
							//text = replaceRegex(text);
							textOverlay.setTextSimple(text);
							textOverlay.setBlend(true);
							textOverlay.doDepthTest = true;
							obj.textOverlay = textOverlay;
							obj.changed = false;
						}

						if (segmentController.getSegmentBuffer().existsPointUnsave(index)) {
							SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(index);
							if (canDraw(segmentPiece)) {
								if (obj.changed || obj.transform == null || obj.transform.origin.length() <= 0) {
									if (obj.transform == null) obj.transform = new Transform();
									SegmentPieceUtils.getProjectorTransform(segmentPiece, obj.offset, obj.rotation, obj.transform);
									Quat4f currentRot = new Quat4f();
									obj.transform.getRotation(currentRot);
									Quat4f addRot = new Quat4f();
									QuaternionUtil.setEuler(addRot, obj.rotation.x / 100.0f, obj.rotation.y / 100.0f, obj.rotation.z / 100.0f);
									currentRot.mul(addRot);
									MathUtils.roundQuat(currentRot);
									obj.transform.setRotation(currentRot);
									obj.transform.origin.add(new Vector3f(obj.offset.toVector3f()));
									MathUtils.roundVector(obj.transform.origin);
									obj.changed = false;
								}
							}
						}
					}
				}
			}
		}.start();
	}

	/*
	private String replaceRegex(String text) {
		List<String> tokens = StringTools.tokenize(text, "[", "]");
		StringBuffer b = new StringBuffer(text);
		for(String s : tokens) {
			for(Replacements.Type type : Replacements.Type.values()) {

			}

		}
		cont.buffer = b;
		cont.realText = b.toString();
	}
	 */

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
		removeDrawData(abs);
		flagUpdatedData();
	}

	@Override
	public String getName() {
		return "TextProjector_ManagerModule";
	}

	public Long2ObjectMap<TextProjectorDrawData> getProjectorMap() {
		if(data instanceof TextProjectorDrawMap) migrate();
		if(!(data instanceof Long2ObjectMap)) data = new Long2ObjectArrayMap<>();
		return (Long2ObjectMap<TextProjectorDrawData>) data;
	}

	private void migrate() {
		if(data instanceof TextProjectorDrawMap) {
			Long2ObjectMap<TextProjectorDrawData> drawDataMap = new Long2ObjectArrayMap<>();
			for(TextProjectorDrawData drawData : ((TextProjectorDrawMap) data).map.values()) drawDataMap.put(drawData.indexAndOrientation, drawData);
			data = drawDataMap;
		}
	}
	public short getProjectorId() {
		return ElementManager.getBlock("Text Projector").getId();
	}

	public void removeDrawData(long indexAndOrientation) {
		getProjectorMap().remove(indexAndOrientation);
	}

	public Object getDrawData(long indexAndOrientation) {
		if (getProjectorMap().containsKey(indexAndOrientation))
			return getProjectorMap().get(indexAndOrientation);
		return createNewDrawData(indexAndOrientation);
	}

	public Object getDrawData(SegmentPiece segmentPiece) {
		return getDrawData(
				ElementCollection.getIndex4(
						segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
	}

	public void setDrawData(long indexAndOrientation, TextProjectorDrawData drawData) {
		removeDrawData(indexAndOrientation);
		getProjectorMap().put(indexAndOrientation, drawData);
		flagUpdatedData();
	}

	private boolean canDraw(SegmentPiece segmentPiece) {
		boolean canToggle = false;
		SegmentController segmentController = segmentPiece.getSegmentController();
		SegmentPiece activator =
				SegmentPieceUtils.getFirstMatchingAdjacent(segmentPiece, ElementKeyMap.ACTIVAION_BLOCK_ID);
		if (activator != null) {
			ArrayList<SegmentPiece> controlling =
					SegmentPieceUtils.getControlledPiecesMatching(activator, segmentPiece.getType());
			if (!controlling.isEmpty()) {
				for (SegmentPiece controlled : controlling) {
					if (controlled.equals(segmentPiece)) {
						canToggle = true;
						break;
					}
				}
			}
		}
		return segmentController.getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex())
				&& segmentController
				.getSegmentBuffer()
				.getPointUnsave(segmentPiece.getAbsoluteIndex())
				.getType()
				== segmentPiece.getType()
				&& segmentController.isFullyLoadedWithDock()
				&& segmentController.isInClientRange()
				&& ((canToggle && activator.isActive()) || activator == null);
	}

	private TextProjectorDrawData createNewDrawData(long indexAndOrientation) {
		long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
		SegmentPiece segmentPiece =
				getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(absIndex);
		TextProjectorDrawData drawData = new TextProjectorDrawData(segmentPiece);
		drawData.indexAndOrientation = indexAndOrientation;
		getProjectorMap().put(indexAndOrientation, drawData);
		flagUpdatedData();
		return drawData;
	}

	public void resetAllProjectors() {
		try {
			getProjectorMap().clear();
			flagUpdatedData();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
