package thederpgamer.decor.gui.panel;

import api.utils.gui.GUIInputDialogPanel;
import org.schema.game.client.view.gui.buildtools.GUIBuildToolSettingSelector;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIActivatableTextBar;
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

    private GUIActivatableTextBar textInput;
    private HoloProjectorOffsetSetting xOffsetSetting;
    private HoloProjectorOffsetSetting yOffsetSetting;
    private HoloProjectorOffsetSetting zOffsetSetting;
    private HoloProjectorScaleSetting scaleSetting;

    public HoloProjectorConfigPanel(InputState inputState, GUICallback guiCallback) {
        super(inputState, "holoprojectorconfigpanel", "Holo Projector Configuration", "", 300, 300, guiCallback);
    }

    @Override
    public void onInit() {
        super.onInit();

        GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
        contentPane.setTextBoxHeightLast(350);

        textInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.MEDIUM, "Image src", contentPane.getContent(0), new TextCallback() {
            @Override
            public String[] getCommandPrefixes() {
                return null;
            }

            @Override
            public String handleAutoComplete(String s, TextCallback textCallback, String s1) throws PrefixNotFoundException {
                return null;
            }

            @Override
            public void onFailedTextCheck(String s) {

            }

            @Override
            public void onTextEnter(String s, boolean b, boolean b1) {

            }

            @Override
            public void newLine() {

            }
        }, null);
        textInput.onInit();
        //textInput.getPos().x = (contentPane.getWidth() / 2) - (textInput.getWidth() / 2);
        textInput.getPos().y += 30;
        contentPane.getContent(0).attach(textInput);

        xOffsetSetting = new HoloProjectorOffsetSetting();
        GUIBuildToolSettingSelector xOffsetSelector = new GUIBuildToolSettingSelector(getState(), xOffsetSetting);
        xOffsetSelector.onInit();
        xOffsetSelector.getPos().x += ((contentPane.getWidth() / 2) - (xOffsetSelector.getWidth() / 2)) - 150;
        xOffsetSelector.getPos().y += 100;
        contentPane.getContent(0).attach(xOffsetSelector);

        yOffsetSetting = new HoloProjectorOffsetSetting();
        GUIBuildToolSettingSelector yOffsetSelector = new GUIBuildToolSettingSelector(getState(), yOffsetSetting);
        yOffsetSelector.onInit();
        yOffsetSelector.getPos().x += (contentPane.getWidth() / 2) - (yOffsetSelector.getWidth() / 2);
        yOffsetSelector.getPos().y += 100;
        contentPane.getContent(0).attach(yOffsetSelector);

        zOffsetSetting = new HoloProjectorOffsetSetting();
        GUIBuildToolSettingSelector zOffsetSelector = new GUIBuildToolSettingSelector(getState(), zOffsetSetting);
        zOffsetSelector.onInit();
        zOffsetSelector.getPos().x += ((contentPane.getWidth() / 2) - (zOffsetSelector.getWidth() / 2)) + 150;
        zOffsetSelector.getPos().y += 100;
        contentPane.getContent(0).attach(zOffsetSelector);

        scaleSetting = new HoloProjectorScaleSetting();
        GUIBuildToolSettingSelector scaleSelector = new GUIBuildToolSettingSelector(getState(), scaleSetting);
        scaleSelector.onInit();
        scaleSelector.getPos().x = yOffsetSelector.getPos().x;
        scaleSelector.getPos().y += 200;
        contentPane.getContent(0).attach(scaleSelector);

        getButtonCancel().setUserPointer("CANCEL");
        getButtonOK().setUserPointer("OK");

        GUITextOverlay xOffsetOverlay = new GUITextOverlay(50, 15, getState());
        xOffsetOverlay.onInit();
        xOffsetOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        xOffsetOverlay.setTextSimple("X offset:");
        xOffsetOverlay.setPos(xOffsetSelector.getPos());
        xOffsetOverlay.getPos().x += xOffsetOverlay.getWidth() / 4;
        xOffsetOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(xOffsetOverlay);

        GUITextOverlay yOffsetOverlay = new GUITextOverlay(50, 15, getState());
        yOffsetOverlay.onInit();
        yOffsetOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        yOffsetOverlay.setTextSimple("Y offset:");
        yOffsetOverlay.setPos(yOffsetSelector.getPos());
        yOffsetOverlay.getPos().x += yOffsetOverlay.getWidth() / 4;
        yOffsetOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(yOffsetOverlay);

        GUITextOverlay zOffsetOverlay = new GUITextOverlay(50, 15, getState());
        zOffsetOverlay.onInit();
        zOffsetOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        zOffsetOverlay.setTextSimple("Z offset:");
        zOffsetOverlay.setPos(zOffsetSelector.getPos());
        zOffsetOverlay.getPos().x += zOffsetOverlay.getWidth() / 4;
        zOffsetOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(zOffsetOverlay);

        GUITextOverlay scaleOverlay = new GUITextOverlay(50, 15, getState());
        scaleOverlay.onInit();
        scaleOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        scaleOverlay.setTextSimple("Scale:");
        scaleOverlay.setPos(scaleSelector.getPos());
        scaleOverlay.getPos().x += scaleOverlay.getWidth() / 2;
        scaleOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(scaleOverlay);
    }

    public int getXOffset() {
        return xOffsetSetting.setting;
    }

    public void setXOffset(int offset) {
        xOffsetSetting.set(offset);
    }

    public int getYOffset() {
        return yOffsetSetting.setting;
    }

    public void setYOffset(int offset) {
        yOffsetSetting.set(offset);
    }

    public int getZOffset() {
        return zOffsetSetting.setting;
    }

    public void setZOffset(int offset) {
        zOffsetSetting.set(offset);
    }

    public int getScaleSetting() {
        return scaleSetting.setting;
    }

    public void setScaleSetting(float scale) {
        scaleSetting.set(scale);
    }

    public String getSrc() {
        if(textInput.getText() == null) return "";
        else return textInput.getText();
    }

    public void setSrc(String src) {
        textInput.setTextWithoutCallback(src);
    }
}
