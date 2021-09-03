package thederpgamer.decor;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.*;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.register.ManagerContainerRegisterEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.game.inventory.InventoryUtils;
import org.schema.game.client.controller.PlayerTextAreaInput;
import org.schema.game.client.controller.element.world.ClientSegmentProvider;
import org.schema.game.client.controller.manager.ingame.PlayerInteractionControlManager;
import org.schema.game.common.controller.SendableSegmentProvider;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.SendableGameState;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.player.inventory.InventorySlot;
import org.schema.game.common.data.world.Segment;
import org.schema.game.network.objects.remote.RemoteTextBlockPair;
import org.schema.game.network.objects.remote.TextBlockPair;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.drawdata.ProjectorDrawData;
import thederpgamer.decor.data.drawdata.StrutDrawData;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.decor.*;
import thederpgamer.decor.gui.panel.holoprojector.HoloProjectorConfigDialog;
import thederpgamer.decor.gui.panel.textprojector.TextProjectorConfigDialog;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.modules.HoloProjectorModule;
import thederpgamer.decor.modules.StrutCornerModule;
import thederpgamer.decor.modules.TextProjectorModule;
import thederpgamer.decor.utils.*;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Main class for DerpsDecor mod.
 *
 * @author TheDerpGamer
 * @since 06/15/2021
 */
public class DerpsDecor extends StarMod {

    //Instance
    private static DerpsDecor instance;
    public static DerpsDecor getInstance() {
        return instance;
    }
    public DerpsDecor() { }
    public static void main(String[] args) { }

    //Utils
    public ClipboardUtils clipboard;

    @Override
    public void onEnable() {
        instance = this;
        clipboard = new ClipboardUtils();
        ConfigManager.initialize(this);
        LogManager.initialize();
        SegmentPieceUtils.initialize();
        registerListeners();
    }

