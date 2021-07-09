package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.Block;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjector extends Block {

    public HoloProjector() {
        super("Holo Projector", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
    }

    @Override
    public void initialize() {
        blockInfo.setInRecipe(true);
        blockInfo.setCanActivate(true);
        blockInfo.setShoppable(true);
        blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
        blockInfo.setOrientatable(true);
        //blockInfo.setIndividualSides(1);
        //blockInfo.setBlockStyle(6);
        //blockInfo.lodShapeStyle = 2;
        //blockInfo.sideTexturesPointToOrientation = true;

        BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(),
                new FactoryResource(1, ElementManager.getBlock("Display Screen").getId())
        );

        //BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "holo_projector", null);
        BlockConfig.add(blockInfo);
    }
}
