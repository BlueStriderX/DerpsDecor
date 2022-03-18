package thederpgamer.decor.manager;

import api.mod.config.FileConfiguration;
import thederpgamer.decor.DerpsDecor;

/**
 * Manages mod config files and values.
 *
 * @author TheDerpGamer
 */
public class ConfigManager {

  // Main Config
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
    "max-strut-connections: 4",
    "max-png-dim: 1024",
    "max-gif-dim: 1024",
    "max-gif-frames: 80",
    "max-projector-draws-per-frame: 120"
  };

  public static void initialize(DerpsDecor instance) {
    mainConfig = instance.getConfig("config");
    mainConfig.saveDefault(defaultMainConfig);
  }

  public static FileConfiguration getMainConfig() {
    return mainConfig;
  }
}
