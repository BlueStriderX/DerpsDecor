package thederpgamer.decor.manager;

import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.block.SegmentPieceKillEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.register.ManagerContainerRegisterEvent;
import api.mod.StarLoader;
import api.utils.SegmentPieceUtils;
import api.utils.game.SegmentControllerUtils;
import api.utils.game.module.util.SimpleDataStorageMCModule;
import org.lwjgl.input.Keyboard;
import org.schema.game.client.view.WorldDrawer;
import org.schema.game.client.view.tools.IconTextureBakery;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.elements.shipyard.ShipyardCollectionManager;
import org.schema.game.common.controller.elements.shipyard.ShipyardElementManager;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.meta.VirtualBlueprintMetaItem;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.ActivationInterface;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.systems.modules.CrewStationModule;
import thederpgamer.decor.systems.modules.HoloProjectorModule;
import thederpgamer.decor.systems.modules.TextProjectorModule;
import thederpgamer.decor.utils.ProjectorUtils;
import thederpgamer.decor.utils.ServerUtils;

import java.util.ArrayList;
import java.util.Objects;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class EventManager {
	public static void initialize(DerpsDecor instance) {
		StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
			@Override
			public void onEvent(KeyPressEvent event) {
				if(ConfigManager.getMainConfig().getBoolean("debug-mode")) {
					if(event.getKey() == Keyboard.KEY_GRAVE) {
						if(event.isKeyDown()) {
							IconTextureBakery.normalWrite = false;
							WorldDrawer.flagTextureBake = true;
						} else {
							IconTextureBakery.normalWrite = true;
							WorldDrawer.flagTextureBake = false;
						}
					}
				}
			}
		}, instance);

		StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
			@Override
			public void onEvent(RegisterWorldDrawersEvent event) {
				GlobalDrawManager.initialize(event);
			}
		}, instance);
		StarLoader.registerListener(ManagerContainerRegisterEvent.class, new Listener<ManagerContainerRegisterEvent>() {
			@Override
			public void onEvent(ManagerContainerRegisterEvent event) {
				event.addModMCModule(new HoloProjectorModule(event.getSegmentController(), event.getContainer()));
				event.addModMCModule(new TextProjectorModule(event.getSegmentController(), event.getContainer()));
				event.addModMCModule(new CrewStationModule(event.getSegmentController(), event.getContainer()));
//				event.addModMCModule(new HoloTableModule(event.getSegmentController(), event.getContainer()));
				//				event.addModMCModule(new StorageCapsuleModule(event.getSegmentController(), event.getContainer()));
			}
		}, instance);
		StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
			@Override
			public void onEvent(SegmentPieceActivateByPlayer event) {
				for(Block block : ElementManager.getAllBlocks()) {
					if(block instanceof ActivationInterface && block.getId() == event.getSegmentPiece().getType()) {
						((ActivationInterface) block).onPlayerActivation(event);
						return;
					}
				}
			}
		}, instance);
		StarLoader.registerListener(SegmentPieceActivateEvent.class, new Listener<SegmentPieceActivateEvent>() {
			@Override
			public void onEvent(SegmentPieceActivateEvent event) {
				for(Block block : ElementManager.getAllBlocks()) {
					if(block instanceof ActivationInterface && block.getId() == event.getSegmentPiece().getType()) {
						((ActivationInterface) block).onLogicActivation(event);
						break;
					}
				}
				if(event.getSegmentPiece().getType() == ElementKeyMap.ACTIVAION_BLOCK_ID || event.getSegmentPiece().getType() == ElementKeyMap.LOGIC_BUTTON_NORM) {
					if(SegmentPieceUtils.getControlledPiecesMatching(event.getSegmentPiece(), ElementKeyMap.SHIPYARD_COMPUTER).isEmpty()) return;
					SegmentPiece controller = SegmentPieceUtils.getControlledPiecesMatching(event.getSegmentPiece(), ElementKeyMap.SHIPYARD_COMPUTER).get(0);
					if(controller != null && !event.isServer()) {
						try {
							if(!event.getSegmentPiece().isActive()) return;
							ShipyardElementManager shipyard = SegmentControllerUtils.getElementManager((ManagedUsableSegmentController<?>) event.getSegmentPiece().getSegmentController(), ShipyardElementManager.class);
							assert shipyard != null;
							SegmentPiece textBlock = SegmentPieceUtils.getFirstMatchingAdjacent(event.getSegmentPiece(), ElementKeyMap.TEXT_BOX);
							assert textBlock != null;
							String text = textBlock.getSegmentController().getTextMap().get(ElementCollection.getIndex4(textBlock.getAbsoluteIndex(), textBlock.getOrientation()));
							String command = text.split("\n")[0].trim().replaceAll(" ", "_").toUpperCase();
							String[] args = (text.split("\n").length > 1) ? text.split("\n")[1].trim().split(", ") : new String[] {""};
							int factionId = event.getSegmentPiece().getSegmentController().getFactionId();
							ShipyardCollectionManager collection = shipyard.getCollectionManagers().get(0);
							switch(command) {
								case "NEW":
								case "CREATE":
								case "CREATE_NEW":
								case "CREATE_NEW_DESIGN":
								case "CREATE_DESIGN":
								case "NEW_DESIGN":
									assert args.length == 1;
									collection.sendShipyardCommandToServer(factionId, ShipyardCollectionManager.ShipyardCommandType.CREATE_NEW_DESIGN, args[0]);
									break;
								case "UNLOAD":
								case "UNLOAD_DESIGN":
									collection.sendShipyardCommandToServer(factionId, ShipyardCollectionManager.ShipyardCommandType.UNLOAD_DESIGN);
									break;
								case "LOAD":
								case "LOAD_DESIGN":
									assert args.length == 1;
									int designId = -1;
									for(VirtualBlueprintMetaItem item : collection.getDesignList()) {
										if(item.UID.toLowerCase().contains(args[0].toLowerCase())) {
											designId = item.getId();
											break;
										}
									}
									if(designId != -1) collection.sendShipyardCommandToServer(factionId, ShipyardCollectionManager.ShipyardCommandType.LOAD_DESIGN, designId);
									break;
								case "DECONSTRUCT":
								case "DECONSTRUCT_DESIGN":
								case "RECYCLE":
								case "RECYCLE_DESIGN":
									assert collection.isCurrentStateUndockable() && collection.isCurrentDockedValid();
									collection.sendShipyardCommandToServer(factionId, ShipyardCollectionManager.ShipyardCommandType.DECONSTRUCT_RECYCLE);
									break;
								case "SPAWN":
								case "SPAWN_DESIGN":
								case "CONSTRUCT":
								case "CONSTRUCT_DESIGN":
									assert collection.isLoadedDesignValid() && args.length == 1;
									collection.sendShipyardCommandToServer(factionId, ShipyardCollectionManager.ShipyardCommandType.SPAWN_DESIGN, args[0]);
									break;
								case "REPAIR":
								case "REPAIR_DESIGN":
								case "REPAIR_TO_DESIGN":
								case "REPAIR_DOCKED":
								case "REPAIR_DOCKED_DESIGN":
								case "REPAIR_DOCKED_TO_DESIGN":
									assert collection.isCurrentStateUndockable() && collection.isCurrentDockedValid() && args.length == 1;
									int design = -1;
									for(VirtualBlueprintMetaItem item : collection.getDesignList()) {
										if(item.UID.toLowerCase().contains(args[0].toLowerCase())) {
											design = item.getId();
											break;
										}
									}
									collection.sendShipyardCommandToServer(factionId, ShipyardCollectionManager.ShipyardCommandType.REPAIR_FROM_DESIGN, design);
									break;
								case "UNDOCK":
								case "UNDOCK_DOCKED":
									assert collection.isCurrentStateUndockable() && collection.isCurrentDockedValid();
									collection.undockRequestedFromShipyard();
									break;
							}
						} catch(Exception ignored) {}
					}
				} else if((event.getSegmentPiece().getType() == ElementKeyMap.ACTIVAION_BLOCK_ID || event.getSegmentPiece().getType() == ElementKeyMap.LOGIC_BUTTON_NORM)) {
					SegmentPiece adjacent = SegmentPieceUtils.getFirstMatchingAdjacent(event.getSegmentPiece(), ElementManager.getBlock("Holo Projector").getId());
					if(adjacent != null) {
						HoloProjectorDrawData adjacentDrawData = (HoloProjectorDrawData) ProjectorUtils.getDrawData(adjacent);
						ArrayList<SegmentPiece> controlling = SegmentPieceUtils.getControlledPiecesMatching(event.getSegmentPiece(), ElementManager.getBlock("Holo Projector").getId());
						if(!controlling.isEmpty() && adjacentDrawData != null) {
							boolean needsUpdate = false;
							for(SegmentPiece segmentPiece : controlling) {
								Object drawData = ProjectorUtils.getDrawData(segmentPiece);
								if(drawData instanceof HoloProjectorDrawData) {
									HoloProjectorDrawData holoProjectorDrawData = (HoloProjectorDrawData) drawData;
									if(!(holoProjectorDrawData.equals(adjacentDrawData)) || segmentPiece.isActive() != event.getSegmentPiece().isActive()) {
										adjacentDrawData.copyTo(holoProjectorDrawData);
										needsUpdate = true;
									}
								}
							}
							if(needsUpdate) ((SimpleDataStorageMCModule) ServerUtils.getManagerContainer(event.getSegmentPiece().getSegmentController()).getModMCModule(ElementManager.getBlock("Holo Projector").getId())).flagUpdatedData();
						}
					} else {
						adjacent = SegmentPieceUtils.getFirstMatchingAdjacent(event.getSegmentPiece(), ElementManager.getBlock("Text Projector").getId());
						if(adjacent != null) {
							TextProjectorDrawData adjacentDrawData = (TextProjectorDrawData) ProjectorUtils.getDrawData(adjacent);
							ArrayList<SegmentPiece> controlling = SegmentPieceUtils.getControlledPiecesMatching(event.getSegmentPiece(), ElementManager.getBlock("Text Projector").getId());
							if(!controlling.isEmpty() && adjacentDrawData != null) {
								boolean needsUpdate = false;
								for(SegmentPiece segmentPiece : controlling) {
									Object drawData = ProjectorUtils.getDrawData(segmentPiece);
									if(drawData instanceof TextProjectorDrawData) {
										TextProjectorDrawData textProjectorDrawData = (TextProjectorDrawData) drawData;
										if(!(textProjectorDrawData.equals(adjacentDrawData)) || segmentPiece.isActive() != event.getSegmentPiece().isActive()) {
											adjacentDrawData.copyTo(textProjectorDrawData);
											needsUpdate = true;
										}
									}
								}
								if(needsUpdate) ((SimpleDataStorageMCModule) ServerUtils.getManagerContainer(event.getSegmentPiece().getSegmentController()).getModMCModule(ElementManager.getBlock("Text Projector").getId())).flagUpdatedData();
							}
						}
					}
				}
			}
		}, instance);

		StarLoader.registerListener(SegmentPieceKillEvent.class, new Listener<SegmentPieceKillEvent>() {
			@Override
			public void onEvent(SegmentPieceKillEvent event) {
				ManagedUsableSegmentController<?> segmentController = (ManagedUsableSegmentController<?>) event.getPiece().getSegmentController();
				if(event.getPiece().getType() == Objects.requireNonNull(ElementManager.getBlock("NPC Station")).getId()) {
					((CrewStationModule) segmentController.getManagerContainer().getModMCModule(event.getPiece().getType())).removeCrewBlock(ElementCollection.getIndex4(event.getPiece().getAbsoluteIndex(), event.getPiece().getOrientation()));
				} else if(event.getPiece().getType() == Objects.requireNonNull(ElementManager.getBlock("Holo Projector")).getId()) {
					segmentController.getManagerContainer().getModMCModule(event.getPiece().getType()).handleRemove(ElementCollection.getIndex4(event.getPiece().getAbsoluteIndex(), event.getPiece().getOrientation()));
				} else if(event.getPiece().getType() == Objects.requireNonNull(ElementManager.getBlock("Text Projector")).getId()) {
					segmentController.getManagerContainer().getModMCModule(event.getPiece().getType()).handleRemove(ElementCollection.getIndex4(event.getPiece().getAbsoluteIndex(), event.getPiece().getOrientation()));
				}
			}
		}, instance);
	}
}
