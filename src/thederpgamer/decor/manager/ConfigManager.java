package thederpgamer.decor.manager;

import api.mod.config.FileConfiguration;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.utils.MathUtils;
import java.math.RoundingMode;

/**
 * Manages mod config files and values.
 *
 * @author TheDerpGamer
 * @since 06/07/2021
 */
public class ConfigManager {

    //Main Config
    private static FileConfiguration mainConfig;
    private static final String[] defaultMainConfig = {
            "debug-mode: false",
            "max-world-logs: 5",
            "max-display-draw-distance: 75",
            "max-projector-scale: 30",
            "max-projector-offset: 30",
            "image-filter-mode: blacklist",
            "image-filter: porn,hentai,sex,nsfw,r34",
            "max-strut-length: 20",
            "max-strut-connections: 5",
            "rounding-mode: HALF_EVEN"
    };

    public static void initialize(DerpsDecor instance) {
        mainConfig = instance.getConfig("config");
        mainConfig.saveDefault(defaultMainConfig);
        try {
            MathUtils.roundingMode = RoundingMode.valueOf(mainConfig.getString("rounding-mode"));
        } catch(Exception exception) {
            MathUtils.roundingMode = RoundingMode.HALF_EVEN;
        }
    }

    public static FileConfiguration getMainConfig() {
        return mainConfig;
    }
}
