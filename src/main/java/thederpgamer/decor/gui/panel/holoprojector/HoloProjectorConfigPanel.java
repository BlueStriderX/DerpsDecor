package thederpgamer.decor.gui.panel.holoprojector;

import api.utils.gui.GUIInputDialogPanel;
import org.schema.game.client.view.gui.advanced.tools.CheckboxCallback;
import org.schema.game.client.view.gui.advanced.tools.CheckboxResult;
import org.schema.game.client.view.gui.advanced.tools.GUIAdvCheckbox;
import org.schema.game.client.view.gui.buildtools.GUIBuildToolSettingSelector;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUITextButton;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIActivatableTextBar;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.gui.elements.GUIMinMaxSetting;
import thederpgamer.decor.manager.ConfigManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjectorConfigPanel extends GUIInputDialogPanel {
	private GUIActivatableTextBar textInput;
	private GUIMinMaxSetting xOffsetSetting;
	private GUIMinMaxSetting yOffsetSetting;
	private GUIMinMaxSetting zOffsetSetting;
	private GUIMinMaxSetting xRotSetting;
	private GUIMinMaxSetting yRotSetting;
	private GUIMinMaxSetting zRotSetting;
	private GUIMinMaxSetting scaleSetting;
	private boolean holographic;

	public HoloProjectorConfigPanel(InputState inputState, GUICallback guiCallback) {
		super(inputState, "holoprojectorconfigpanel", "Holo Projector Configuration", "", 500, 530, guiCallback);
	}

	@Override
	public void onInit() {
		super.onInit();
		GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
		contentPane.setTextBoxHeightLast(500);
		textInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.MEDIUM, 128, 1, "Image src", contentPane.getContent(0), new TextCallback() {
			@Override
			public String[] getCommandPrefixes() {
				return null;
			}

			@Override
			public String handleAutoComplete(String s, TextCallback textCallback, String s1) {
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
		xOffsetSetting = new GUIMinMaxSetting(ConfigManager.getMainConfig().getInt("max-projector-offset") * -1, ConfigManager.getMainConfig().getInt("max-projector-offset"));
		GUIBuildToolSettingSelector xOffsetSelector = new GUIBuildToolSettingSelector(getState(), xOffsetSetting);
		xOffsetSelector.onInit();
		xOffsetSelector.getPos().x = ((contentPane.getWidth() / 3) + (xOffsetSelector.getWidth() / 3)) - 100;
		xOffsetSelector.getPos().y += 50;
		contentPane.getContent(0).attach(xOffsetSelector);
		yOffsetSetting = new GUIMinMaxSetting(ConfigManager.getMainConfig().getInt("max-projector-offset") * -1, ConfigManager.getMainConfig().getInt("max-projector-offset"));
		GUIBuildToolSettingSelector yOffsetSelector = new GUIBuildToolSettingSelector(getState(), yOffsetSetting);
		yOffsetSelector.onInit();
		yOffsetSelector.getPos().x = ((contentPane.getWidth() / 3) + (yOffsetSelector.getWidth() / 3));
		yOffsetSelector.getPos().y += 50;
		contentPane.getContent(0).attach(yOffsetSelector);
		zOffsetSetting = new GUIMinMaxSetting(ConfigManager.getMainConfig().getInt("max-projector-offset") * -1, ConfigManager.getMainConfig().getInt("max-projector-offset"));
		GUIBuildToolSettingSelector zOffsetSelector = new GUIBuildToolSettingSelector(getState(), zOffsetSetting);
		zOffsetSelector.onInit();
		zOffsetSelector.getPos().x = ((contentPane.getWidth() / 3) + (zOffsetSelector.getWidth() / 3)) + 100;
		zOffsetSelector.getPos().y += 50;
		contentPane.getContent(0).attach(zOffsetSelector);
		xRotSetting = new GUIMinMaxSetting(-180, 180);
		GUIBuildToolSettingSelector xRotSelector = new GUIBuildToolSettingSelector(getState(), xRotSetting);
		xRotSelector.onInit();
		xRotSelector.getPos().x = ((contentPane.getWidth() / 3) + (xRotSelector.getWidth() / 3)) - 100;
		xRotSelector.getPos().y += 150;
		contentPane.getContent(0).attach(xRotSelector);
		yRotSetting = new GUIMinMaxSetting(-180, 180);
		GUIBuildToolSettingSelector yRotSelector = new GUIBuildToolSettingSelector(getState(), yRotSetting);
		yRotSelector.onInit();
		yRotSelector.getPos().x = ((contentPane.getWidth() / 3) + (yRotSelector.getWidth() / 3));
		yRotSelector.getPos().y += 150;
		contentPane.getContent(0).attach(yRotSelector);
		zRotSetting = new GUIMinMaxSetting(-180, 180);
		GUIBuildToolSettingSelector zRotSelector = new GUIBuildToolSettingSelector(getState(), zRotSetting);
		zRotSelector.onInit();
		zRotSelector.getPos().x = ((contentPane.getWidth() / 3) + (zRotSelector.getWidth() / 3)) + 100;
		zRotSelector.getPos().y += 150;
		contentPane.getContent(0).attach(zRotSelector);
		scaleSetting = new GUIMinMaxSetting(1, ConfigManager.getMainConfig().getInt("max-projector-scale"));
		GUIBuildToolSettingSelector scaleSelector = new GUIBuildToolSettingSelector(getState(), scaleSetting);
		scaleSelector.onInit();
		scaleSelector.getPos().x = yOffsetSelector.getPos().x;
		scaleSelector.getPos().y += 250;
		contentPane.getContent(0).attach(scaleSelector);
		GUIAdvCheckbox holographicSetting = new GUIAdvCheckbox(getState(), contentPane.getContent(0), new CheckboxResult() {
			@Override
			public boolean getCurrentValue() {
				return holographic;
			}

			@Override
			public void setCurrentValue(boolean b) {
				holographic = b;
			}

			@Override
			public boolean getDefault() {
				return true;
			}

			@Override
			public CheckboxCallback initCallback() {
				return null;
			}

			@Override
			public String getName() {
				return "Holographic";
			}

			@Override
			public String getToolTipText() {
				return "Whether to use the hologram shader.";
			}
		});
		holographicSetting.onInit();
		holographicSetting.getPos().x = yOffsetSelector.getPos().x;
		holographicSetting.getPos().y = scaleSelector.getPos().y + 60;
		contentPane.getContent(0).attach(holographicSetting);
		GUITextButton copySettingsButton = new GUITextButton(getState(), 150, 30, GUITextButton.ColorPalette.OK, "COPY SETTINGS", new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse() && guiElement != null && guiElement.getUserPointer() != null && guiElement.getUserPointer().equals("COPY")) {
					getState().getController().queueUIAudio("0022_menu_ui - select 1");
					DerpsDecor.getInstance().clipboard.setClipboard(getValues());
				}
			}

			@Override
			public boolean isOccluded() {
				return false;
			}
		});
		copySettingsButton.setUserPointer("COPY");
		copySettingsButton.onInit();
		copySettingsButton.getPos().x = xOffsetSelector.getPos().x - 60;
		copySettingsButton.getPos().y = scaleSelector.getPos().y;
		contentPane.getContent(0).attach(copySettingsButton);
		GUITextButton pasteSettingsButton = new GUITextButton(getState(), 150, 30, GUITextButton.ColorPalette.OK, "PASTE SETTINGS", new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse() && guiElement != null && guiElement.getUserPointer() != null && guiElement.getUserPointer().equals("PASTE")) {
					getState().getController().queueUIAudio("0022_menu_ui - select 2");
					setValues(DerpsDecor.getInstance().clipboard.getClipboard());
				}
			}

			@Override
			public boolean isOccluded() {
				return false;
			}
		});
		pasteSettingsButton.setUserPointer("PASTE");
		pasteSettingsButton.onInit();
		pasteSettingsButton.getPos().x = zOffsetSelector.getPos().x;
		pasteSettingsButton.getPos().y = scaleSelector.getPos().y;
		contentPane.getContent(0).attach(pasteSettingsButton);
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

	public String getValues() {
		return getXOffset() + "~" + getYOffset() + "~" + getZOffset() + "~" + getXRot() + "~" + getYRot() + "~" + getZRot() + "~" + getScaleSetting() + "~" + getText() + "~" + getHolographic();
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

	public String getText() {
		if(textInput.getText() == null) return "";
		else return textInput.getText();
	}

	public void setText(String text) {
		if(text != null) textInput.setTextWithoutCallback(text);
		else textInput.setTextWithoutCallback("");
	}

	public boolean getHolographic() {
		return holographic;
	}

	public void setHolographic(boolean holographic) {
		this.holographic = holographic;
	}

	public void setValues(String s) {
		if(s != null && !s.equals("")) {
			try {
				String[] values = s.split("~");
				setXOffset(Integer.parseInt(values[0]));
				setYOffset(Integer.parseInt(values[1]));
				setZOffset(Integer.parseInt(values[2]));
				setXRot(Integer.parseInt(values[3]));
				setYRot(Integer.parseInt(values[4]));
				setZRot(Integer.parseInt(values[5]));
				setScaleSetting(Integer.parseInt(values[6]));
				setText(values[7]);
				setHolographic(Boolean.parseBoolean(values[8]));
			} catch(Exception ignored) {
			}
		}
	}
}
