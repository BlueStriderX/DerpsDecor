package thederpgamer.decor;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceAddByMetadataEvent;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.block.SegmentPieceRemoveEvent;
import api.listener.fastevents.FastListenerCommon;
import api.mod.StarLoader;
import api.mod.StarMod;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.world.Segment;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.decor.drawer.ProjectorDrawListener;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.decor.HoloProjector;
import thederpgamer.decor.element.blocks.decor.TextProjector;
import thederpgamer.decor.gui.panel.holoprojector.HoloProjectorConfigDialog;
import thederpgamer.decor.gui.panel.textprojector.TextProjectorConfigDialog;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.ClipboardUtils;

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
        registerFastListeners();
        registerListeners();
    }

    @Override
    public void onResourceLoad(ResourceLoader loader) {
        ResourceManager.loadResources(this, loader);
    }

    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        //Decor Blocks
        //ElementManager.addBlock(new DisplayScreen());
        ElementManager.addBlock(new HoloProjector());
        ElementManager.addBlock(new TextProjector());

        ElementManager.initialize();
    }

    private void registerListeners() {
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

                }/* else if(ElementManager.getBlock("Display Screen") != null && piece.getType() == ElementManager.getBlock("Display Screen").getId()) {
                    final PlayerInteractionControlManager cm = event.getControlManager();
                    String text = piece.getSegment().getSegmentController().getTextMap().get(ElementCollection.getIndex4(piece.getAbsoluteIndex(), piece.getOrientation()));
                    if(text == null) text = "";

                    final PlayerTextAreaInput t = new PlayerTextAreaInput("EDIT_DISPLAY_BLOCK_POPUP", cm.getState(), 400, 300, SendableGameState.TEXT_BLOCK_LIMIT, SendableGameState.TEXT_BLOCK_LINE_LIMIT + 1, "Edit Holo Projector", "", text, FontLibrary.FontSize.SMALL) {
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
                            f.block = piece.getTextBlockIndex();
                            f.text = entry;
                            System.err.println("[CLIENT]Text entry:\n\"" + f.text + "\"");
                            ss.getNetworkObject().textBlockResponsesAndChangeRequests.add(new RemoteTextBlockPair(f, false));
                            return true;
                        }

                        @Override
                        public String handleAutoComplete(String s, TextCallback callback, String prefix) {
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

               */
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddEvent.class, new Listener<SegmentPieceAddEvent>() {
            @Override
            public void onEvent(SegmentPieceAddEvent event) {
                if(ElementManager.getBlock("Holo Projector") != null && event.getNewType() == ElementManager.getBlock("Holo Projector").getId()) {
                    long indexAndOrientation = ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation());
                    event.getSegment().getSegmentController().getTextBlocks().remove(indexAndOrientation);
                    event.getSegmentController().getTextBlocks().add(indexAndOrientation);
                } else if(ElementManager.getBlock("Text Projector") != null && event.getNewType() == ElementManager.getBlock("Text Projector").getId()) {
                    long indexAndOrientation = ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation());
                    event.getSegment().getSegmentController().getTextBlocks().remove(indexAndOrientation);
                    event.getSegmentController().getTextBlocks().add(indexAndOrientation);
                //} else if(ElementManager.getBlock("Display Screen") != null && event.getNewType() == ElementManager.getBlock("Display Screen").getId()) {
                    //event.getSegment().getSegmentController().getTextBlocks().remove(ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation()));
                   // event.getSegment().getSegmentController().getTextBlocks().add(ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation()));
                } else if(event.getNewType() == ElementKeyMap.TEXT_BOX) {
                    event.getSegment().getSegmentController().getTextBlocks().remove(ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation()));
                    event.getSegment().getSegmentController().getTextBlocks().add(ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation()));
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceRemoveEvent.class, new Listener<SegmentPieceRemoveEvent>() {
            @Override
            public void onEvent(SegmentPieceRemoveEvent event) {
                if(ElementManager.getBlock("Holo Projector") != null && event.getType() == ElementManager.getBlock("Holo Projector").getId()) {
                    Segment segment = event.getSegment();
                    long absoluteIndex = segment.getAbsoluteIndex(event.getX(), event.getY(), event.getZ());
                    long indexAndOrientation = ElementCollection.getIndex4(absoluteIndex, event.getOrientation());
                    event.getSegment().getSegmentController().getTextBlocks().remove(indexAndOrientation);
                    event.getSegment().getSegmentController().getTextMap().remove(indexAndOrientation);
                } else if(ElementManager.getBlock("Text Projector") != null && event.getType() == ElementManager.getBlock("Text Projector").getId()) {
                    Segment segment = event.getSegment();
                    long absoluteIndex = segment.getAbsoluteIndex(event.getX(), event.getY(), event.getZ());
                    long indexAndOrientation = ElementCollection.getIndex4(absoluteIndex, event.getOrientation());
                    event.getSegment().getSegmentController().getTextBlocks().remove(indexAndOrientation);
                    event.getSegment().getSegmentController().getTextMap().remove(indexAndOrientation);
               // } else if(ElementManager.getBlock("Display Screen") != null && event.getType() == ElementManager.getBlock("Display Screen").getId()) {
                  //  Segment segment = event.getSegment();
//                    long indexAndOrientation = ElementCollection.getIndex4(absoluteIndex, event.getOrientation());
                    //event.getSegment().getSegmentController().getTextBlocks().remove(indexAndOrientation);
                    //event.getSegment().getSegmentController().getTextMap().remove(indexAndOrientation);
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddByMetadataEvent.class, new Listener<SegmentPieceAddByMetadataEvent>() {
            @Override
            public void onEvent(SegmentPieceAddByMetadataEvent event) {
                if(ElementManager.getBlock("Holo Projector") != null && event.getType() == ElementManager.getBlock("Holo Projector").getId()) {
                        event.getSegment().getSegmentController().getTextBlocks().add(event.getIndexAndOrientation());
                } else if(ElementManager.getBlock("Text Projector") != null && event.getType() == ElementManager.getBlock("Text Projector").getId()) {
                    event.getSegment().getSegmentController().getTextBlocks().add(event.getIndexAndOrientation());
                //} else if(ElementManager.getBlock("Display Screen") != null && event.getType() == ElementManager.getBlock("Display Screen").getId()) {
                   // event.getSegment().getSegmentController().getTextBlocks().add(event.getIndexAndOrientation());
                }
            }
        }, this);
    }

    private void registerFastListeners() {
        FastListenerCommon.textBoxListeners.add(new ProjectorDrawListener());
    }
}
