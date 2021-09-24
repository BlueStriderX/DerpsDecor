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
 * @since 09/01/2021
 */
public class StrutConnector extends Block {

    public StrutConnector() {
        super("Strut Connector", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
    }

    @Override
    public void initialize() {
        if(GraphicsContext.initialized) {
            try {
                blockInfo.setBuildIconNum(ResourceManager.getTexture("strut-connector-icon").getTextureId());
            } catch(Exception ignored) { }
        }
        blockInfo.setDescription("Place two of these down and activate each while holding paint to create a colored strut in-between them.");
        blockInfo.setCanActivate(true);
        blockInfo.setInRecipe(true);
        blockInfo.setShoppable(true);
        blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
        blockInfo.setOrientatable(true);
        blockInfo.setIndividualSides(6);
        blockInfo.setBlockStyle(ElementKeyMap.getInfo(1137).blockStyle.id);
        blockInfo.lodShapeStyle = 1;
        blockInfo.sideTexturesPointToOrientation = false;

        BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(),
                              new FactoryResource(1, (short) 941),
                              new FactoryResource(1, (short) 976));

        BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "strut_connector", null);
        BlockConfig.add(blockInfo);
    }
}