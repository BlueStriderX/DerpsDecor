package thederpgamer.decor.data.drawdata;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/20/2021]
 */
public class TextProjectorDrawMap {

  public ConcurrentHashMap<Long, TextProjectorDrawData> map;

  public TextProjectorDrawMap() {
    map = new ConcurrentHashMap<>();
  }
}
