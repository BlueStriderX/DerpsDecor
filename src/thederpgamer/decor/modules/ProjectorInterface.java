package thederpgamer.decor.modules;

import thederpgamer.decor.data.projector.ProjectorDrawData;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public interface ProjectorInterface {

    short getProjectorId();
    ProjectorDrawData getDrawData(long indexAndOrientation);
    void setDrawData(long indexAndOrientation, ProjectorDrawData drawData);
}
