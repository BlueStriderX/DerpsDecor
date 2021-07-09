package thederpgamer.decor.gui.panel;

import org.schema.game.client.controller.manager.ingame.AbstractSizeSetting;
import thederpgamer.decor.manager.ConfigManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjectorScaleSetting extends AbstractSizeSetting {

    public HoloProjectorScaleSetting() {
        set(1.0f);
    }

    @Override
    public int getMin() {
        return 1;
    }

    @Override
    public int getMax() {
        return (int) ConfigManager.getMainConfig().getDouble("max-image-scale") * 10;
    }

    public float getValue() {
        return setting / 10f;
    }
}
