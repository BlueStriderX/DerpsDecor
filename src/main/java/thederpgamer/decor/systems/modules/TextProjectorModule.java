package thederpgamer.decor.systems.modules;

import api.common.GameClient;
import api.utils.game.module.util.SimpleDataStorageMCModule;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.MathUtils;
import api.utils.SegmentPieceUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/18/2021
 */
public class TextProjectorModule extends SimpleDataStorageMCModule {
	public TextProjectorModule(SegmentController ship, ManagerContainer<?> managerContainer) {
		super(ship, managerContainer, DerpsDecor.getInstance(), ElementManager.getBlock("Text Projector").getId());
	}

	@Override
	public void handle(Timer timer) {
		if(isOnServer()) return;
		final HashMap<Long, TextProjectorDrawData> drawDataMap = getProjectorMap();
		for(Map.Entry<Long, TextProjectorDrawData> obj : drawDataMap.entrySet()) {
			TextProjectorDrawData drawData = obj.getValue();
			long indexAndOrientation = obj.getKey();
			long index = ElementCollection.getPosIndexFrom4(indexAndOrientation);
			if(drawData.text != null && drawData.color != null && !drawData.text.isEmpty()) {
				if(drawData.changed || drawData.textOverlay == null || drawData.color.isEmpty()) {
					GUITextOverlay textOverlay = new GUITextOverlay(30, 10, GameClient.getClientState());
					textOverlay.onInit();
					int trueSize = drawData.scale + 10;
					try {
						textOverlay.setFont(ResourceManager.getFont("Monda-Extended-Bold", trueSize, Color.decode("0x" + drawData.color)));
					} catch(Exception exception) {
						exception.printStackTrace();
						textOverlay.setFont(ResourceManager.getFont("Monda-Extended-Bold", trueSize, Color.white));
						drawData.color = "FFFFFF";
					}
					textOverlay.setScale(-trueSize / 1000.0f, -trueSize / 1000.0f, -trueSize / 1000.0f);
					String text = drawData.text;
					//Process regex
					//text = replaceRegex(text);
					textOverlay.setTextSimple(text);
					textOverlay.setBlend(true);
					textOverlay.doDepthTest = true;
					drawData.textOverlay = textOverlay;
					drawData.changed = false;
				}
				if(segmentController.getSegmentBuffer().existsPointUnsave(index)) {
					SegmentPiece segmentPiece = segmentController.getSegmentBuffer().getPointUnsave(index);
					if(segmentPiece.getType() != getProjectorId()) {
						drawDataMap.remove(indexAndOrientation);
						continue;
					}
					if(canDraw(segmentPiece)) {
						if(drawData.changed || drawData.transform == null || drawData.transform.origin.length() <= 0) {
							if(drawData.transform == null) drawData.transform = new Transform();
							thederpgamer.decor.utils.SegmentPieceUtils.getProjectorTransform(segmentPiece, drawData.offset, drawData.rotation, drawData.transform);
							Quat4f currentRot = new Quat4f();
							drawData.transform.getRotation(currentRot);
							Quat4f addRot = new Quat4f();
							QuaternionUtil.setEuler(addRot, drawData.rotation.x / 100.0f, drawData.rotation.y / 100.0f, drawData.rotation.z / 100.0f);
							currentRot.mul(addRot);
							MathUtils.roundQuat(currentRot);
							drawData.transform.setRotation(currentRot);
							drawData.transform.origin.add(new Vector3f(drawData.offset.toVector3f()));
							MathUtils.roundVector(drawData.transform.origin);
							drawData.changed = false;
						}
					}
				}
			}
		}
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

	public HashMap<Long, TextProjectorDrawData> getProjectorMap() {
		if(!(data instanceof TextProjectorDrawMap)) data = new TextProjectorDrawMap();
		return ((TextProjectorDrawMap) data).map;
	}

	private boolean canDraw(SegmentPiece segmentPiece) {
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
	public void handleRemove(long abs) {
		super.handleRemove(abs);
		removeDrawData(abs);
		flagUpdatedData();
	}

	public void removeDrawData(long indexAndOrientation) {
		getProjectorMap().remove(indexAndOrientation);
	}

	@Override
	public String getName() {
		return "TextProjector_ManagerModule";
	}

	public short getProjectorId() {
		return ElementManager.getBlock("Text Projector").getId();
	}

	public TextProjectorDrawData getDrawData(SegmentPiece segmentPiece) {
		return getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
	}

	public TextProjectorDrawData getDrawData(long indexAndOrientation) {
		if(getProjectorMap().containsKey(indexAndOrientation)) return getProjectorMap().get(indexAndOrientation);
		return createNewDrawData(indexAndOrientation);
	}

	private TextProjectorDrawData createNewDrawData(long indexAndOrientation) {
		long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
		SegmentPiece segmentPiece = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(absIndex);
		TextProjectorDrawData drawData = new TextProjectorDrawData(segmentPiece);
		drawData.indexAndOrientation = indexAndOrientation;
		getProjectorMap().put(indexAndOrientation, drawData);
		flagUpdatedData();
		return drawData;
	}

	public void setDrawData(long indexAndOrientation, TextProjectorDrawData drawData) {
		removeDrawData(indexAndOrientation);
		getProjectorMap().put(indexAndOrientation, drawData);
		flagUpdatedData();
	}

	public void resetAllProjectors() {
		try {
			getProjectorMap().clear();
			flagUpdatedData();
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	private static class TextProjectorDrawMap {
		public HashMap<Long, TextProjectorDrawData> map;

		public TextProjectorDrawMap() {
			map = new HashMap<>();
		}
	}
}
