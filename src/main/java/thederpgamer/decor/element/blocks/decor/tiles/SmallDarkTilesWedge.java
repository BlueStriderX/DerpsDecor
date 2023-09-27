package thederpgamer.decor.element.blocks.decor.tiles;

import api.config.BlockConfig;
import thederpgamer.decor.manager.ResourceManager;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.Block;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class SmallDarkTilesWedge extends Block {
	public SmallDarkTilesWedge() {
		super("Small Dark Tiles Wedge", ElementKeyMap.getInfo(205).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription("Decorative tile blocks that can be used to create a variety of different floor designs.");
		blockInfo.setInRecipe(false);
		blockInfo.setShoppable(false);
		blockInfo.setBlockStyle(BlockStyle.WEDGE.id);
		blockInfo.setInventoryGroup("dark-tiles");
		blockInfo.setOrientatable(true);
		blockInfo.sourceReference = ElementManager.getInfo("Small Dark Tiles").getId();
		if(GraphicsContext.initialized) {
			try {
				short textureId = (short) ResourceManager.getTexture("small-dark-tiles").getTextureId();
				blockInfo.setTextureId(new short[] {textureId, textureId, textureId, textureId, textureId, textureId});
				blockInfo.setBuildIconNum(ResourceManager.getIcon("small-dark-tiles-wedge-icon").getTextureId());
			} catch(Exception ignored) {}
		}
		BlockConfig.add(blockInfo);
	}
}
