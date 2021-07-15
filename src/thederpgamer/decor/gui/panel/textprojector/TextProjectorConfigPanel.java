package thederpgamer.decor.gui.panel.textprojector;

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
import thederpgamer.decor.gui.elements.GUIMinMaxSetting;
import thederpgamer.decor.manager.ConfigManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/15/2021
 */
public class TextProjectorConfigPanel extends GUIInputDialogPanel {

    private GUIActivatableTextBar textInput;
    private GUIActivatableTextBar colorInput;
    private GUIMinMaxSetting xOffsetSetting;
    private GUIMinMaxSetting yOffsetSetting;
    private GUIMinMaxSetting zOffsetSetting;
    private GUIMinMaxSetting xRotSetting;
    private GUIMinMaxSetting yRotSetting;
    private GUIMinMaxSetting zRotSetting;
    private GUIMinMaxSetting scaleSetting;

    public TextProjectorConfigPanel(InputState inputState, GUICallback guiCallback) {
        super(inputState, "textprojectorconfigpanel", "Text Projector Configuration", "", 500, 500, guiCallback);
    }

    @Override
    public void onInit() {
        super.onInit();

        GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
        contentPane.setTextBoxHeightLast(500);

        textInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.MEDIUM, 128, 1, "Text", contentPane.getContent(0), new TextCallback() {
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
        contentPane.getContent(0).attach(textInput);

        colorInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.MEDIUM, 6, 1, "Color Code", contentPane.getContent(0), new TextCallback() {
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
        colorInput.onInit();
        colorInput.getPos().y += 50;
        contentPane.getContent(0).attach(colorInput);

        xOffsetSetting = new GUIMinMaxSetting(0, ConfigManager.getMainConfig().getInt("max-projector-offset") * -1, ConfigManager.getMainConfig().getInt("max-projector-offset"));
        GUIBuildToolSettingSelector xOffsetSelector = new GUIBuildToolSettingSelector(getState(), xOffsetSetting);
        xOffsetSelector.onInit();
        xOffsetSelector.getPos().x = ((contentPane.getWidth() / 3) + (xOffsetSelector.getWidth() / 3)) - 100;
        xOffsetSelector.getPos().y += 150;
        contentPane.getContent(0).attach(xOffsetSelector);

        yOffsetSetting = new GUIMinMaxSetting(0, ConfigManager.getMainConfig().getInt("max-projector-offset") * -1, ConfigManager.getMainConfig().getInt("max-projector-offset"));
        GUIBuildToolSettingSelector yOffsetSelector = new GUIBuildToolSettingSelector(getState(), yOffsetSetting);
        yOffsetSelector.onInit();
        yOffsetSelector.getPos().x = ((contentPane.getWidth() / 3) + (yOffsetSelector.getWidth() / 3));
        yOffsetSelector.getPos().y += 150;
        contentPane.getContent(0).attach(yOffsetSelector);

        zOffsetSetting = new GUIMinMaxSetting(0, ConfigManager.getMainConfig().getInt("max-projector-offset") * -1, ConfigManager.getMainConfig().getInt("max-projector-offset"));
        GUIBuildToolSettingSelector zOffsetSelector = new GUIBuildToolSettingSelector(getState(), zOffsetSetting);
        zOffsetSelector.onInit();
        zOffsetSelector.getPos().x = ((contentPane.getWidth() / 3) + (zOffsetSelector.getWidth() / 3)) + 100;
        zOffsetSelector.getPos().y += 150;
        contentPane.getContent(0).attach(zOffsetSelector);

        xRotSetting = new GUIMinMaxSetting(0, -180, 180);
        GUIBuildToolSettingSelector xRotSelector = new GUIBuildToolSettingSelector(getState(), xRotSetting);
        xRotSelector.onInit();
        xRotSelector.getPos().x = ((contentPane.getWidth() / 3) + (xRotSelector.getWidth() / 3)) - 100;
        xRotSelector.getPos().y += 250;
        contentPane.getContent(0).attach(xRotSelector);

        yRotSetting = new GUIMinMaxSetting(0, -180, 180);
        GUIBuildToolSettingSelector yRotSelector = new GUIBuildToolSettingSelector(getState(), yRotSetting);
        yRotSelector.onInit();
        yRotSelector.getPos().x = ((contentPane.getWidth() / 3) + (yRotSelector.getWidth() / 3));
        yRotSelector.getPos().y += 250;
        contentPane.getContent(0).attach(yRotSelector);

        zRotSetting = new GUIMinMaxSetting(0, -180, 180);
        GUIBuildToolSettingSelector zRotSelector = new GUIBuildToolSettingSelector(getState(), zRotSetting);
        zRotSelector.onInit();
        zRotSelector.getPos().x = ((contentPane.getWidth() / 3) + (zRotSelector.getWidth() / 3)) + 100;
        zRotSelector.getPos().y += 250;
        contentPane.getContent(0).attach(zRotSelector);

        scaleSetting = new GUIMinMaxSetting(1, 1, ConfigManager.getMainConfig().getInt("max-projector-scale"));
        GUIBuildToolSettingSelector scaleSelector = new GUIBuildToolSettingSelector(getState(), scaleSetting);
        scaleSelector.onInit();
        scaleSelector.getPos().x = yOffsetSelector.getPos().x;
        scaleSelector.getPos().y += 350;
        contentPane.getContent(0).attach(scaleSelector);

        getButtonCancel().setUserPointer("CANCEL");
        getButtonOK().setUserPointer("OK");

        GUITextOverlay xOffsetOverlay = new GUITextOverlay(50, 15, getState());
        xOffsetOverlay.onInit();
        xOffsetOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        xOffsetOverlay.setTextSimple("X Offset:");
        xOffsetOverlay.setPos(xOffsetSelector.getPos());
        xOffsetOverlay.getPos().x += xOffsetOverlay.getWidth() / 4;
        xOffsetOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(xOffsetOverlay);

        GUITextOverlay yOffsetOverlay = new GUITextOverlay(50, 15, getState());
        yOffsetOverlay.onInit();
        yOffsetOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        yOffsetOverlay.setTextSimple("Y Offset:");
        yOffsetOverlay.setPos(yOffsetSelector.getPos());
        yOffsetOverlay.getPos().x += yOffsetOverlay.getWidth() / 4;
        yOffsetOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(yOffsetOverlay);

        GUITextOverlay zOffsetOverlay = new GUITextOverlay(50, 15, getState());
        zOffsetOverlay.onInit();
        zOffsetOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        zOffsetOverlay.setTextSimple("Z Offset:");
        zOffsetOverlay.setPos(zOffsetSelector.getPos());
        zOffsetOverlay.getPos().x += zOffsetOverlay.getWidth() / 4;
        zOffsetOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(zOffsetOverlay);

        GUITextOverlay xRotOverlay = new GUITextOverlay(50, 15, getState());
        xRotOverlay.onInit();
        xRotOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        xRotOverlay.setTextSimple("X Rotation:");
        xRotOverlay.setPos(xRotSelector.getPos());
        xRotOverlay.getPos().x += xRotOverlay.getWidth() / 4;
        xRotOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(xRotOverlay);

        GUITextOverlay yRotOverlay = new GUITextOverlay(50, 15, getState());
        yRotOverlay.onInit();
        yRotOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        yRotOverlay.setTextSimple("Y Rotation:");
        yRotOverlay.setPos(yRotSelector.getPos());
        yRotOverlay.getPos().x += yRotOverlay.getWidth() / 4;
        yRotOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(yRotOverlay);

        GUITextOverlay zRotOverlay = new GUITextOverlay(50, 15, getState());
        zRotOverlay.onInit();
        zRotOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
        zRotOverlay.setTextSimple("Z Rotation:");
        zRotOverlay.setPos(zRotSelector.getPos());
        zRotOverlay.getPos().x += zRotOverlay.getWidth() / 4;
        zRotOverlay.getPos().y += 30;
        contentPane.getContent(0).attach(zRotOverlay);

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

    public int getXRot() {
        return xRotSetting.setting;
    }

    public void setXRot(int rot) {
        xRotSetting.set(rot);
    }

    public int getYRot() {
        return yRotSetting.setting;
    }

    public void setYRot(int rot) {
        yRotSetting.set(rot);
    }

    public int getZRot() {
        return zRotSetting.setting;
    }

    public void setZRot(int rot) {
        zRotSetting.set(rot);
    }

    public int getScaleSetting() {
        return scaleSetting.setting;
    }

    public void setScaleSetting(float scale) {
        scaleSetting.set(scale);
    }

    public String getColor() {
        if(colorInput.getText() == null) return "";
        else return colorInput.getText();
    }

    public void setColor(String color) {
        colorInput.setTextWithoutCallback(color);
    }

    public String getText() {
        if(textInput.getText() == null) return "";
        else return textInput.getText();
    }

    public void setText(String text) {
        textInput.setTextWithoutCallback(text);
    }
}
