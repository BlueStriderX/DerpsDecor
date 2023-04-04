package thederpgamer.decor.data.system.storagecapsule;

import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/16/2022]
 */
public class StorageCapsuleSystemData {
	public HashMap<Long, StorageCapsuleData> map;

	public StorageCapsuleSystemData() {
		map = new HashMap<>();
	}

	public StorageCapsuleSystemData(HashMap<Long, StorageCapsuleData> map) {
		this.map = map;
	}
}
