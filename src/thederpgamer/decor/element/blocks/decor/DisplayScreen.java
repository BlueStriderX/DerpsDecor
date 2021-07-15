package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.manager.LogManager;

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
            try {
                //blockInfo.setBuildIconNum(ResourceManager.getTexture("display-screen-icon").getTextureId());
            } catch(Exception exception) {
                LogManager.logException("Encountered an exception while trying to load textures for Display Screen! This will result in missing textures in-game!", exception);
            }
        }
        blockInfo.setDescription("Displays a holographic screen based on the text entered. Can also be used to track vital systems' information.");
        blockInfo.setInRecipe(true);
        blockInfo.setCanActivate(true);
        blockInfo.setShoppable(true);
        blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
        blockInfo.setOrientatable(true);
        blockInfo.setIndividualSides(6);
        blockInfo.setBlockStyle(6);
        blockInfo.lodShapeStyle = 2;
        blockInfo.sideTexturesPointToOrientation = false;

        BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(),
                new FactoryResource(1, ElementKeyMap.TEXT_BOX)
        );

        BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "display_screen", null);
        BlockConfig.add(blockInfo);
    }
}
