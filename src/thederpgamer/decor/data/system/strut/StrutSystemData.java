package thederpgamer.decor.data.system.strut;

import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/16/2022]
 */
public class StrutSystemData {
	public ConcurrentHashMap<Pair<Long, Long>, StrutData> map;

	public StrutSystemData() {
		map = new ConcurrentHashMap<>();
	}

	public StrutSystemData(ConcurrentHashMap<Pair<Long, Long>, StrutData> map) {
		this.map = map;
	}
}
