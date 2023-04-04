package thederpgamer.decor.utils;

import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [02/25/2022]
 */
public class ModuleUtils {
	public static ManagerContainer<?> getManagerContainer(SegmentController segmentController) {
		if(segmentController instanceof Ship && segmentController.getType().equals(SimpleTransformableSendableObject.EntityType.SHIP)) {
			return ((Ship) segmentController).getManagerContainer();
		} else if(segmentController instanceof SpaceStation && segmentController.getType().equals(SimpleTransformableSendableObject.EntityType.SPACE_STATION)) {
			return ((SpaceStation) segmentController).getManagerContainer();
		} else return null;
	}
}
