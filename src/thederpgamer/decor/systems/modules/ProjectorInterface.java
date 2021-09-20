package thederpgamer.decor.systems.modules;

import org.schema.game.common.data.SegmentPiece;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public interface ProjectorInterface {

    ConcurrentHashMap<Long, Object> getProjectorMap();
    short getProjectorId();
    void removeDrawData(long indexAndOrientation);
    Object getDrawData(long indexAndOrientation);
    Object getDrawData(SegmentPiece segmentPiece);
    void setDrawData(long indexAndOrientation, Object drawData);
}
