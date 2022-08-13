package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.utils.BlockIconUtils;

public class TileBlocks {

	private static final String categoryPath = "";

	public static class SmallDarkTiles extends Block {

		public SmallDarkTiles() {
			super("Small Dark Tiles", ElementManager.getCategory(categoryPath));
		}

		@Override
		public void initialize() {
			blockInfo.setDescription("Small dark decorative tiles.");
			blockInfo.setCanActivate(true);
			blockInfo.setInRecipe(true);
			blockInfo.setShoppable(true);
			blockInfo.setPrice(ElementKeyMap.getInfo(205).price);
			blockInfo.setOrientatable(false);
			blockInfo.setIndividualSides(1);

			BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(205).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(205).getFactoryBakeTime(), new FactoryResource(1, (short) 205));
			BlockConfig.add(blockInfo);
		}

		@Override
		public void createGraphics() {
			BlockIconUtils.createBlockIcon(blockInfo);
		}
	}

	public static class SmallLightTiles extends Block {

		public SmallLightTiles() {
			super("Small Light Tiles", ElementManager.getCategory(categoryPath));
		}

		@Override
		public void initialize() {
			blockInfo.setDescription("Small light decorative tiles.");
			blockInfo.setCanActivate(true);
			blockInfo.setInRecipe(true);
			blockInfo.setShoppable(true);
			blockInfo.setPrice(ElementKeyMap.getInfo(205).price);
			blockInfo.setOrientatable(false);
			blockInfo.setIndividualSides(1);

			BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(205).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(205).getFactoryBakeTime(), new FactoryResource(1, (short) 161));
			BlockConfig.add(blockInfo);
		}

		@Override
		public void createGraphics() {
			BlockIconUtils.createBlockIcon(blockInfo);
		}
	}

	public static class LargeDarkTiles extends Block {

		public LargeDarkTiles() {
			super("Large Dark Tiles", ElementManager.getCategory(categoryPath));
		}

		@Override
		public void initialize() {
			blockInfo.setDescription("Large dark decorative tiles.");
			blockInfo.setCanActivate(true);
			blockInfo.setInRecipe(true);
			blockInfo.setShoppable(true);
			blockInfo.setPrice(ElementKeyMap.getInfo(205).price);
			blockInfo.setOrientatable(false);
			blockInfo.setIndividualSides(1);

			BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(205).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(205).getFactoryBakeTime(), new FactoryResource(1, (short) 205));
			BlockConfig.add(blockInfo);
		}

		@Override
		public void createGraphics() {
			BlockIconUtils.createBlockIcon(blockInfo);
		}
	}

	public static class LargeLightTiles extends Block {

		public LargeLightTiles() {
			super("Large Light Tiles", ElementManager.getCategory(categoryPath));
		}

		@Override
		public void initialize() {
			blockInfo.setDescription("Large light decorative tiles.");
			blockInfo.setCanActivate(true);
			blockInfo.setInRecipe(true);
			blockInfo.setShoppable(true);
			blockInfo.setPrice(ElementKeyMap.getInfo(205).price);
			blockInfo.setOrientatable(false);
			blockInfo.setIndividualSides(1);

			BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(205).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(205).getFactoryBakeTime(), new FactoryResource(1, (short) 161));
			BlockConfig.add(blockInfo);
		}

		@Override
		public void createGraphics() {
			BlockIconUtils.createBlockIcon(blockInfo);
		}
	}
}
