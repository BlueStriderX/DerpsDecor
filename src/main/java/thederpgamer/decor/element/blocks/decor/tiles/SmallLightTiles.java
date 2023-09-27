package thederpgamer.decor.element.blocks.decor.tiles;

import api.config.BlockConfig;
import thederpgamer.decor.manager.ResourceManager;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.Block;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class SmallLightTiles  extends Block {

	public SmallLightTiles() {
		super("Small Light Tiles", ElementKeyMap.getInfo(161).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription("Decorative tile blocks that can be used to create a variety of different floor designs.");
		blockInfo.setInRecipe(true);
		blockInfo.setShoppable(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(161).price);
		blockInfo.setInventoryGroup("light-tiles");
		blockInfo.styleIds = new short[] {ElementManager.getBlock("Small Light Tiles Wedge").getId()};
		if(GraphicsContext.initialized) {
			try {
				short textureId = (short) ResourceManager.getTexture("small-light-tiles").getTextureId();
				blockInfo.setTextureId(new short[] {textureId, textureId, textureId, textureId, textureId, textureId});
				blockInfo.setBuildIconNum(ResourceManager.getIcon("small-light-tiles-icon").getTextureId());
			} catch(Exception ignored) {}
		}
		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(161).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(161).getFactoryBakeTime(), new FactoryResource(1, ElementManager.getBlock("Large Light Tiles").getId()));
		BlockConfig.add(blockInfo);
	}
}
