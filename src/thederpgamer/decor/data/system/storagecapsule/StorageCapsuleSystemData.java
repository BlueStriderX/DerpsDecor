package thederpgamer.decor.data.system.storagecapsule;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/16/2022]
 */
public class StorageCapsuleSystemData {
	public ConcurrentHashMap<Long, StorageCapsuleData> map;

	public StorageCapsuleSystemData() {
		map = new ConcurrentHashMap<>();
	}

	public StorageCapsuleSystemData(ConcurrentHashMap<Long, StorageCapsuleData> map) {
		this.map = map;
	}
}
