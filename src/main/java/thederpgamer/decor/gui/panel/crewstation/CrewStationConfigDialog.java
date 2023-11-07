package thederpgamer.decor.gui.panel.crewstation;

import api.common.GameClient;
import api.utils.gui.GUIInputDialog;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.system.crew.CrewData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.systems.modules.CrewStationModule;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class CrewStationConfigDialog extends GUIInputDialog {
	private SegmentPiece segmentPiece;

	public CrewStationConfigDialog(SegmentPiece segmentPiece) {
		this.segmentPiece = segmentPiece;
	}

	public void setSegmentPiece(SegmentPiece segmentPiece) {
		this.segmentPiece = segmentPiece;
	}

	private CrewStationConfigPanel getConfigPanel() {
		return (CrewStationConfigPanel) getInputPanel();
	}

	@Override
	public CrewStationConfigPanel createPanel() {
		return new CrewStationConfigPanel(getState(), this);
	}

	@Override
	public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
		try {
			if(!isOccluded() && mouseEvent.pressedLeftMouse()) {
				if(callingElement.getUserPointer() != null) {
					long index = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
					CrewData data = getModule().getData(segmentPiece);
					switch((String) callingElement.getUserPointer()) {
						case "X":
						case "CANCEL":
							deactivate();
							break;
						case "OK":
							data.setCrewName(getConfigPanel().getCrewName());
							data.animationName = getConfigPanel().getAnimationName();
							data.offset = getConfigPanel().getOffset();
							getModule().setCrewBlock(index, data);
							data.recall();
							deactivate();
							break;
						case "RECALL":
							data.recall();
							break;
						case "TOGGLE LOOPING":
							data.looping = !data.looping;
							getModule().setCrewBlock(index, data);
							break;
					}
				}
			}
		} catch(NullPointerException exception) {
			DerpsDecor.getInstance().logException("Failed to handle crew station config dialog callback", exception);
		}
	}

	@Override
	public void onDeactivate() {
		super.onDeactivate();
		GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().suspend(false);
	}

	private CrewStationModule getModule() {
		return (CrewStationModule) ((ManagedUsableSegmentController<?>) segmentPiece.getSegmentController()).getManagerContainer().getModMCModule(ElementManager.getBlock("NPC Station").getId());
	}

	public void setCrewData(CrewData data) {
		getConfigPanel().setCrewData(data);
	}
}