    @Override
    public void onResourceLoad(ResourceLoader loader) {
        ResourceManager.loadResources(this, loader);
    }

    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        ElementManager.addBlock(new HoloProjector());
        ElementManager.addBlock(new TextProjector());
        ElementManager.addBlock(new StrutConnector());
        ElementManager.addBlock(new DisplayScreen());
        ElementManager.addBlock(new HoloTable());
        ElementManager.initialize();
    }

    private void registerListeners() {
        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                GlobalDrawManager.initialize(event);
            }
        }, this);

        StarLoader.registerListener(ManagerContainerRegisterEvent.class, new Listener<ManagerContainerRegisterEvent>() {
            @Override
            public void onEvent(ManagerContainerRegisterEvent event) {
                event.addModMCModule(new HoloProjectorModule(event.getSegmentController(), event.getContainer()));
                event.addModMCModule(new TextProjectorModule(event.getSegmentController(), event.getContainer()));
                event.addModMCModule(new StrutCornerModule(event.getSegmentController(), event.getContainer()));
            }
        }, this);

        StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
            @Override
            public void onEvent(final SegmentPieceActivateByPlayer event) {
                final SegmentPiece piece = event.getSegmentPiece();
                if(ElementManager.getBlock("Holo Projector") != null && piece.getType() == ElementManager.getBlock("Holo Projector").getId()) {
                    HoloProjectorConfigDialog configDialog = new HoloProjectorConfigDialog();
                    configDialog.setSegmentPiece(piece);
                    configDialog.activate();
                    piece.setActive(!piece.isActive());
                    if(GameClient.getClientState() != null) GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().suspend(true);
                } else if(ElementManager.getBlock("Text Projector") != null && piece.getType() == ElementManager.getBlock("Text Projector").getId()) {
                    TextProjectorConfigDialog configDialog = new TextProjectorConfigDialog();
                    configDialog.setSegmentPiece(piece);
                    configDialog.activate();
                    piece.setActive(!piece.isActive());
                    if(GameClient.getClientState() != null) GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().suspend(true);
                } else if(event.getSegmentPiece().getType() == Objects.requireNonNull(ElementManager.getBlock("Strut Connector")).getId())  {
                    InventorySlot selectedSlot = PlayerUtils.getSelectedSlot();
                    if(!selectedSlot.isEmpty() && ElementKeyMap.getInfo(selectedSlot.getType()).getName().toLowerCase().contains("paint")) {
                        switch(PlayerUtils.connectingStrut) {
                            case PlayerUtils.NONE:
                                PlayerUtils.currentConnectionIndex = event.getSegmentPiece().getAbsoluteIndex();
                                PlayerUtils.startConnectionRunner();
                                api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Set first point.");
                                //Todo: Draw some sort of indication
                                break;
                            case PlayerUtils.FIRST:
                                PlayerUtils.connectingStrut = PlayerUtils.SECOND;
                                ManagerContainer<?> managerContainer = ServerUtils.getManagerContainer(event.getSegmentPiece().getSegmentController());
                                if(managerContainer != null) {
                                    StrutCornerModule module = (StrutCornerModule) managerContainer.getModMCModule(ElementManager.getBlock("Strut Connector").getId());
                                    if(module != null) {
                                        SegmentPiece otherPiece = managerContainer.getSegmentController().getSegmentBuffer().getPointUnsave(PlayerUtils.currentConnectionIndex);
                                        if(SegmentPieceUtils.withinSameAxisAndAngle(otherPiece, event.getSegmentPiece(), 90.0f)) {
                                            int requiredAmount = SegmentPieceUtils.getDistance(otherPiece, event.getSegmentPiece());
                                            int currentAmount = InventoryUtils.getItemAmount(GameClient.getClientPlayerState().getInventory(), PlayerUtils.getSelectedSlot().getType());
                                            if(currentAmount >= requiredAmount || GameClient.getClientPlayerState().isUseCreativeMode()) {
                                                SegmentPiece[] key = {otherPiece, event.getSegmentPiece()};
                                                module.blockMap.remove(key);
                                                module.blockMap.put(key, new StrutDrawData(PaintColor.fromId(selectedSlot.getType()).color, otherPiece, event.getSegmentPiece()));
                                                InventoryUtils.consumeItems(GameClient.getClientPlayerState().getInventory(), PlayerUtils.getSelectedSlot().getType(), requiredAmount);
                                                api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Created new strut.");
                                            } else {
                                                String blockName = ElementKeyMap.getInfo(PlayerUtils.getSelectedSlot().getType()).getName();
                                                api.utils.game.PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Not enough " + blockName + " blocks. Need " + (requiredAmount - currentAmount) + "more.");
                                            }
                                            return;
                                        }
                                    }
                                }
                                LogManager.logWarning("Player \"" + GameClient.getClientPlayerState().getName() + "\" attempted to create a strut on an invalid entity.", null);
                                PlayerUtils.currentConnectionIndex = 0;
                                break;
                        }
                    }
                } else if(event.getSegmentPiece().getType() == Objects.requireNonNull(ElementManager.getBlock("Display Screen")).getId()) {
                    final PlayerInteractionControlManager cm = event.getControlManager();
                    String text = piece.getSegment().getSegmentController().getTextMap().get(ElementCollection.getIndex4(piece.getAbsoluteIndex(), piece.getOrientation()));
                    if(text == null) text = "";

                    final PlayerTextAreaInput t = new PlayerTextAreaInput("EDIT_DISPLAY_BLOCK_POPUP", cm.getState(), 400, 300, SendableGameState.TEXT_BLOCK_LIMIT, SendableGameState.TEXT_BLOCK_LINE_LIMIT + 1, "Edit Holoprojector", "", text, FontLibrary.FontSize.SMALL) {

                        @Override
                        public void onDeactivate() {
                            cm.suspend(false);
                        }

                        @Override
                        public String[] getCommandPrefixes() {
                            return null;
                        }

                        @Override
                        public boolean onInput(String entry) {
                            SendableSegmentProvider ss = ((ClientSegmentProvider) piece.getSegment().getSegmentController().getSegmentProvider()).getSendableSegmentProvider();
                            TextBlockPair f = new TextBlockPair();
                            f.block = ElementCollection.getIndex4(piece.getAbsoluteIndex(), piece.getOrientation());
                            f.text = entry;
                            System.err.println("[CLIENT]Text entry:\n\"" + f.text + "\"");
                            ss.getNetworkObject().textBlockResponsesAndChangeRequests.add(new RemoteTextBlockPair(f, false));
                            return true;
                        }

                        @Override
                        public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
                            return null;
                        }

                        @Override
                        public boolean isOccluded() {
                            return false;
                        }


                        @Override
                        public void onFailedTextCheck(String msg) {

                        }
                    };

                    t.getTextInput().setAllowEmptyEntry(true);
                    t.getInputPanel().onInit();
                    t.activate();
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceActivateEvent.class, new Listener<SegmentPieceActivateEvent>() {
            @Override
            public void onEvent(SegmentPieceActivateEvent event) {
                if(event.isServer()) {
                    if((event.getSegmentPiece().getType() == ElementKeyMap.ACTIVAION_BLOCK_ID || event.getSegmentPiece().getType() == ElementKeyMap.LOGIC_BUTTON_NORM) && event.getSegmentPiece().isActive()) {
                        SegmentPiece adjacent = SegmentPieceUtils.getFirstMatchingAdjacent(event.getSegmentPiece(), ElementManager.getBlock("Holo Projector").getId());
                        if(adjacent != null) {
                            HoloProjectorDrawData adjacentDrawData = (HoloProjectorDrawData) ProjectorUtils.getDrawData(adjacent);
                            ArrayList<SegmentPiece> controlling = SegmentPieceUtils.getControlledPiecesMatching(event.getSegmentPiece(), ElementManager.getBlock("Holo Projector").getId());
                            if(!controlling.isEmpty() && adjacentDrawData != null) {
                                for(SegmentPiece segmentPiece : controlling) {
                                    ProjectorDrawData drawData = ProjectorUtils.getDrawData(segmentPiece);
                                    if(drawData instanceof HoloProjectorDrawData) {
                                        ((HoloProjectorDrawData) drawData).src = adjacentDrawData.src;
                                        drawData.changed = true;
                                    }
                                }
                            }
                        } else {
                            adjacent = SegmentPieceUtils.getFirstMatchingAdjacent(event.getSegmentPiece(), ElementManager.getBlock("Text Projector").getId());
                            if(adjacent != null) {
                                TextProjectorDrawData adjacentDrawData = (TextProjectorDrawData) ProjectorUtils.getDrawData(adjacent);
                                ArrayList<SegmentPiece> controlling = SegmentPieceUtils.getControlledPiecesMatching(event.getSegmentPiece(), ElementManager.getBlock("Text Projector").getId());
                                if(!controlling.isEmpty() && adjacentDrawData != null) {
                                    for(SegmentPiece segmentPiece : controlling) {
                                        ProjectorDrawData drawData = ProjectorUtils.getDrawData(segmentPiece);
                                        if(drawData instanceof TextProjectorDrawData) {
                                            ((TextProjectorDrawData) drawData).text = adjacentDrawData.text;
                                            ((TextProjectorDrawData) drawData).color = adjacentDrawData.color;
                                            drawData.changed = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddEvent.class, new Listener<SegmentPieceAddEvent>() {
            @Override
            public void onEvent(SegmentPieceAddEvent event) {
                if(event.getNewType() == Objects.requireNonNull(ElementManager.getBlock("Display Screen")).getId()) {
                    long indexAndOrientation = ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation());
                    event.getSegmentController().getTextBlocks().add(indexAndOrientation);
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceRemoveEvent.class, new Listener<SegmentPieceRemoveEvent>() {
            @Override
            public void onEvent(SegmentPieceRemoveEvent event) {
                if(event.getType() == Objects.requireNonNull(ElementManager.getBlock("Display Screen")).getId()) {
                    Segment segment = event.getSegment();
                    long absoluteIndex = segment.getAbsoluteIndex(event.getX(), event.getY(), event.getZ());
                    long indexAndOrientation = ElementCollection.getIndex4(absoluteIndex, event.getOrientation());
                    event.getSegment().getSegmentController().getTextBlocks().remove(indexAndOrientation);
                    event.getSegment().getSegmentController().getTextMap().remove(indexAndOrientation);
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddByMetadataEvent.class, new Listener<SegmentPieceAddByMetadataEvent>() {
            @Override
            public void onEvent(SegmentPieceAddByMetadataEvent event) {
                if(event.getType() == Objects.requireNonNull(ElementManager.getBlock("Display Screen")).getId()) {
                    event.getSegment().getSegmentController().getTextBlocks().add(event.getIndexAndOrientation());
                }
            }
        }, this);
    }
}
