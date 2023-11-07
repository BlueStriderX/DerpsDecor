package thederpgamer.decor.element.blocks.decor;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.data.system.crew.CrewData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.ActivationInterface;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.gui.panel.crewstation.CrewStationConfigDialog;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.systems.modules.CrewStationModule;

import java.util.Objects;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class NPCStation extends Block implements ActivationInterface {
	public NPCStation() {
		super("NPC Station", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription("A block that allows you to spawn and configure decorative NPC crew members.");
		blockInfo.setInRecipe(true);
		blockInfo.setShoppable(true);
		blockInfo.setCanActivate(true);
		blockInfo.setIndividualSides(6);
		blockInfo.setBlockStyle(BlockStyle.NORMAL.id);
		blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
		blockInfo.setOrientatable(true);
		if(GraphicsContext.initialized) {
			try {
				blockInfo.setTextureId(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getTextureIds());
				blockInfo.setTextureId(0, (short) ResourceManager.getTexture("npc-station-front").getTextureId());
				blockInfo.setBuildIconNum(ResourceManager.getIcon("npc-station-icon").getTextureId());
			} catch(Exception ignored) {}
		}
		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(), new FactoryResource(1, ElementKeyMap.TEXT_BOX), new FactoryResource(50, (short) 220));
		BlockConfig.add(blockInfo);
	}

	@Override
	public void onPlayerActivation(SegmentPieceActivateByPlayer event) {
		CrewStationConfigDialog configDialog = new CrewStationConfigDialog(event.getSegmentPiece());
		ManagedUsableSegmentController<?> segmentController = (ManagedUsableSegmentController<?>) event.getSegmentPiece().getSegmentController();
		CrewStationModule module = (CrewStationModule) segmentController.getManagerContainer().getModMCModule(Objects.requireNonNull(ElementManager.getBlock("NPC Station")).getId());
		configDialog.setCrewData(module.getData(event.getSegmentPiece()));
		configDialog.activate();
		if(GameClient.getClientState() != null) GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().suspend(true);
	}

	@Override
	public void onLogicActivation(SegmentPieceActivateEvent event) {
		ManagedUsableSegmentController<?> segmentController = (ManagedUsableSegmentController<?>) event.getSegmentPiece().getSegmentController();
		CrewStationModule module = (CrewStationModule) segmentController.getManagerContainer().getModMCModule(Objects.requireNonNull(ElementManager.getBlock("NPC Station")).getId());
		event.getSegmentPiece().setActive(!event.getSegmentPiece().isActive());
		CrewData data = module.getData(event.getSegmentPiece());
		data.active = !event.getSegmentPiece().isActive();
		data.updateCrew();
	}
}