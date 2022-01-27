package org.schema.game.client.view.gui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Locale;
import org.schema.game.client.view.mainmenu.DialogInput;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIScrollablePanel;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.decor.manager.ResourceManager;

/**
 * Modified version of GUIQuickReferencePanel.
 *
 * @author Schema (original), TheDerpGamer
 */
public class GUIQuickReferencePanel extends GUIElement implements GUIActiveInterface {

  public GUIMainWindow mainPanel;
  private DialogInput diag;

  private List<GUIElement> toCleanUp = new ObjectArrayList<GUIElement>();

  private boolean init;

  public GUIQuickReferencePanel(InputState state, DialogInput diag) {
    super(state);
    this.diag = diag;
  }

  @Override
  public void cleanUp() {
    for (GUIElement e : toCleanUp) {
      e.cleanUp();
    }
    toCleanUp.clear();
  }

  @Override
  public void draw() {
    if (!init) {
      onInit();
    }
    GlUtil.glPushMatrix();
    transform();

    mainPanel.draw();

    GlUtil.glPopMatrix();
  }

  @Override
  public void onInit() {
    if (init) {
      return;
    }
    mainPanel =
        new GUIMainWindow(getState(), 880, GLFrame.getHeight() - 200, 300, 24, "QUICK_REF_DGL");
    mainPanel.onInit();

    mainPanel.clearTabs();

    final String path =
        EngineSettings.LANGUAGE_PACK
                .getCurrentState()
                .toString()
                .toLowerCase(Locale.ENGLISH)
                .startsWith("chinese")
            ? "infographics/power2.0/chinese/"
            : "infographics/power2.0/english/";
    createHelpTab(Lng.str("Reactors"), path + "1_Reactors");
    createHelpTab(Lng.str("Chambers"), path + "2_Chambers");
    createHelpTab(Lng.str("Shields"), path + "3_Shields");
    createHelpTab(Lng.str("Stealth/Recon"), path + "4_SteathRecon");
    createHelpTab(Lng.str("Integrity"), path + "5_Integrity");

    // INSERTED CODE
    createDerpsDecorTab();
    //

    mainPanel.activeInterface = this;

    mainPanel.setCloseCallback(
        new GUICallback() {

          @Override
          public boolean isOccluded() {
            return !isActive();
          }

          @Override
          public void callback(GUIElement callingGuiElement, MouseEvent event) {
            if (event.pressedLeftMouse()) {
              diag.deactivate();
            }
          }
        });

    init = true;
  }

  @Override
  public boolean isInside() {
    return mainPanel.isInside();
  }

  // INSERTED CODE
  public GUIContentPane createDerpsDecorTab() {
    // Todo: Make a StarLoader event hook for mods to add their own tabs under a general "Mods" tab
    GUIContentPane contentPane = mainPanel.addTab("Derps Decor");
    contentPane.setTextBoxHeightLast(48);

    GUITabbedContent tabbedContent = new GUITabbedContent(getState(), contentPane.getContent(0));
    tabbedContent.onInit();

    { // Projectors Tab
      GUIContentPane projectorsTab = tabbedContent.addTab("Projectors");
      projectorsTab.setTextBoxHeightLast(48);

      Sprite sprite = ResourceManager.getSprite("projectors-infographic");
      GUITexDrawableArea drawableArea =
          new GUITexDrawableArea(
              getState(), sprite.getMaterial().getTexture(), ((1024f - 800f) * 0.5f) / 1024f, 0);
      drawableArea.setWidth(800);

      GUIScrollablePanel scrollablePanel =
          new GUIScrollablePanel(10, 10, projectorsTab.getContent(0), getState());
      scrollablePanel.setContent(drawableArea);
      scrollablePanel.onInit();
      projectorsTab.getContent(0).attach(scrollablePanel);
    }

    contentPane.getContent(0).attach(tabbedContent);
    return contentPane;
  }
  //

  private GUIContentPane createHelpTab(String name, String texture) {
    final GUIContentPane t;
    t = mainPanel.addTab(name);
    t.setTextBoxHeightLast(48);
    Sprite sprite = Controller.getResLoader().getSprite(texture);

    final float graphicsWidth = 800f;
    GUITexDrawableArea m =
        new GUITexDrawableArea(
            getState(),
            sprite.getMaterial().getTexture(),
            ((1024f - graphicsWidth) * 0.5f) / 1024f,
            0);
    m.setWidth(800);
    GUIScrollablePanel p = new GUIScrollablePanel(10, 10, t.getContent(0), getState());
    p.setContent(m);
    p.onInit();
    t.getContent(0).attach(p);
    return t;
  }

  @Override
  public float getHeight() {
    return 0;
  }

  @Override
  public float getWidth() {
    return 0;
  }

  @Override
  public boolean isActive() {
    return diag.isActive();
  }
}
