package thederpgamer.decor.element;

import api.config.BlockConfig;
import api.utils.textures.StarLoaderTexture;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCategory;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.element.blocks.BlockGroup;
import thederpgamer.decor.element.blocks.Factory;
import thederpgamer.decor.element.items.Item;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 04/25/2021
 */
public class ElementManager {

	public enum FactoryType {
		NONE,
		CAPSULE_REFINERY,
		MICRO_ASSEMBLER,
		BASIC_FACTORY,
		STANDARD_FACTORY,
		ADVANCED_FACTORY
	}

	private static final ArrayList<Block> blockList = new ArrayList<>();
	private static final ArrayList<Factory> factoryList = new ArrayList<>();
	private static final ArrayList<Item> itemList = new ArrayList<>();

	public static void initialize() {
		for (Block block : blockList) block.initialize();
		for (Factory factory : factoryList) factory.initialize();
		for (Item item : itemList) item.initialize();
	}

	public static void createGraphics() {
		StarLoaderTexture.runOnGraphicsThread(
				new Runnable() {
					@Override
					public void run() {
						if (GraphicsContext.initialized) {
							for (Block block : blockList) block.createGraphics();
							for (Factory factory : factoryList) factory.createGraphics();
							for (Item item : itemList) item.createGraphics();
						}
					}
				});
	}

	public static void doOverwrites() {
		for(ElementInformation info : ElementKeyMap.getInfoArray()) {
			try {
				if(info.getName().toLowerCase().contains("console") && info.hasLod()) {
					info.signal = true;
					info.canActivate = true;

					info.controlling.add(ElementKeyMap.LOGIC_BUTTON_NORM);
					info.controlling.add(ElementKeyMap.ACTIVAION_BLOCK_ID);

					ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).controlledBy.add(info.getId());
					ElementKeyMap.getInfo(ElementKeyMap.ACTIVAION_BLOCK_ID).controlledBy.add(info.getId());
				} else if(info.getId() == ElementKeyMap.SHIPYARD_COMPUTER) {
					//info.signal = true;
					//info.canActivate = true;

					info.controlling.add(ElementKeyMap.LOGIC_BUTTON_NORM);
					info.controlling.add(ElementKeyMap.ACTIVAION_BLOCK_ID);

					ElementKeyMap.getInfo(ElementKeyMap.LOGIC_BUTTON_NORM).controlledBy.add(info.getId());
					ElementKeyMap.getInfo(ElementKeyMap.ACTIVAION_BLOCK_ID).controlledBy.add(info.getId());
				}
			} catch (NullPointerException ignored) {
			}
		}
	}

	public static ArrayList<Block> getAllBlocks() {
		return blockList;
	}

	public static ArrayList<Factory> getAllFactories() {
		return factoryList;
	}

	public static ArrayList<Item> getAllItems() {
		return itemList;
	}

	public static Block getBlock(short id) {
		for (Block blockElement : getAllBlocks())
			if (blockElement.getBlockInfo().getId() == id) return blockElement;
		return null;
	}

	public static Block getBlock(String blockName) {
		for (Block block : getAllBlocks()) {
			if (block.getBlockInfo().getName().equalsIgnoreCase(blockName)) return block;
		}
		return null;
	}

	public static Block getBlock(SegmentPiece segmentPiece) {
		for (Block block : getAllBlocks()) if (block.getId() == segmentPiece.getType()) return block;
		return null;
	}

	public static Factory getFactory(String factoryName) {
		for (Factory factory : getAllFactories()) {
			if (factory.getBlockInfo().getName().equalsIgnoreCase(factoryName)) return factory;
		}
		return null;
	}

	public static Factory getFactory(SegmentPiece segmentPiece) {
		for (Factory factory : getAllFactories())
			if (factory.getId() == segmentPiece.getType()) return factory;
		return null;
	}

	public static Item getItem(String itemName) {
		for (Item item : getAllItems()) {
			if (item.getItemInfo().getName().equalsIgnoreCase(itemName)) return item;
		}
		return null;
	}

	public static void addBlockGroup(BlockGroup blockGroup) {
		Block[] blocks = blockGroup.getBlocks();
		for (Block block : blocks) addBlock(block);
	}

	public static void addBlock(Block block) {
		blockList.add(block);
	}

	public static void addFactory(Factory factory) {
		factoryList.add(factory);
	}

	public static void addItem(Item item) {
		itemList.add(item);
	}

	public static ElementCategory getCategory(String path) {
		String[] split = path.split("\\.");
		ElementCategory category = ElementKeyMap.getCategoryHirarchy();
		for (String s : split) {
			boolean createNew = false;
			if (category.hasChildren()) {
				for (ElementCategory child : category.getChildren()) {
					if (child.getCategory().equalsIgnoreCase(s)) {
						category = child;
						break;
					}
					createNew = true;
				}
			} else createNew = true;
			if (createNew) category = BlockConfig.newElementCategory(category, StringUtils.capitalize(s));
		}
		return category;
	}

	public static ElementInformation getInfo(String name) {
		Block block = getBlock(name);
		if (block != null) return block.getBlockInfo();
		else {
			Factory factory = getFactory(name);
			if (factory != null) return factory.getBlockInfo();
			else {
				Item item = getItem(name);
				if (item != null) return item.getItemInfo();
				else {
					for (ElementInformation elementInfo : ElementKeyMap.getInfoArray()) {
						if (elementInfo.getName().equalsIgnoreCase(name)) return elementInfo;
					}
				}
			}
		}
		return null;
	}
}
