package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.manager.ResourceManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/15/2021
 */
public class DisplayScreen extends Block {

    public DisplayScreen() {
        super("Display Screen", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
    }

    @Override
    public void initialize() {
        if(GraphicsContext.initialized) {
            blockInfo.setBuildIconNum(ResourceManager.getTexture("display-screen-icon").getTextureId());
        }
        blockInfo.setDescription(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getDescription());
        blockInfo.setCanActivate(true);
        blockInfo.setInRecipe(false); //Todo: Fix orientation bug
        blockInfo.setShoppable(false);
        blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
        blockInfo.setOrientatable(true);
        blockInfo.setIndividualSides(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getIndividualSides());
        blockInfo.setBlockStyle(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getBlockStyle().id);
        blockInfo.lodShapeStyle = 1;
        blockInfo.sideTexturesPointToOrientation = false;

        BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(),
                new FactoryResource(1, ElementKeyMap.TEXT_BOX)
        );

        BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "display_screen", null);
        BlockConfig.add(blockInfo);
    }
}
