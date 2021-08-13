package thederpgamer.decor.utils;

import api.common.GameServer;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.game.common.data.player.PlayerState;
import javax.vecmath.Vector3f;
import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/13/2021
 */
public class PlayerUtils {

    public static Transform getPlayerTransform(PlayerState playerState) {
        Transform transform = new Transform();
        playerState.getWordTransform(transform);
        return transform;
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
