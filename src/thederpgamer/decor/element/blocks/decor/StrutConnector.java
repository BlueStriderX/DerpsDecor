package thederpgamer.decor.element.blocks.decor;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.utils.game.inventory.InventoryUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.game.common.data.player.inventory.InventorySlot;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.system.strut.StrutData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.ActivationInterface;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.systems.modules.StrutConnectorModule;
import thederpgamer.decor.utils.*;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/01/2021
 */
public class StrutConnector extends Block implements ActivationInterface {

	public StrutConnector() {
		super("Strut Connector", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription("Place two of these down and activate each while holding paint to create a colored strut" + " in-between them.");
		blockInfo.setCanActivate(true);
		blockInfo.setInRecipe(true);
		blockInfo.setShoppable(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
		blockInfo.setOrientatable(true);
		blockInfo.setIndividualSides(6);
		blockInfo.setBlockStyle(BlockStyle.WEDGE.id);
		blockInfo.lodShapeStyle = 1;
		blockInfo.sideTexturesPointToOrientation = false;

		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(), new FactoryResource(1, (short) 941), new FactoryResource(1, (short) 976));

		BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "strut_connector", null);
		BlockConfig.add(blockInfo);
	}

	@Override
	public void createGraphics() {
		BlockIconUtils.createBlockIcon(blockInfo);
	}

	@Override
	public void onPlayerActivation(SegmentPieceActivateByPlayer event) {
		final SegmentPiece segmentPiece = event.getSegmentPiece();
		InventorySlot selectedSlot = PlayerUtils.getSelectedSlot();
		if(!selectedSlot.isEmpty() && ElementKeyMap.getInfo(selectedSlot.getType()).getName().toLowerCase().contains("paint")) {
			switch(PlayerUtils.connectingStrut) {
				case PlayerUtils.NONE:
					PlayerUtils.currentConnectionIndex = event.getSegmentPiece().getAbsoluteIndex();
					PlayerUtils.startConnectionRunner();
					api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Set point A.");
					break;
				case PlayerUtils.FIRST:
					PlayerUtils.connectingStrut = PlayerUtils.SECOND;
					ManagerContainer<?> managerContainer = ServerUtils.getManagerContainer(event.getSegmentPiece().getSegmentController());
					if(managerContainer != null) {
						StrutConnectorModule module = (StrutConnectorModule) managerContainer.getModMCModule(ElementManager.getBlock("Strut Connector").getId());
						if(module != null) {
							SegmentPiece otherPiece = managerContainer.getSegmentController().getSegmentBuffer().getPointUnsave(PlayerUtils.currentConnectionIndex);
							if(SegmentPieceUtils.withinSameAxisAndAngle(otherPiece, event.getSegmentPiece(), 90.0f)) {
								int requiredAmount = SegmentPieceUtils.getDistance(otherPiece, event.getSegmentPiece());
								int currentAmount = InventoryUtils.getItemAmount(GameClient.getClientPlayerState().getInventory(), PlayerUtils.getSelectedSlot().getType());
								if(requiredAmount > ConfigManager.getMainConfig().getInt("max-strut-length")) {
									api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Strut cannot be longer than the server limit (" +  ConfigManager.getMainConfig().getInt("max-strut-length") + ").");
								} else if(currentAmount >= requiredAmount || GameClient.getClientPlayerState().isUseCreativeMode()) {
									int maxConnections = ConfigManager.getMainConfig().getInt("max-strut-connections");
									int connectionsA = module.getConnectionCount(otherPiece);
									int connectionsB = module.getConnectionCount(event.getSegmentPiece());
									if(connectionsA > maxConnections) {
										api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Strut A cannot have more connections than the server limit (" + maxConnections + ").");
									} else if(connectionsB > maxConnections) {
										api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Strut B cannot have more connections than the server limit (" + maxConnections + ").");
									} else {
										if(otherPiece.equals(event.getSegmentPiece())) {
											api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Strut A cannot be connected to itself!");
										} else {
											MutablePair<Long, Long> key = new MutablePair<>(otherPiece.getAbsoluteIndex(), event.getSegmentPiece().getAbsoluteIndex());
											module.getData().remove(key);
											module.getData().put(key, new StrutData(PaintColor.fromId(selectedSlot.getType()), otherPiece, event.getSegmentPiece()));
											module.flagUpdatedData();
											InventoryUtils.consumeItems(GameClient.getClientPlayerState().getInventory(), PlayerUtils.getSelectedSlot().getType(), requiredAmount);
											api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Set Point B.");
											api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Created new strut.");
										}
									}
								} else {
									String blockName = ElementKeyMap.getInfo(PlayerUtils.getSelectedSlot().getType()).getName();
									api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Not enough " + blockName + " blocks. Need " + (requiredAmount - currentAmount) + "more.");
								}
								PlayerUtils.currentConnectionIndex = 0;
							} else api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "The angle between both struts must be less than or equal to 90 degrees.");
							return;
						}
					}
					LogManager.logWarning("Player \"" + GameClient.getClientPlayerState().getName() + "\" attempted to create a strut on an invalid entity.", null);
					PlayerUtils.currentConnectionIndex = 0;
					break;
			}
		}
	}

	@Override
	public void onLogicActivation(SegmentPieceActivateEvent event) {

	}
}
