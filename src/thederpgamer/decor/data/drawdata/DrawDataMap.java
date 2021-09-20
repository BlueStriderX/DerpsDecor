package thederpgamer.decor.data.drawdata;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 */
public class DrawDataMap {

    public ConcurrentHashMap<Long, Object> map;

    public DrawDataMap() {
        map = new ConcurrentHashMap<>();
    }

    public DrawDataMap(ConcurrentHashMap<Long, Object> map) {
        this.map = map;
    }
}