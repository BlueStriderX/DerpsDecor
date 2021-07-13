package thederpgamer.decor;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceAddByMetadataEvent;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.block.SegmentPieceRemoveEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.decor.drawer.HoloProjectorWorldDrawer;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.decor.HoloProjector;
import thederpgamer.decor.gui.panel.HoloProjectorConfigDialog;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.DataUtils;
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
    public DerpsDecor() {

    }
    public static void main(String[] args) {

    }

    //Data
    public HoloProjectorWorldDrawer projectorDrawer;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize(this);
        LogManager.initialize();

        //registerFastListeners();
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

        ElementManager.initialize();
    }

    private void registerListeners() {
        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                event.getModDrawables().add(projectorDrawer = new HoloProjectorWorldDrawer());
            }
        }, this);

        StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
            @Override
            public void onEvent(final SegmentPieceActivateByPlayer event) {
                final SegmentPiece piece = event.getSegmentPiece();
                /*
                if(piece.getType() == Objects.requireNonNull(ElementManager.getBlock("Display Screen")).getId()) {
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

                            if(entry.toLowerCase().contains("<img>")) {
                                //Log image details in case server staff need to remove inappropriate images
                                LogManager.logMessage(MessageType.INFO, "An image link was entered into a display module on entity \"" + event.getSegmentPiece().getSegmentController().getName() + "\" by player " + GameClient.getClientPlayerState().getName() + ":\n\"" + entry + "\"");
                            }
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
                } else

                 */
                if(piece.getType() == ElementManager.getBlock("Holo Projector").getId()) {

                    HoloProjectorConfigDialog configDialog = new HoloProjectorConfigDialog();
                    configDialog.setSegmentPiece(piece);
                    configDialog.activate();
                    if(GameClient.getClientState() != null) GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().suspend(true);
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddEvent.class, new Listener<SegmentPieceAddEvent>() {
            @Override
            public void onEvent(SegmentPieceAddEvent event) {
                /*
                if(event.getNewType() == Objects.requireNonNull(ElementManager.getBlock("Display Screen")).getId()) {
                    event.getSegment().getSegmentController().getTextBlocks().add(ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation()));
                } else
                */
                if(ElementManager.getBlock("Holo Projector") != null) {
                    if (event.getNewType() == Objects.requireNonNull(ElementManager.getBlock("Holo Projector")).getId()) {
                        DataUtils.registerNewProjector(event.getSegmentController().getSegmentBuffer().getPointUnsave(event.getAbsIndex()));
                    }
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceRemoveEvent.class, new Listener<SegmentPieceRemoveEvent>() {
            @Override
            public void onEvent(SegmentPieceRemoveEvent event) {
                /*if(event.getType() == Objects.requireNonNull(ElementManager.getBlock("Display Screen")).getId()) {
                    Segment segment = event.getSegment();
                    long absoluteIndex = segment.getAbsoluteIndex(event.getX(), event.getY(), event.getZ());
                    long indexAndOrientation = ElementCollection.getIndex4(absoluteIndex, event.getOrientation());
                    event.getSegment().getSegmentController().getTextBlocks().remove(indexAndOrientation);
                    event.getSegment().getSegmentController().getTextMap().remove(indexAndOrientation);
                } else
                 */
                if(ElementManager.getBlock("Holo Projector") != null) {
                    if (event.getType() == Objects.requireNonNull(ElementManager.getBlock("Holo Projector")).getId()) {
                        DataUtils.removeProjector(event.getSegment().getSegmentController(), new Vector3i(event.getX(), event.getY(), event.getZ()));
                    }
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddByMetadataEvent.class, new Listener<SegmentPieceAddByMetadataEvent>() {
            @Override
            public void onEvent(SegmentPieceAddByMetadataEvent event) {
                /*
                if(event.getType() == Objects.requireNonNull(ElementManager.getBlock("Display Screen")).getId()) {
                    event.getSegment().getSegmentController().getTextBlocks().add(event.getIndexAndOrientation());
                } else
                 */
                if(ElementManager.getBlock("Holo Projector") != null) {
                    if(event.getType() == Objects.requireNonNull(ElementManager.getBlock("Holo Projector")).getId()) {
                        DataUtils.registerNewProjector(event.getAsSegmentPiece(new SegmentPiece()));
                    }
                }
            }
        }, this);
    }
}
