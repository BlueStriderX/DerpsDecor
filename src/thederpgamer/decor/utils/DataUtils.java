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
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.LogManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/09/2021
 */
public class DataUtils {

    public static HashMap<SegmentPiece, ProjectorDrawData> projectorDrawMap = new HashMap<>();

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
        for(Object object : PersistentObjectUtil.getObjects(DerpsDecor.getInstance().getSkeleton(), ProjectorDrawData.class)) {
            ProjectorDrawData drawData = (ProjectorDrawData) object;
            if(drawData.entityId == segmentPiece.getSegmentController().getDbId() && drawData.index == segmentPiece.getAbsoluteIndex()) {
                return drawData;
            }
        }
        return new ProjectorDrawData(segmentPiece);
    }

    public static void registerNewProjector(final SegmentPiece segmentPiece) {
        new StarRunnable() {
            @Override
            public void run() {
                if(!projectorDrawMap.containsKey(segmentPiece)) projectorDrawMap.put(segmentPiece, getProjectorDrawData(segmentPiece));
            }
        }.runLater(DerpsDecor.getInstance(), 5);
    }

    public static void removeProjector(SegmentController controller, Vector3i pos) {
        ArrayList<ProjectorDrawData> toRemove = new ArrayList<>();
        for(Object object : PersistentObjectUtil.getObjects(DerpsDecor.getInstance().getSkeleton(), ProjectorDrawData.class)) {
            try {
                ProjectorDrawData drawData = (ProjectorDrawData) object;
                if(drawData.entityId == controller.getDbId() && (!controller.getSegmentBuffer().existsPointUnsave(pos) || (controller.getSegmentBuffer().getPointUnsave(pos) != null && controller.getSegmentBuffer().getPointUnsave(pos).getType() != Objects.requireNonNull(ElementManager.getBlock("Holo Projector")).getId()))) {
                    toRemove.add(drawData);
                }
            } catch(Exception ignored) { }
        }
        for(ProjectorDrawData drawData : toRemove) {
            try {
                projectorDrawMap.remove(controller.getSegmentBuffer().getPointUnsave(drawData.index));
                PersistentObjectUtil.removeObject(DerpsDecor.getInstance().getSkeleton(), drawData);
            } catch(Exception ignored) { }
        }
        PersistentObjectUtil.save(DerpsDecor.getInstance().getSkeleton());
    }
}