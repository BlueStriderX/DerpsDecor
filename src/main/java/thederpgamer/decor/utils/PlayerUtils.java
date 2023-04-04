package thederpgamer.decor.utils;

import api.common.GameClient;
import api.common.GameServer;
import api.utils.StarRunnable;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.inventory.InventorySlot;
import thederpgamer.decor.DerpsDecor;

import javax.vecmath.Vector3f;
import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/13/2021
 */
public class PlayerUtils {
	public static final int NONE = 0;
	public static final int FIRST = 1;
	public static final int SECOND = 2;
	public static int connectingStrut = NONE;
	public static long currentConnectionIndex = 0;

	public static void startConnectionRunner() {
		connectingStrut = FIRST;
		new StarRunnable() {
			@Override
			public void run() {
				if(connectingStrut == SECOND || !GameClient.getClientState().isInAnyStructureBuildMode() || PlayerUtils.getSelectedSlot().isEmpty() || !ElementKeyMap.getInfo(PlayerUtils.getSelectedSlot().getType()).getName().toLowerCase().contains("paint")) {
					PlayerUtils.connectingStrut = PlayerUtils.NONE;
					cancel();
				}
			}
		}.runTimer(DerpsDecor.getInstance(), 30);
	}

	public static InventorySlot getSelectedSlot() {
		return GameClient.getClientPlayerState().getInventory().getSlot(GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getSelectedSlot());
	}

	public static HashMap<PlayerState, Float> getPlayersInRange(Transform transform, float maxDistance) {
		HashMap<PlayerState, Float> playerMap = new HashMap<>();
		Vector3f pos = new Vector3f(transform.origin);
		for(PlayerState playerState : GameServer.getServerState().getPlayerStatesByName().values()) {
			Transform playerTransform = new Transform();
			playerState.getWordTransform(playerTransform);
			Vector3f playerPos = new Vector3f(playerTransform.origin);
			float distance = Math.abs(Vector3fTools.distance(pos.x, pos.y, pos.z, playerPos.x, playerPos.y, playerPos.z));
			if(maxDistance == -1 || distance <= maxDistance) playerMap.put(playerState, distance);
		}
		return playerMap;
	}
}
