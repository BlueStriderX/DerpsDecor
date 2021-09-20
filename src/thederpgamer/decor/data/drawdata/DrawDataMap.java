package thederpgamer.decor.data.drawdata;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 */
public class DrawDataMap {

    public ConcurrentHashMap<Long, ProjectorDrawData> map;

    public DrawDataMap() {
        map = new ConcurrentHashMap<>();
    }

    public DrawDataMap(ConcurrentHashMap<Long, ProjectorDrawData> map) {
        this.map = map;
    }
}