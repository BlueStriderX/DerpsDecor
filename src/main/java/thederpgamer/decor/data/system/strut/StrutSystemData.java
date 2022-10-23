package thederpgamer.decor.data.system.strut;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.tuple.MutablePair;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/16/2022]
 */
public class StrutSystemData {
  public ConcurrentHashMap<MutablePair<Long, Long>, StrutData> map;

  public StrutSystemData() {
    map = new ConcurrentHashMap<>();
  }
}
