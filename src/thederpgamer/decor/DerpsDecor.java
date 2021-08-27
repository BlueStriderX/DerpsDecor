package thederpgamer.decor;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.register.ManagerContainerRegisterEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.decor.data.projector.HoloProjectorDrawData;
import thederpgamer.decor.data.projector.ProjectorDrawData;
import thederpgamer.decor.data.projector.TextProjectorDrawData;
import thederpgamer.decor.drawer.ProjectorDrawer;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.decor.HoloProjector;
import thederpgamer.decor.element.blocks.decor.TextProjector;
import thederpgamer.decor.gui.panel.holoprojector.HoloProjectorConfigDialog;
import thederpgamer.decor.gui.panel.textprojector.TextProjectorConfigDialog;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.modules.HoloProjectorModule;
import thederpgamer.decor.modules.TextProjectorModule;
import thederpgamer.decor.utils.ClipboardUtils;
import thederpgamer.decor.utils.ProjectorUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import java.util.ArrayList;

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

    //Constants
    public static final int HOLO_PROJECTOR = 1;
    public static final int TEXT_PROJECTOR = 2;

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
        //Decor Blocks
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

        StarLoader.registerListener(ManagerContainerRegisterEvent.class, new Listener<ManagerContainerRegisterEvent>() {
            @Override
            public void onEvent(ManagerContainerRegisterEvent event) {
                event.addModMCModule(new HoloProjectorModule(event.getSegmentController(), event.getContainer()));
                event.addModMCModule(new TextProjectorModule(event.getSegmentController(), event.getContainer()));
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
    }
}
