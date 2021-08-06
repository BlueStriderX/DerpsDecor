package thederpgamer.decor;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceAddByMetadataEvent;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.decor.drawer.ProjectorDrawer;
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

    //Graphics
    public ProjectorDrawer projectorDrawer;

    //Utils
    public ClipboardUtils clipboard;

    @Override
    public void onEnable() {
        instance = this;
        clipboard = new ClipboardUtils();
        ConfigManager.initialize(this);
        LogManager.initialize();
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
        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                event.getModDrawables().add(projectorDrawer = new ProjectorDrawer());
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
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddEvent.class, new Listener<SegmentPieceAddEvent>() {
            @Override
            public void onEvent(SegmentPieceAddEvent event) {
                if(ElementManager.getBlock("Holo Projector") != null && event.getNewType() == ElementManager.getBlock("Holo Projector").getId()) {
                    projectorDrawer.addProjector(event.getSegmentController().getSegmentBuffer().getPointUnsave(event.getAbsIndex()));
                } else if(ElementManager.getBlock("Text Projector") != null && event.getNewType() == ElementManager.getBlock("Text Projector").getId()) {
                    projectorDrawer.addProjector(event.getSegmentController().getSegmentBuffer().getPointUnsave(event.getAbsIndex()));
                }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddByMetadataEvent.class, new Listener<SegmentPieceAddByMetadataEvent>() {
            @Override
            public void onEvent(SegmentPieceAddByMetadataEvent event) {
                if(ElementManager.getBlock("Holo Projector") != null && event.getType() == ElementManager.getBlock("Holo Projector").getId()) {
                    projectorDrawer.addProjector(event.getAsSegmentPiece(new SegmentPiece()));
                } else if(ElementManager.getBlock("Text Projector") != null && event.getType() == ElementManager.getBlock("Text Projector").getId()) {
                    projectorDrawer.addProjector(event.getAsSegmentPiece(new SegmentPiece()));
                }
            }
        }, this);
    }
}
