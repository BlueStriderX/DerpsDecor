package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.manager.ResourceManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/28/2021]
 */
public class CakeBlock extends Block {

  public CakeBlock() {
    super("Cake", ElementManager.getCategory("Food"));
  }

  @Override
  public void initialize() {
    if (GraphicsContext.initialized) {
      try {
        blockInfo.setBuildIconNum(ResourceManager.getTexture("cake-icon").getTextureId());
      } catch (Exception ignored) {
      }
    }

    blockInfo.setDescription(
        "A fresh baked cake, just for you. Restores health if interacted with, and tastes"
            + " delicious!");
    blockInfo.setCanActivate(true);
    blockInfo.setInRecipe(true);
    blockInfo.setShoppable(true);
    blockInfo.setPrice(500);
    blockInfo.setOrientatable(true);
    blockInfo.setBlockStyle(BlockStyle.NORMAL24.id);
    blockInfo.lodShapeStyle = 1;
    blockInfo.sideTexturesPointToOrientation = false;

    /*
    BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(),
                          new FactoryResource(1, ));

     */

    BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "cake", null);
    BlockConfig.add(blockInfo);
  }
}
