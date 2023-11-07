package thederpgamer.decor.drawer;

import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.utils.draw.ModWorldDrawer;

import java.util.Arrays;

/**
 * Manages all mod world drawers.
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public class GlobalDrawManager {
	private static final ModWorldDrawer[] worldDrawers = new ModWorldDrawer[4];

	public static void initialize(RegisterWorldDrawersEvent event) {
		worldDrawers[0] = new ProjectorDrawer();
		worldDrawers[1] = new NPCDrawer();
		worldDrawers[2] = new StorageCapsuleDrawer();
		worldDrawers[3] = new StrutDrawer();
		event.getModDrawables().addAll(Arrays.asList(worldDrawers));
	}

	public static ProjectorDrawer getProjectorDrawer() {
		return (ProjectorDrawer) worldDrawers[0];
	}

	public static NPCDrawer getNPCDrawer() {
		return (NPCDrawer) worldDrawers[1];
	}

	public static StorageCapsuleDrawer getStorageCapsuleDrawer() {
		return (StorageCapsuleDrawer) worldDrawers[2];
	}

	public static StrutDrawer getStrutDrawer() {
		return (StrutDrawer) worldDrawers[3];
	}
}
