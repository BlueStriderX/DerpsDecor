package thederpgamer.decor.gui.panel;

import api.utils.gui.GUIInputDialogPanel;
import org.schema.game.client.view.gui.buildtools.GUIBuildToolSettingSelector;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUITextInput;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjectorConfigPanel extends GUIInputDialogPanel {

    private GUITextInput textInput;
    private HoloProjectorOffsetSetting xOffsetSetting;
    private HoloProjectorOffsetSetting yOffsetSetting;
    private HoloProjectorOffsetSetting zOffsetSetting;
    private HoloProjectorScaleSetting scaleSetting;

    public HoloProjectorConfigPanel(InputState inputState, GUICallback guiCallback) {
        super(inputState, "holoprojectorconfigpanel", "Holo Projector Configuration", "", 500, 400, guiCallback);
    }

    @Override
    public void onInit() {
        super.onInit();

        GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
        contentPane.setTextBoxHeightLast(350);

        textInput = new GUITextInput(150, 20, getState());
        textInput.setTextBox(false);
        textInput.onInit();
        textInput.setWidth(300);
        textInput.getPos().x = (getWidth() / 2) - (textInput.getWidth() / 2);
        textInput.getPos().y += 50;
        contentPane.getContent(0).attach(textInput);

        xOffsetSetting = new HoloProjectorOffsetSetting();
        GUIBuildToolSettingSelector xOffsetSelector = new GUIBuildToolSettingSelector(getState(), xOffsetSetting);
        xOffsetSelector.onInit();
        xOffsetSelector.getPos().x = ((getWidth() / 2) - (xOffsetSelector.getWidth() / 2)) - 150;
        xOffsetSelector.getPos().y += 150;
        contentPane.getContent(0).attach(xOffsetSelector);

        yOffsetSetting = new HoloProjectorOffsetSetting();
        GUIBuildToolSettingSelector yOffsetSelector = new GUIBuildToolSettingSelector(getState(), yOffsetSetting);
        yOffsetSelector.onInit();
        yOffsetSelector.getPos().x = (getWidth() / 2) - (xOffsetSelector.getWidth() / 2);
        yOffsetSelector.getPos().y += 150;
        contentPane.getContent(0).attach(yOffsetSelector);

        zOffsetSetting = new HoloProjectorOffsetSetting();
        GUIBuildToolSettingSelector zOffsetSelector = new GUIBuildToolSettingSelector(getState(), zOffsetSetting);
        zOffsetSelector.onInit();
        zOffsetSelector.getPos().x = ((getWidth() / 2) - (xOffsetSelector.getWidth() / 2)) + 150;
        zOffsetSelector.getPos().y += 150;
        contentPane.getContent(0).attach(zOffsetSelector);

        scaleSetting = new HoloProjectorScaleSetting();
        GUIBuildToolSettingSelector scaleSelector = new GUIBuildToolSettingSelector(getState(), scaleSetting);
        scaleSelector.onInit();
        scaleSelector.getPos().x = (getWidth() / 2) + (scaleSelector.getWidth() / 2);
        scaleSelector.getPos().y += 300;
        contentPane.getContent(0).attach(scaleSelector);

        getButtonCancel().setUserPointer("CANCEL");
        getButtonOK().setUserPointer("OK");
    }

    public int getXOffset() {
        return xOffsetSetting.setting;
    }

    public int getYOffset() {
        return yOffsetSetting.setting;
    }

    public int getZOffset() {
        return zOffsetSetting.setting;
    }

    public float getScaleSetting() {
        return scaleSetting.getValue();
    }

    public String getSrc() {
        if(textInput.getTextInput().getCache() == null || textInput.getTextInput().getCache().isEmpty()) return "";
        else return textInput.getTextInput().getCache();
    }
}
