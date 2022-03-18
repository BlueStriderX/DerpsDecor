package thederpgamer.decor.data.drawdata;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/03/2022]
 */
public class HoloTableDrawMap {

	public ConcurrentHashMap<Long, HoloTableDrawData> map;

	public HoloTableDrawMap() {
		map = new ConcurrentHashMap<>();
	}

	public HoloTableDrawMap(ConcurrentHashMap<Long, HoloTableDrawData> map) {
		this.map = map;
	}
}
