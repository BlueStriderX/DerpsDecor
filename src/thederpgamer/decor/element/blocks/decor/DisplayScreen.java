package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.blocks.Block;

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
        blockInfo.setInRecipe(true);
        blockInfo.setCanActivate(true);
        blockInfo.setShoppable(true);
        blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
        blockInfo.setOrientatable(true);
        blockInfo.setIndividualSides(1);
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
