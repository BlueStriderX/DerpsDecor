package thederpgamer.decor;

import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.register.ManagerContainerRegisterEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.game.module.util.SimpleDataStorageMCModule;
import org.apache.commons.io.IOUtils;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.decor.commands.ClearProjectorsCommand;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.drawer.GlobalDrawManager;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.ActivationInterface;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.element.blocks.decor.HoloProjector;
import thederpgamer.decor.element.blocks.decor.StrutConnector;
import thederpgamer.decor.element.blocks.decor.TextProjector;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.systems.inventories.StorageCapsuleModule;
import thederpgamer.decor.systems.modules.HoloProjectorModule;
import thederpgamer.decor.systems.modules.TextProjectorModule;
import thederpgamer.decor.utils.ClipboardUtils;
import thederpgamer.decor.utils.ProjectorUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;
import thederpgamer.decor.utils.ServerUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Main class for DerpsDecor mod.
 *
 * @author TheDerpGamer
 * @version 1.1 - [11/12/2021]
 */
public class DerpsDecor extends StarMod {

	// Instance
	private static DerpsDecor instance;
	// Other
	private final String[] overwriteClasses = {
			"GUIQuickReferencePanel"
			// "Constructing",
			// "ElementCollection",
			// "ElementCollectionMesh"
	};
	// Utils
	public ClipboardUtils clipboard;

	public DerpsDecor() { }

	public static DerpsDecor getInstance() {
		return instance;
	}

	public static void main(String[] args) { }

	@Override
	public byte[] onClassTransform(String className, byte[] byteCode) {
		for(String name : overwriteClasses) if(className.endsWith(name)) return overwriteClass(className, byteCode);
		return super.onClassTransform(className, byteCode);
	}

	@Override
	public void onEnable() {
		instance = this;
		clipboard = new ClipboardUtils();
		ConfigManager.initialize(this);
		LogManager.initialize();
		SegmentPieceUtils.initialize();
		registerListeners();
		registerCommands();
	}

	@Override
	public void onBlockConfigLoad(BlockConfig config) {
		ElementManager.addBlock(new HoloProjector());
		ElementManager.addBlock(new TextProjector());
		ElementManager.addBlock(new StrutConnector());
		// ElementManager.addBlock(new DisplayScreen());
		// ElementManager.addBlock(new HoloTable());
		// ElementManager.addBlock(new StorageCapsule());
		// ElementManager.addBlock(new CakeBlock());
		ElementManager.doOverwrites();
		ElementManager.initialize();
	}

	@Override
	public void onResourceLoad(ResourceLoader loader) {
		ResourceManager.loadResources(this, loader);
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
				//event.addModMCModule(new StrutConnectorModule(event.getSegmentController(), event.getContainer()));
				event.addModMCModule(new StorageCapsuleModule(event.getSegmentController(), event.getContainer()));
			}
		}, this);

		StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
			@Override
			public void onEvent(SegmentPieceActivateByPlayer event) {
				for(Block block : ElementManager.getAllBlocks()) {
					if(block instanceof ActivationInterface && block.getId() == event.getSegmentPiece().getType()) {
						((ActivationInterface) block).onPlayerActivation(event);
						return;
					}
				}

           /*
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
             */
			}
		}, this);

		StarLoader.registerListener(SegmentPieceActivateEvent.class, new Listener<SegmentPieceActivateEvent>() {
			@Override
			public void onEvent(SegmentPieceActivateEvent event) {
				for(Block block : ElementManager.getAllBlocks()) {
					if(block instanceof ActivationInterface && block.getId() == event.getSegmentPiece().getType()) {
						((ActivationInterface) block).onLogicActivation(event);
						break;
					}
				}

				if(event.isServer()) {
					if((event.getSegmentPiece().getType() == ElementKeyMap.ACTIVAION_BLOCK_ID || event.getSegmentPiece().getType() == ElementKeyMap.LOGIC_BUTTON_NORM)) {
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
			}
		}, this);

    /* Todo: Fix display screen orientation
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
     */
	}

	private void registerCommands() {
		StarLoader.registerCommand(new ClearProjectorsCommand());
	}

	private byte[] overwriteClass(String className, byte[] byteCode) {
		byte[] bytes = null;
		try {
			ZipInputStream file = new ZipInputStream(new FileInputStream(this.getSkeleton().getJarFile()));
			while(true) {
				ZipEntry nextEntry = file.getNextEntry();
				if(nextEntry == null) break;
				if(nextEntry.getName().endsWith(className + ".class")) bytes = IOUtils.toByteArray(file);
			}
			file.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		if(bytes != null) return bytes;
		else return byteCode;
	}
}
