package thederpgamer.decor.systems.modules;

import org.schema.game.common.data.SegmentPiece;
import thederpgamer.decor.data.drawdata.ProjectorDrawData;

import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public interface ProjectorInterface {

    ArrayList<ProjectorDrawData> getProjectorList();
    short getProjectorId();
    void removeDrawData(long indexAndOrientation);
    ProjectorDrawData getDrawData(long indexAndOrientation);
    ProjectorDrawData getDrawData(SegmentPiece segmentPiece);
    void setDrawData(long indexAndOrientation, ProjectorDrawData drawData);
}
