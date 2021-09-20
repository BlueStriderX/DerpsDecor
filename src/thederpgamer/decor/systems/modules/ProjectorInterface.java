package thederpgamer.decor.systems.modules;

import org.schema.game.common.data.SegmentPiece;
import thederpgamer.decor.data.drawdata.ProjectorDrawData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public interface ProjectorInterface {

    ConcurrentHashMap<Long, ProjectorDrawData> getProjectorMap();
    short getProjectorId();
    ProjectorDrawData getDrawData(long indexAndOrientation);
    ProjectorDrawData getDrawData(SegmentPiece segmentPiece);
    void setDrawData(long indexAndOrientation, ProjectorDrawData drawData);
}
