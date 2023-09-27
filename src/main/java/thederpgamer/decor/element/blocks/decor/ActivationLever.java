package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.blocks.ActivationInterface;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.manager.ResourceManager;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class ActivationLever extends Block implements ActivationInterface {
	public ActivationLever() {
		super("Activation Lever", ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription("An toggleable lever that functions identically to an Activation Module.");
		blockInfo.setCanActivate(true);
		blockInfo.setInRecipe(true);
		blockInfo.setShoppable(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).price);
		blockInfo.setOrientatable(true);
		blockInfo.setIndividualSides(6);
		blockInfo.setBlockStyle(BlockStyle.NORMAL24.id);
		blockInfo.lodShapeStyle = ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).lodShapeStyle;
		blockInfo.controlledBy.addAll(ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).controlledBy);
		blockInfo.controlling.addAll(ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).controlling);
		for(short id : ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).controlling) ElementKeyMap.getInfo(id).controlledBy.add(blockInfo.getId());
		for(short id : ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).controlledBy) ElementKeyMap.getInfo(id).controlling.add(blockInfo.getId());
		if(GraphicsContext.initialized) {
			try {
				blockInfo.setBuildIconNum(ResourceManager.getIcon("activation-lever-icon").getTextureId());
				BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "activation_lever_off", "activation_lever_on");
			} catch(Exception ignored) {}
		}
		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(), new FactoryResource(1, ElementKeyMap.TEXT_BOX));
		BlockConfig.add(blockInfo);
	}

	@Override
	public void onPlayerActivation(SegmentPieceActivateByPlayer event) {
		//The event should automatically toggle the state of the block TODO: Verify this
	}

	@Override
	public void onLogicActivation(SegmentPieceActivateEvent event) {
		//The event should automatically toggle the state of the block TODO: Verify this
	}
}
