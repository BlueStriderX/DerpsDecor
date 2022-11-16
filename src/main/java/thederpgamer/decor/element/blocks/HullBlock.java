package thederpgamer.decor.element.blocks;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementCategory;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.manager.ResourceManager;

import java.util.Locale;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class HullBlock {

	public enum Color {
		GREY("Grey"),
		WHITE("White"),
		DARK_GREY("Dark Grey"),
		BLACK("Black"),
		YELLOW("Yellow"),
		ORANGE("Orange"),
		RED("Red"),
		PINK("Pink"),
		PURPLE("Purple"),
		BLUE("Blue"),
		TEAL("Teal"),
		GREEN("Green"),
		BROWN("Brown");

		public final String name;

		Color(String name) {
			this.name = name;
		}

		public java.awt.Color getColor() {
			switch(this) {
				default:
				case GREY:
					return new java.awt.Color(0.5f, 0.5f, 0.5f);
				case WHITE:
					return java.awt.Color.WHITE;
				case DARK_GREY:
					return new java.awt.Color(0.25f, 0.25f, 0.25f);
				case BLACK:
					return java.awt.Color.BLACK;
				case YELLOW:
					return java.awt.Color.YELLOW;
				case ORANGE:
					return java.awt.Color.ORANGE;
				case RED:
					return java.awt.Color.RED;
				case PINK:
					return java.awt.Color.PINK;
				case PURPLE:
					return java.awt.Color.MAGENTA;
				case BLUE:
					return java.awt.Color.BLUE;
				case TEAL:
					return new java.awt.Color(0, 0.5f, 0.5f);
				case GREEN:
					return java.awt.Color.GREEN;
				case BROWN:
					return new java.awt.Color(0.5f, 0.25f, 0);
			}
		}
	}

	public enum Type {
		BASIC("Basic"),
		STANDARD("Standard"),
		ADVANCED("Advanced");

		public final String name;

		Type(String name) {
			this.name = name;
		}
	}

	public final Block[] colorVariants = new Block[13];

	public HullBlock(String name, Type type) {
		for(int i = 0; i < colorVariants.length; i++) {
			String colorName = Color.values()[i].name;
			ElementCategory category;
			short baseId = 0;
			switch(Color.values()[i]) {
				case GREY: {
					switch(type) {
						case BASIC:
							baseId = 589;
							break;
						case STANDARD:
							baseId = 5;
							break;
						case ADVANCED:
							baseId = 263;
							break;
					}
				}
				case WHITE: {
					switch(type) {
						case BASIC:
							baseId = 608;
							break;
						case STANDARD:
							baseId = 81;
							break;
						case ADVANCED:
							baseId = 271;
							break;
					}
				}
				case DARK_GREY: {
					switch(type) {
						case BASIC:
							baseId = 828;
							break;
						case STANDARD:
							baseId = 818;
							break;
						case ADVANCED:
							baseId = 823;
							break;
					}
				}
				case BLACK: {
					switch(type) {
						case BASIC:
							baseId = 603;
							break;
						case STANDARD:
							baseId = 75;
							break;
						case ADVANCED:
							baseId = 264;
							break;
					}
				}
				case YELLOW: {
					switch(type) {
						case BASIC:
							baseId = 628;
							break;
						case STANDARD:
							baseId = 79;
							break;
						case ADVANCED:
							baseId = 270;
							break;
					}
				}
				case ORANGE: {
					switch(type) {
						case BASIC:
							baseId = 633;
							break;
						case STANDARD:
							baseId = 426;
							break;
						case ADVANCED:
							baseId = 431;
							break;
					}
				}
				case RED: {
					switch(type) {
						case BASIC:
							baseId = 638;
							break;
						case STANDARD:
							baseId = 76;
							break;
						case ADVANCED:
							baseId = 265;
							break;
					}
				}
				case PINK: {
					switch(type) {
						case BASIC:
							baseId = 912;
							break;
						case STANDARD:
							baseId = 902;
							break;
						case ADVANCED:
							baseId = 907;
							break;
					}
				}
				case PURPLE: {
					switch(type) {
						case BASIC:
							baseId = 613;
							break;
						case STANDARD:
							baseId = 69;
							break;
						case ADVANCED:
							baseId = 266;
							break;
					}
				}
				case BLUE: {
					switch(type) {
						case BASIC:
							baseId = 618;
							break;
						case STANDARD:
							baseId = 77;
							break;
						case ADVANCED:
							baseId = 267;
							break;
					}
				}
				case TEAL: {
					switch(type) {
						case BASIC:
							baseId = 878;
							break;
						case STANDARD:
							baseId = 868;
							break;
						case ADVANCED:
							baseId = 873;
							break;
					}
				}
				case GREEN: {
					switch(type) {
						case BASIC:
							baseId = 623;
							break;
						case STANDARD:
							baseId = 78;
							break;
						case ADVANCED:
							baseId = 268;
							break;
					}
				}
				case BROWN: {
					switch(type) {
						case BASIC:
							baseId = 643;
							break;
						case STANDARD:
							baseId = 70;
							break;
						case ADVANCED:
							baseId = 269;
							break;
					}
				}
			}
			category = ElementKeyMap.getInfo(baseId).getType();
			final short finalBaseId = baseId;
			colorVariants[i] = new Block(colorName + type.name + " Armor " + name, category) {
				@Override
				public void initialize() {
					blockInfo.setDescription(ElementKeyMap.getInfo(finalBaseId).getDescription());
					blockInfo.setCanActivate(false);
					blockInfo.setInRecipe(false);
					blockInfo.setShoppable(false);
					blockInfo.setOrientatable(false);

					if(GraphicsContext.initialized) {
						try {
							short textureId = (short) ResourceManager.getTexture(blockInfo.name.toLowerCase(Locale.ENGLISH).replaceAll(" ", "-")).getTextureId();
							blockInfo.setTextureId(new short[] {textureId, textureId, textureId, textureId, textureId, textureId});
							blockInfo.setBuildIconNum(ResourceManager.getTexture(blockInfo.name.toLowerCase(Locale.ENGLISH).replaceAll(" ", "-") + "-icon").getTextureId());
						} catch(Exception ignored) {}
					}
					BlockConfig.add(blockInfo);
				}

				@Override
				public void createGraphics() {

				}
			};
		}
	}
}
