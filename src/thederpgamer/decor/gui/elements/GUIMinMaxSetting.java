package thederpgamer.decor.gui.elements;

import org.schema.game.client.controller.manager.ingame.AbstractSizeSetting;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class GUIMinMaxSetting extends AbstractSizeSetting {

  private final int min;
  private final int max;

  public GUIMinMaxSetting(int min, int max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public void dec() {
    setting = Math.max(min, setting - 1);
    if (guiCallBack != null) guiCallBack.settingChanged(setting);
  }

  @Override
  public void inc() {
    setting = Math.min(max, setting + 1);
    if (guiCallBack != null) guiCallBack.settingChanged(setting);
  }

  @Override
  public int getMin() {
    return min;
  }

  @Override
  public int getMax() {
    return max;
  }
}
