package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.utils.BlockIconUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public class HoloTable extends Block {

	public HoloTable() {
		super("Holo Table", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription("A decorative hologram table.");
		blockInfo.setInRecipe(true);
		blockInfo.setCanActivate(true);
		blockInfo.setShoppable(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
		blockInfo.setOrientatable(true);
		blockInfo.setIndividualSides(6);
		blockInfo.setBlockStyle(BlockStyle.NORMAL.id);
		blockInfo.lodShapeStyle = 1;
		blockInfo.sideTexturesPointToOrientation = false;

		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(), new FactoryResource(1, ElementKeyMap.TEXT_BOX), new FactoryResource(1, (short) 660));
		BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "holo_table", null);
		BlockConfig.add(blockInfo);
	}

	@Override
	public void createGraphics() {
		BlockIconUtils.createBlockIcon(blockInfo);
	}
}
