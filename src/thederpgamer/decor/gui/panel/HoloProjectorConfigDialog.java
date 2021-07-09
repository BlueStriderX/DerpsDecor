package thederpgamer.decor.gui.panel;

import api.common.GameClient;
import api.utils.gui.GUIInputDialog;
import api.utils.gui.GUIInputDialogPanel;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.decor.data.ProjectorDrawData;
import thederpgamer.decor.utils.DataUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjectorConfigDialog extends GUIInputDialog {

    public SegmentPiece segmentPiece;

    @Override
    public GUIInputDialogPanel createPanel() {
        return new HoloProjectorConfigPanel(getState(), this);
    }

    @Override
    public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
        if(!isOccluded() && mouseEvent.pressedLeftMouse()) {
            if(callingElement.getUserPointer() != null) {
                switch((String) callingElement.getUserPointer()) {
                    case "X":
                    case "CANCEL":
                        deactivate();
                        break;
                    case "OK":
                        ProjectorDrawData drawData = DataUtils.getProjectorDrawData(segmentPiece);
                        drawData.scale = ((HoloProjectorConfigPanel) getInputPanel()).getScaleSetting();
                        drawData.xOffset = ((HoloProjectorConfigPanel) getInputPanel()).getXOffset();
                        drawData.yOffset = ((HoloProjectorConfigPanel) getInputPanel()).getYOffset();
                        drawData.zOffset = ((HoloProjectorConfigPanel) getInputPanel()).getZOffset();
                        drawData.src = ((HoloProjectorConfigPanel) getInputPanel()).getSrc();
                        deactivate();
                        break;
                }
            }
        }
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().suspend(false);
    }
}
