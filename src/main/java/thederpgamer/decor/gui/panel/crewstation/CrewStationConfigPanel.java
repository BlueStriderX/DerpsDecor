package thederpgamer.decor.gui.panel.crewstation;

import api.utils.gui.GUIInputDialogPanel;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.gui.buildtools.GUIBuildToolSettingSelector;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.animation.structure.classes.AnimationIndex;
import org.schema.schine.graphicsengine.animation.structure.classes.AnimationIndexElement;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.decor.data.system.crew.CrewData;
import thederpgamer.decor.gui.elements.GUIMinMaxSetting;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class CrewStationConfigPanel extends GUIInputDialogPanel {
	private GUIActivatableTextBar nameInput;
	private GUIDropDownList animationDropDown;
	private GUIMinMaxSetting xOffsetSetting;
	private GUIMinMaxSetting yOffsetSetting;
	private GUIMinMaxSetting zOffsetSetting;
	private final GUICallback guiCallback;
	private final CrewData crewData;
	private final SegmentPiece segmentPiece;

	public CrewStationConfigPanel(InputState inputState, GUICallback guiCallback, CrewData crewData) {
		super(inputState, "crew_station_config_panel", "NPC Station Configuration", "", 610, 530, guiCallback);
		this.guiCallback = guiCallback;
		this.crewData = crewData;
		segmentPiece = crewData.getSegmentPiece();
		setCrewName(crewData.crewName);
		setAnimationName(crewData.animationName);
		Vector3i piecePos = segmentPiece.getAbsolutePos(new Vector3i());
		setOffset(new Vector3i(piecePos.x - crewData.spawnPos.x, piecePos.y - crewData.spawnPos.y, piecePos.z - crewData.spawnPos.z));
	}

	@Override
	public void onInit() {
		super.onInit();
		GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
		contentPane.setTextBoxHeightLast(500);
		nameInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.MEDIUM, 128, 1, "Crew Name", contentPane.getContent(0), new TextCallback() {
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
		nameInput.onInit();
		contentPane.getContent(0).attach(nameInput);

		animationDropDown = createDropdown(new GUIListFilterDropdown<String, AnimationIndexElement>(AnimationIndex.animations) {
			@Override
			public boolean isOk(AnimationIndexElement animationIndexElement, String s) {
				return false;
			}
		}, new CreateGUIElementInterface<AnimationIndexElement>() {
			@Override
			public GUIElement create(AnimationIndexElement element) {
				GUIAncor anchor = new GUIAncor(getState(), 585, 24.0F);
				GUITextOverlayTableDropDown dropDown = new GUITextOverlayTableDropDown(585, 10, getState());
				dropDown.setTextSimple(element.toString().replaceAll("_", " "));
				dropDown.setPos(4.0F, 4.0F, 0.0F);
				dropDown.setUserPointer(element);
				anchor.setUserPointer(element);
				anchor.attach(dropDown);
				return anchor;
			}

			@Override
			public GUIElement createNeutral() {
				return null;
			}

			/*
			@Override
			public GUIElement createNeutral() {
				GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
				GUITextOverlayTableDropDown dropDown = new GUITextOverlayTableDropDown(10, 10, getState());
				dropDown.setTextSimple(AnimationIndex.IDLING_FLOATING.toString().replaceAll("_", " "));
				dropDown.setPos(4.0F, 4.0F, 0.0F);
				dropDown.setUserPointer(AnimationIndex.IDLING_FLOATING);
				anchor.setUserPointer(AnimationIndex.IDLING_FLOATING);
				anchor.attach(dropDown);
				return anchor;
			}
			 */
		});
		animationDropDown.onInit();
		animationDropDown.getPos().y += 30;
		contentPane.getContent(0).attach(animationDropDown);

		GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, contentPane.getContent(0));
		buttonPane.onInit();

		(buttonPane.addButton(0, 0, "RECALL", GUIHorizontalArea.HButtonColor.PINK, guiCallback, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return true;
			}
		})).setUserPointer("RECALL");
		(buttonPane.addButton(1, 0, "TOGGLE LOOPING", GUIHorizontalArea.HButtonColor.BLUE, guiCallback, new GUIActivationHighlightCallback() {
			@Override
			public boolean isHighlighted(InputState inputState) {
				return crewData.looping;
			}

			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return true;
			}
		})).setUserPointer("TOGGLE LOOPING");

		buttonPane.getPos().y += 60;
		contentPane.getContent(0).attach(buttonPane);

		xOffsetSetting = new GUIMinMaxSetting(-10, 10);
		GUIBuildToolSettingSelector xOffsetSelector = new GUIBuildToolSettingSelector(getState(), xOffsetSetting);
		xOffsetSelector.onInit();
		xOffsetSelector.getPos().x = ((contentPane.getWidth() / 3) + (xOffsetSelector.getWidth() / 3)) - 100;
		xOffsetSelector.getPos().y += 80;
		contentPane.getContent(0).attach(xOffsetSelector);

		yOffsetSetting = new GUIMinMaxSetting(-10, 10);
		GUIBuildToolSettingSelector yOffsetSelector = new GUIBuildToolSettingSelector(getState(), yOffsetSetting);
		yOffsetSelector.onInit();
		yOffsetSelector.getPos().x = ((contentPane.getWidth() / 3) + (yOffsetSelector.getWidth() / 3));
		yOffsetSelector.getPos().y += 80;
		contentPane.getContent(0).attach(yOffsetSelector);

		zOffsetSetting = new GUIMinMaxSetting(-10, 10);
		GUIBuildToolSettingSelector zOffsetSelector = new GUIBuildToolSettingSelector(getState(), zOffsetSetting);
		zOffsetSelector.onInit();
		zOffsetSelector.getPos().x = ((contentPane.getWidth() / 3) + (zOffsetSelector.getWidth() / 3)) + 100;
		zOffsetSelector.getPos().y += 80;
		contentPane.getContent(0).attach(zOffsetSelector);
	}

	private GUIDropDownList createDropdown(final GUIListFilterDropdown<String, AnimationIndexElement> guiListFilterDropdown, CreateGUIElementInterface<AnimationIndexElement> factory) {
		GUIElement[] fields;
		GUIElement neutral = factory.createNeutral();
		if(neutral != null) {
			fields = new GUIElement[guiListFilterDropdown.values.length + 1];
			fields[0] = neutral;
			for(int i = 0; i < guiListFilterDropdown.values.length; i++) {
				AnimationIndexElement o = guiListFilterDropdown.values[i];
				fields[i + 1] = factory.create(o);
			}
		} else {
			fields = new GUIElement[guiListFilterDropdown.values.length];
			for(int i = 0; i < guiListFilterDropdown.values.length; i++) {
				AnimationIndexElement o = guiListFilterDropdown.values[i];
				fields[i] = factory.create(o);
			}
		}
		GUIDropDownList list = new GUIDropDownList(getState(), 600, 24, 500, new DropDownCallback() {
			@Override
			public void onSelectionChanged(GUIListElement element) {
				if(element.getContent().getUserPointer() != null) guiListFilterDropdown.setFilter((AnimationIndexElement) element.getContent().getUserPointer());
			}
		}, fields);
		list.dependend = this;
		list.onInit();
		return list;
	}

	public String getCrewName() {
		return nameInput.getText();
	}

	public void setCrewName(String crewName) {
		nameInput.setText(crewName);
	}

	public String getAnimationName() {
		return animationDropDown.getSelectedElement().getContent().getUserPointer().toString();
	}

	public void setAnimationName(String animationName) {
		for(GUIListElement element : animationDropDown) {
			if(element.getContent().getUserPointer().toString().equals(animationName)) {
				animationDropDown.setSelectedElement(element);
				break;
			}
		}
	}

	public void setOffset(Vector3i offset) {
		xOffsetSetting.set(offset.x);
		yOffsetSetting.set(offset.y);
		zOffsetSetting.set(offset.z);
	}

	public Vector3i getOffset() {
		return new Vector3i(segmentPiece.getAbsolutePos(new Vector3i()).x - crewData.spawnPos.x, segmentPiece.getAbsolutePos(new Vector3i()).y - crewData.spawnPos.y, segmentPiece.getAbsolutePos(new Vector3i()).z - crewData.spawnPos.z);
	}
}
