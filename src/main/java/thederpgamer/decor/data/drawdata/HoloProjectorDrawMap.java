package thederpgamer.decor.data.drawdata;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 */
public class HoloProjectorDrawMap {

  public ConcurrentHashMap<Long, HoloProjectorDrawData> map;

  public HoloProjectorDrawMap() {
    map = new ConcurrentHashMap<>();
  }
}
