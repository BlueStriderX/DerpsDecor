package thederpgamer.decor.element.blocks.decor.tiles;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.manager.ResourceManager;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class LargeDarkTiles extends Block {

	public LargeDarkTiles() {
		super("Large Dark Tiles", ElementKeyMap.getInfo(205).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription("Decorative tile blocks that can be used to create a variety of different floor designs.");
		blockInfo.setInRecipe(true);
		blockInfo.setShoppable(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(205).price);
		blockInfo.setInventoryGroup("dark-tiles");
		blockInfo.styleIds = new short[] {ElementManager.getBlock("Large Dark Tiles Wedge").getId()};
		if(GraphicsContext.initialized) {
			try {
				short textureId = (short) ResourceManager.getTexture("large-dark-tiles").getTextureId();
				blockInfo.setTextureId(new short[] {textureId, textureId, textureId, textureId, textureId, textureId});
				blockInfo.setBuildIconNum(ResourceManager.getTexture("large-dark-tiles-icon").getTextureId());
			} catch(Exception ignored) {}
		}
		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(205).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(205).getFactoryBakeTime(), new FactoryResource(1, (short) 205));
		BlockConfig.add(blockInfo);
	}
}