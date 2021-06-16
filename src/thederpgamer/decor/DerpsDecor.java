package thederpgamer.decor;

import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceAddByMetadataEvent;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.block.SegmentPieceRemoveEvent;
import api.listener.fastevents.FastListenerCommon;
import api.mod.StarLoader;
import api.mod.StarMod;
import org.schema.game.client.controller.PlayerTextAreaInput;
import org.schema.game.client.controller.element.world.ClientSegmentProvider;
import org.schema.game.client.controller.manager.ingame.PlayerInteractionControlManager;
import org.schema.game.common.controller.SendableSegmentProvider;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.SendableGameState;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.world.Segment;
import org.schema.game.network.objects.remote.RemoteTextBlockPair;
import org.schema.game.network.objects.remote.TextBlockPair;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.decor.DisplayScreen;
import thederpgamer.decor.listeners.TextDrawEvent;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.manager.ResourceManager;
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

    @Override
    public void onEnable() {
        instance = this;
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
        ElementManager.addBlock(new DisplayScreen());

        ElementManager.initialize();
    }

    private void registerFastListeners() {
        FastListenerCommon.textBoxListeners.add(new TextDrawEvent());
    }

    private void registerListeners() {
        StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
            @Override
            public void onEvent(final SegmentPieceActivateByPlayer event) {
                final SegmentPiece piece = event.getSegmentPiece();
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
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddEvent.class, new Listener<SegmentPieceAddEvent>() {
            @Override
            public void onEvent(SegmentPieceAddEvent event) {
                if(event.getNewType() == Objects.requireNonNull(ElementManager.getBlock("Display Screen")).getId()) {
                    event.getSegment().getSegmentController().getTextBlocks().add(ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation()));
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
