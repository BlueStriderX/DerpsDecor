package thederpgamer.decor.gui.panel.holoprojector;

import api.common.GameClient;
import api.utils.gui.GUIInputDialog;
import api.utils.gui.GUIInputDialogPanel;
import org.schema.game.client.controller.element.world.ClientSegmentProvider;
import org.schema.game.common.controller.SendableSegmentProvider;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.network.objects.remote.RemoteTextBlockPair;
import org.schema.game.network.objects.remote.TextBlockPair;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjectorConfigDialog extends GUIInputDialog {

    private SegmentPiece segmentPiece;

    public void setSegmentPiece(SegmentPiece segmentPiece) {
        this.segmentPiece = segmentPiece;
        String text = segmentPiece.getSegmentController().getTextMap().get(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
        if(text != null && !text.equals("[no data]")) {
            try {
                String[] values = text.split("~");
                int xOffset = Integer.parseInt(values[0]);
                int yOffset = Integer.parseInt(values[1]);
                int zOffset = Integer.parseInt(values[2]);
                int xRot = Integer.parseInt(values[3]);
                int yRot = Integer.parseInt(values[4]);
                int zRot = Integer.parseInt(values[5]);
                int scale = Integer.parseInt(values[6]);
                String src = values[7];
                ((HoloProjectorConfigPanel) getInputPanel()).setXOffset(xOffset);
                ((HoloProjectorConfigPanel) getInputPanel()).setYOffset(yOffset);
                ((HoloProjectorConfigPanel) getInputPanel()).setZOffset(zOffset);
                ((HoloProjectorConfigPanel) getInputPanel()).setXRot(xRot);
                ((HoloProjectorConfigPanel) getInputPanel()).setYRot(yRot);
                ((HoloProjectorConfigPanel) getInputPanel()).setZRot(zRot);
                ((HoloProjectorConfigPanel) getInputPanel()).setScaleSetting(scale);
                ((HoloProjectorConfigPanel) getInputPanel()).setText(src);
            } catch(Exception ignored) { }
        }
        if(((HoloProjectorConfigPanel) getInputPanel()).getScaleSetting() == 0) ((HoloProjectorConfigPanel) getInputPanel()).setScaleSetting(1);
    }

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
                        SendableSegmentProvider ss = ((ClientSegmentProvider) segmentPiece.getSegment().getSegmentController().getSegmentProvider()).getSendableSegmentProvider();
                        TextBlockPair f = new TextBlockPair();
                        f.block = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
                        f.text = ((HoloProjectorConfigPanel) getInputPanel()).getValues();
                        System.out.println(f.text);
                        ss.getNetworkObject().textBlockResponsesAndChangeRequests.add(new RemoteTextBlockPair(f, false));
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
