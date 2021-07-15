package thederpgamer.decor.gui.panel;

import org.schema.game.client.controller.manager.ingame.AbstractSizeSetting;
import thederpgamer.decor.manager.ConfigManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjectorOffsetSetting extends AbstractSizeSetting {

    public HoloProjectorOffsetSetting() {
        set(0);
    }

    @Override
    public void dec() {
        this.setting = Math.max(this.getMin(), this.setting - 1);
        if(this.guiCallBack != null) this.guiCallBack.settingChanged(this.setting);
    }

    @Override
    public void inc() {
        this.setting = Math.min(this.getMax(), this.setting + 1);
        if(this.guiCallBack != null) this.guiCallBack.settingChanged(this.setting);
    }

    @Override
    public int getMin() {
        return ConfigManager.getMainConfig().getInt("max-image-offset") * -1;
    }

    @Override
    public int getMax() {
        return ConfigManager.getMainConfig().getInt("max-image-offset");
    }
}
