package thederpgamer.decor.element.blocks.decor;

import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.common.data.element.ElementKeyMap;
import thederpgamer.decor.element.blocks.ActivationInterface;
import thederpgamer.decor.element.blocks.Block;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class Chair extends Block implements ActivationInterface {
	public Chair() {
		super("Chair", ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).getType());
	}

	@Override
	public void initialize() {
	}

	@Override
	public void onPlayerActivation(SegmentPieceActivateByPlayer event) {
	}

	@Override
	public void onLogicActivation(SegmentPieceActivateEvent event) {
	}
}
