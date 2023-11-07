package thederpgamer.decor.data.system.crew;

import java.util.HashMap;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class CrewModuleData {

	public HashMap<Long, CrewData> crewDataMap;

	public CrewModuleData() {
		crewDataMap = new HashMap<>();
	}

	public void removeBlock(long indexAndOrientation) {
		try {
			crewDataMap.get(indexAndOrientation).removeCrew();
			crewDataMap.remove(indexAndOrientation);
		} catch(Exception ignored) {}
	}

	public void putData(long indexAndOrientation, CrewData data) {
		crewDataMap.put(indexAndOrientation, data);
	}

	/**
	 * Moves all crew members to their default position and resets their animations.
	 */
	public void resetAll() {
		for(CrewData crewData : crewDataMap.values()) crewData.recall();
	}

	public CrewData getData(long indexAndOrientation) {
		return crewDataMap.get(indexAndOrientation);
	}

	public boolean indexExists(long indexAndOrientation) {
		return crewDataMap.containsKey(indexAndOrientation);
	}

	public void handleUpdates() {
		for(CrewData crewData : crewDataMap.values()) {
			if(crewData.needsUpdate) crewData.updateCrew();
		}
	}
}
