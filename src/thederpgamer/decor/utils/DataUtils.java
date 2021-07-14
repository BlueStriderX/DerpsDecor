package thederpgamer.decor.utils;

import api.common.GameClient;
import api.common.GameCommon;
import api.mod.config.PersistentObjectUtil;
import api.utils.StarRunnable;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.SegmentPiece;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.ProjectorDrawData;
import thederpgamer.decor.manager.LogManager;
import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/09/2021
 */
public class DataUtils {

    public static HashMap<Integer, ProjectorDrawData> projectorDrawMap = new HashMap<>();

    public static String getResourcesPath() {
        return DerpsDecor.getInstance().getSkeleton().getResourcesFolder().getPath().replace('\\', '/');
    }

    public static String getWorldDataPath() {
        String universeName = GameCommon.getUniqueContextId();
        if(!universeName.contains(":")) {
            return getResourcesPath() + "/data/" + universeName;
        } else {
            try {
                LogManager.logMessage(MessageType.ERROR, "Client " + GameClient.getClientPlayerState().getName() + " attempted to illegally access server data.");
            } catch(Exception ignored) { }
            return null;
        }
    }

    public static ProjectorDrawData getProjectorDrawData(SegmentPiece segmentPiece) {
        return projectorDrawMap.get(ProjectorDrawData.getHashCode(segmentPiece));
    }

    public static void registerNewProjector(final SegmentPiece segmentPiece) {
        new StarRunnable() {
            @Override
            public void run() {
                int hashCode = ProjectorDrawData.getHashCode(segmentPiece);
                projectorDrawMap.remove(hashCode);
                projectorDrawMap.put(hashCode, new ProjectorDrawData(segmentPiece));
            }
        }.runLater(DerpsDecor.getInstance(), 5);
    }

    public static void modProjector(ProjectorDrawData drawData, String senderName) {
        projectorDrawMap.remove(drawData.hashCode());
        projectorDrawMap.put(drawData.hashCode(), drawData);
        LogManager.logMessage(MessageType.INFO, "Player " + senderName + " activated a Holo Projector:\n[entityId = " + drawData.entityId + ", src = " + drawData.src + "]");
    }

    public static void removeProjector(SegmentController controller, Vector3i pos) {
        projectorDrawMap.remove(ProjectorDrawData.getHashCode(controller.getId(), pos.x, pos.y, pos.z));
    }

    public static void save() {
        if(GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer()) {
            PersistentObjectUtil.removeAllObjects(DerpsDecor.getInstance().getSkeleton(), ProjectorDrawData.class);
            for(ProjectorDrawData drawData : projectorDrawMap.values()) PersistentObjectUtil.addObject(DerpsDecor.getInstance().getSkeleton(), drawData);
            PersistentObjectUtil.save(DerpsDecor.getInstance().getSkeleton());
        }
    }
}