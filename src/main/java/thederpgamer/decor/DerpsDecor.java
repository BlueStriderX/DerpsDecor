package thederpgamer.decor;

import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.register.ManagerContainerRegisterEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.game.module.util.SimpleDataStorageMCModule;
import glossar.GlossarCategory;
import glossar.GlossarEntry;
import glossar.GlossarInit;
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
import thederpgamer.decor.element.blocks.decor.HoloTable;
import thederpgamer.decor.element.blocks.decor.TextProjector;
import thederpgamer.decor.element.blocks.decor.TileBlocks;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.manager.EventManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.systems.modules.HoloProjectorModule;
import thederpgamer.decor.systems.modules.TextProjectorModule;
import thederpgamer.decor.utils.*;

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
	private final String[] overwriteClasses = {"GUIQuickReferencePanel"
			// "ElementCollection",
			// "ElementCollectionMesh"
	};
	// Utils
	public ClipboardUtils clipboard;
	private BlockIconUtils iconUtils;

	public DerpsDecor() {}

	public static DerpsDecor getInstance() {
		return instance;
	}

	public static void main(String[] args) {}

	@Override
	public byte[] onClassTransform(String className, byte[] byteCode) {
		for(String name : overwriteClasses)
			if(className.endsWith(name)) return overwriteClass(className, byteCode);
		return super.onClassTransform(className, byteCode);
	}

	@Override
	public void onEnable() {
		instance = this;
		clipboard = new ClipboardUtils();
		ConfigManager.initialize(this);
		LogManager.initialize();
		SegmentPieceUtils.initialize();
		EventManager.initialize(this);
		registerCommands();
	}

	@Override
	public void onClientCreated(ClientInitializeEvent clientInitializeEvent) {
		super.onClientCreated(clientInitializeEvent);
		initGlossary();
	}

	@Override
	public void onBlockConfigLoad(BlockConfig config) {
		ElementManager.addBlock(new HoloProjector());
		ElementManager.addBlock(new TextProjector());
		// ElementManager.addBlock(new StrutConnector());
		// ElementManager.addBlock(new DisplayScreen());
		ElementManager.addBlock(new HoloTable());
		// ElementManager.addBlock(new StorageCapsule());
		//ElementManager.addBlock(new TileBlocks.SmallDarkTiles());
		//ElementManager.addBlock(new TileBlocks.SmallLightTiles());
		//ElementManager.addBlock(new TileBlocks.LargeDarkTiles());
		//ElementManager.addBlock(new TileBlocks.LargeLightTiles());
		ElementManager.doOverwrites();
		ElementManager.initialize();
	}

	@Override
	public void onResourceLoad(ResourceLoader loader) {
		ResourceManager.loadResources(this, loader);
	}

	private void initGlossary() {
		GlossarInit.initGlossar(this);
		GlossarCategory derpsDecor = new GlossarCategory("DerpsDecor");
		derpsDecor.addEntry(new GlossarEntry("Holo Projector", "A holographic projector that can display images. Input a direct image URL ending in .png or .gif into the src field to display an image."));
		derpsDecor.addEntry(new GlossarEntry("Text Projector", "A holographic projector that can display text. Input text into the src field to display it."));
		derpsDecor.addEntry(new GlossarEntry("Command Selection", "You can activate shipyard commands using logic blocks. To do so, place an activation module and connect it to the shipyard computer. Then, put a display module next to the activation module and input the name of the shipyard command you want to activate on the first line, and any arguments on the second separated by commas."));
		GlossarInit.addCategory(derpsDecor);
	}

	private void registerCommands() {
		StarLoader.registerCommand(new ClearProjectorsCommand());
	}

	private byte[] overwriteClass(String className, byte[] byteCode) {
		byte[] bytes = null;
		try {
			ZipInputStream file =
					new ZipInputStream(new FileInputStream(this.getSkeleton().getJarFile()));
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
