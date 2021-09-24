package thederpgamer.decor.element.blocks.decor;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.element.blocks.ActivationInterface;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.gui.panel.holoprojector.HoloProjectorConfigDialog;
import thederpgamer.decor.manager.ResourceManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjector extends Block implements ActivationInterface {

    public HoloProjector() {
        super("Holo Projector", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
    }

    @Override
    public void initialize() {
        if(GraphicsContext.initialized) {
            try {
                blockInfo.setTextureId(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getTextureIds());
                blockInfo.setTextureId(0, (short) ResourceManager.getTexture("holo-projector-front").getTextureId());
                blockInfo.setBuildIconNum(ResourceManager.getTexture("holo-projector-icon").getTextureId());
            } catch(Exception ignored) { }
          }
        blockInfo.setDescription("A block used to project an image at a specified location, scale, and rotation.");
        blockInfo.setInRecipe(true);
        blockInfo.setShoppable(true);
        blockInfo.setCanActivate(true);
        blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
        blockInfo.setOrientatable(true);

        blockInfo.controlledBy.add((short) 405);
        blockInfo.controlledBy.add((short) 993);
        blockInfo.controlledBy.add((short) 666);
        blockInfo.controlledBy.add((short) 399);

        ElementKeyMap.getInfo(405).controlling.add(getId());
        ElementKeyMap.getInfo(993).controlling.add(getId());
        ElementKeyMap.getInfo(666).controlling.add(getId());
        ElementKeyMap.getInfo(399).controlling.add(getId());

        BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(),
                new FactoryResource(1, ElementKeyMap.TEXT_BOX),
                new FactoryResource(50, (short) 220)
        );
        BlockConfig.add(blockInfo);
    }

    @Override
    public void onPlayerActivation(SegmentPieceActivateByPlayer event) {
        HoloProjectorConfigDialog configDialog = new HoloProjectorConfigDialog();
        configDialog.setSegmentPiece(event.getSegmentPiece());
        configDialog.activate();
        event.getSegmentPiece().setActive(!event.getSegmentPiece().isActive());
        if(GameClient.getClientState() != null) GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().suspend(true);
    }

    @Override
    public void onLogicActivation(SegmentPieceActivateEvent event) {

    }
}
