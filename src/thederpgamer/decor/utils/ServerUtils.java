package thederpgamer.decor.utils;

import org.schema.game.server.data.ServerConfig;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/13/2021
 */
public class ServerUtils {

    public static int getSectorSize() {
        return (int) ServerConfig.SECTOR_SIZE.getCurrentState();
    }
}
