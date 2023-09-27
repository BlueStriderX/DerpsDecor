package thederpgamer.decor.manager;

import api.utils.textures.StarLoaderTexture;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.OutlineEffect;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.decor.DerpsDecor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Manager class for handling resource loading.
 *
 * @author TheDerpGamer
 */
public class ResourceManager {
	private static final String[] textureNames = {"holo-projector-front", "text-projector-front", "large-dark-tiles", "small-dark-tiles", "large-light-tiles", "small-light-tiles"};
	/*
			"grey-basic-armor-small-tiles",
			"grey-basic-armor-small-tiles-icon",
			"grey-standard-armor-small-tiles",
			"grey-standard-armor-small-tiles-icon",
			"grey-advanced-armor-small-tiles",
			"grey-advanced-armor-small-tiles-icon",

			"grey-basic-armor-large-tiles",
			"grey-basic-armor-large-tiles-icon",
			"grey-standard-armor-large-tiles",
			"grey-standard-armor-large-tiles-icon",
			"grey-advanced-armor-large-tiles",
			"grey-advanced-armor-large-tiles-icon",

			"white-basic-armor-small-tiles",
			"white-basic-armor-small-tiles-icon",
			"white-standard-armor-small-tiles",
			"white-standard-armor-small-tiles-icon",
			"white-advanced-armor-small-tiles",
			"white-advanced-armor-small-tiles-icon",

			"white-basic-armor-large-tiles",
			"white-basic-armor-large-tiles-icon",
			"white-standard-armor-large-tiles",
			"white-standard-armor-large-tiles-icon",
			"white-advanced-armor-large-tiles",
			"white-advanced-armor-large-tiles-icon",

			"dark-grey-basic-armor-small-tiles",
			"dark-grey-basic-armor-small-tiles-icon",
			"dark-grey-standard-armor-small-tiles",
			"dark-grey-standard-armor-small-tiles-icon",
			"dark-grey-advanced-armor-small-tiles",
			"dark-grey-advanced-armor-small-tiles-icon",

			"dark-grey-basic-armor-large-tiles",
			"dark-grey-basic-armor-large-tiles-icon",
			"dark-grey-standard-armor-large-tiles",
			"dark-grey-standard-armor-large-tiles-icon",
			"dark-grey-advanced-armor-large-tiles",
			"dark-grey-advanced-armor-large-tiles-icon",

			"black-basic-armor-small-tiles",
			"black-basic-armor-small-tiles-icon",
			"black-standard-armor-small-tiles",
			"black-standard-armor-small-tiles-icon",
			"black-advanced-armor-small-tiles",
			"black-advanced-armor-small-tiles-icon",

			"black-basic-armor-large-tiles",
			"black-basic-armor-large-tiles-icon",
			"black-standard-armor-large-tiles",
			"black-standard-armor-large-tiles-icon",
			"black-advanced-armor-large-tiles",
			"black-advanced-armor-large-tiles-icon",

			"yellow-basic-armor-small-tiles",
			"yellow-basic-armor-small-tiles-icon",
			"yellow-standard-armor-small-tiles",
			"yellow-standard-armor-small-tiles-icon",
			"yellow-advanced-armor-small-tiles",
			"yellow-advanced-armor-small-tiles-icon",

			"yellow-basic-armor-large-tiles",
			"yellow-basic-armor-large-tiles-icon",
			"yellow-standard-armor-large-tiles",
			"yellow-standard-armor-large-tiles-icon",
			"yellow-advanced-armor-large-tiles",
			"yellow-advanced-armor-large-tiles-icon",

			"orange-basic-armor-small-tiles",
			"orange-basic-armor-small-tiles-icon",
			"orange-standard-armor-small-tiles",
			"orange-standard-armor-small-tiles-icon",
			"orange-advanced-armor-small-tiles",
			"orange-advanced-armor-small-tiles-icon",

			"orange-basic-armor-large-tiles",
			"orange-basic-armor-large-tiles-icon",
			"orange-standard-armor-large-tiles",
			"orange-standard-armor-large-tiles-icon",
			"orange-advanced-armor-large-tiles",
			"orange-advanced-armor-large-tiles-icon",

			"red-basic-armor-small-tiles",
			"red-basic-armor-small-tiles-icon",
			"red-standard-armor-small-tiles",
			"red-standard-armor-small-tiles-icon",
			"red-advanced-armor-small-tiles",
			"red-advanced-armor-small-tiles-icon",

			"red-basic-armor-large-tiles",
			"red-basic-armor-large-tiles-icon",
			"red-standard-armor-large-tiles",
			"red-standard-armor-large-tiles-icon",
			"red-advanced-armor-large-tiles",
			"red-advanced-armor-large-tiles-icon",

			"pink-basic-armor-small-tiles",
			"pink-basic-armor-small-tiles-icon",
			"pink-standard-armor-small-tiles",
			"pink-standard-armor-small-tiles-icon",
			"pink-advanced-armor-small-tiles",
			"pink-advanced-armor-small-tiles-icon",

			"pink-basic-armor-large-tiles",
			"pink-basic-armor-large-tiles-icon",
			"pink-standard-armor-large-tiles",
			"pink-standard-armor-large-tiles-icon",
			"pink-advanced-armor-large-tiles",
			"pink-advanced-armor-large-tiles-icon",

			"purple-basic-armor-small-tiles",
			"purple-basic-armor-small-tiles-icon",
			"purple-standard-armor-small-tiles",
			"purple-standard-armor-small-tiles-icon",
			"purple-advanced-armor-small-tiles",
			"purple-advanced-armor-small-tiles-icon",

			"purple-basic-armor-large-tiles",
			"purple-basic-armor-large-tiles-icon",
			"purple-standard-armor-large-tiles",
			"purple-standard-armor-large-tiles-icon",
			"purple-advanced-armor-large-tiles",
			"purple-advanced-armor-large-tiles-icon",

			"blue-basic-armor-small-tiles",
			"blue-basic-armor-small-tiles-icon",
			"blue-standard-armor-small-tiles",
			"blue-standard-armor-small-tiles-icon",
			"blue-advanced-armor-small-tiles",
			"blue-advanced-armor-small-tiles-icon",

			"blue-basic-armor-large-tiles",
			"blue-basic-armor-large-tiles-icon",
			"blue-standard-armor-large-tiles",
			"blue-standard-armor-large-tiles-icon",
			"blue-advanced-armor-large-tiles",
			"blue-advanced-armor-large-tiles-icon",

			"teal-basic-armor-small-tiles",
			"teal-basic-armor-small-tiles-icon",
			"teal-standard-armor-small-tiles",
			"teal-standard-armor-small-tiles-icon",
			"teal-advanced-armor-small-tiles",
			"teal-advanced-armor-small-tiles-icon",

			"teal-basic-armor-large-tiles",
			"teal-basic-armor-large-tiles-icon",
			"teal-standard-armor-large-tiles",
			"teal-standard-armor-large-tiles-icon",
			"teal-advanced-armor-large-tiles",
			"teal-advanced-armor-large-tiles-icon",

			"green-basic-armor-small-tiles",
			"green-basic-armor-small-tiles-icon",
			"green-standard-armor-small-tiles",
			"green-standard-armor-small-tiles-icon",
			"green-advanced-armor-small-tiles",
			"green-advanced-armor-small-tiles-icon",

			"green-basic-armor-large-tiles",
			"green-basic-armor-large-tiles-icon",
			"green-standard-armor-large-tiles",
			"green-standard-armor-large-tiles-icon",
			"green-advanced-armor-large-tiles",
			"green-advanced-armor-large-tiles-icon",

			"brown-basic-armor-small-tiles",
			"brown-basic-armor-small-tiles-icon",
			"brown-standard-armor-small-tiles",
			"brown-standard-armor-small-tiles-icon",
			"brown-advanced-armor-small-tiles",
			"brown-advanced-armor-small-tiles-icon",

			"brown-basic-armor-large-tiles",
			"brown-basic-armor-large-tiles-icon",
			"brown-standard-armor-large-tiles",
			"brown-standard-armor-large-tiles-icon",
			"brown-advanced-armor-large-tiles",
			"brown-advanced-armor-large-tiles-icon",
	}
	 */
	private static final String[] iconNames = {"holo-projector-icon", "text-projector-icon", "holo-table-icon", "large-dark-tiles-icon", "large-dark-tiles-wedge-icon", "small-dark-tiles-icon", "small-dark-tiles-wedge-icon", "large-light-tiles-icon", "large-light-tiles-wedge-icon", "small-light-tiles-icon", "small-light-tiles-wedge-icon"};
	private static final String[] spriteNames = {"projectors-infographic", "transparent", "projector-debug-grid"};
	private static final String[] modelNames = {"holo_table"};
	private static final String[] fontNames = {"Monda-Extended-Regular", "Monda-Extended-Bold"};
	private static final HashMap<String, StarLoaderTexture> textureMap = new HashMap<>();
	private static final HashMap<String, StarLoaderTexture> iconMap = new HashMap<>();
	private static final HashMap<String, Sprite> spriteMap = new HashMap<>();
	private static final HashMap<String, Mesh> meshMap = new HashMap<>();
	private static final HashMap<String, Font> fontMap = new HashMap<>();

	/**
	 * Generates the texture variants for hull blocks.
	 */
	public static void main(String[] args) {
		if(args.length != 2) throw new IllegalArgumentException("Args requires both the output directory and the base texture file.");
		File outputDir = new File(args[0]);
		if(!outputDir.exists()) outputDir.mkdirs();
		File baseTexture = new File(args[1]);
		if(!baseTexture.exists()) throw new IllegalArgumentException("Base texture file does not exist.");
		int lighterColor = 32;
		int darkerColor = 64;
		try {
			BufferedImage baseImage = ImageIO.read(baseTexture);
			//Generate a variant of the image with a grid overlay. For the "large tiles" variant, the grid separates the texture into 2x2 squares. For the "small tiles" variant, the grid separates the texture into 4x4 squares.
			//For each horizontal line, we make the line 2 pixels wide, with the left half of the line being lighter than the right. For each horizontal line, we make the line 2 pixels wide, with the top half of the line being lighter than the bottom.
			//Assume the base image is 256x256.
			{ //Generate the "large tiles" variant first.
				BufferedImage largeTilesImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D largeTilesGraphics = largeTilesImage.createGraphics();
				largeTilesGraphics.drawImage(baseImage, 0, 0, null);
				//Make the lighter lines.
				largeTilesGraphics.setColor(new Color(0, 0, 0, lighterColor));
				for(int y = 0; y < largeTilesImage.getHeight(); y += 128) largeTilesGraphics.drawLine(0, y, largeTilesImage.getWidth(), y);
				for(int x = 0; x < largeTilesImage.getWidth(); x += 128) largeTilesGraphics.drawLine(x, 0, x, largeTilesImage.getHeight());
				//Make the darker lines.
				largeTilesGraphics.setColor(new Color(0, 0, 0, darkerColor));
				for(int y = 1; y < largeTilesImage.getHeight(); y += 128) largeTilesGraphics.drawLine(0, y, largeTilesImage.getWidth(), y);
				for(int x = 1; x < largeTilesImage.getWidth(); x += 128) largeTilesGraphics.drawLine(x, 0, x, largeTilesImage.getHeight());
				//Save the image.
				ImageIO.write(largeTilesImage, "png", new File(outputDir, baseTexture.getName().substring(0, baseTexture.getName().indexOf(".png")) + "-large-tiles.png"));
			}
			{ //Generate the "small tiles" variant next.
				BufferedImage smallTilesImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D smallTilesGraphics = smallTilesImage.createGraphics();
				smallTilesGraphics.drawImage(baseImage, 0, 0, null);
				//Make the lighter lines.
				smallTilesGraphics.setColor(new Color(0, 0, 0, lighterColor));
				for(int y = 0; y < smallTilesImage.getHeight(); y += 64) smallTilesGraphics.drawLine(0, y, smallTilesImage.getWidth(), y);
				for(int x = 0; x < smallTilesImage.getWidth(); x += 64) smallTilesGraphics.drawLine(x, 0, x, smallTilesImage.getHeight());
				//Make the darker lines.
				smallTilesGraphics.setColor(new Color(0, 0, 0, darkerColor));
				for(int y = 1; y < smallTilesImage.getHeight(); y += 64) smallTilesGraphics.drawLine(0, y, smallTilesImage.getWidth(), y);
				for(int x = 1; x < smallTilesImage.getWidth(); x += 64) smallTilesGraphics.drawLine(x, 0, x, smallTilesImage.getHeight());
				//Save the image.
				ImageIO.write(smallTilesImage, "png", new File(outputDir, baseTexture.getName().substring(0, baseTexture.getName().indexOf(".png")) + "-small-tiles.png"));
			}
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void loadResources(final DerpsDecor instance, final ResourceLoader loader) {
		// Load fonts
		for(String fontName : fontNames) {
			try {
				fontMap.put(fontName, Font.createFont(Font.TRUETYPE_FONT, DerpsDecor.getInstance().getJarResource("fonts/" + fontName + ".ttf")));
			} catch(Exception exception) {
				DerpsDecor.getInstance().logException("Failed to load font \"" + fontName + "\"", exception);
			}
		}
		StarLoaderTexture.runOnGraphicsThread(new Runnable() {
			@Override
			public void run() {
				//Load Textures
				for(String textureName : textureNames) {
					try {
						textureMap.put(textureName, StarLoaderTexture.newBlockTexture(ImageIO.read(DerpsDecor.getInstance().getJarResource("textures/" + textureName + ".png")), ImageIO.read(DerpsDecor.getInstance().getJarResource("textures/" + textureName + "-normal.png"))));
					} catch(Exception exception) {
						instance.logException("Failed to load texture \"" + textureName + "\"", exception);
					}
				}

				//Load Icons
				for(String iconName : iconNames) {
					try {
						iconMap.put(iconName, StarLoaderTexture.newIconTexture(ImageIO.read(DerpsDecor.getInstance().getJarResource("icons/" + iconName + ".png"))));
					} catch(Exception exception) {
						instance.logException("Failed to load icon \"" + iconName + "\"", exception);
					}
				}

				//Load Sprites
				for(String spriteName : spriteNames) {
					try {
						Sprite sprite = StarLoaderTexture.newSprite(ImageIO.read(DerpsDecor.getInstance().getJarResource("sprites/" + spriteName + ".png")), instance, spriteName);
						sprite.setPositionCenter(false);
						sprite.setName(spriteName);
						spriteMap.put(spriteName, sprite);
					} catch(Exception exception) {
						instance.logException("Failed to load sprite \"" + spriteName + "\"", exception);
					}
				}
				//Load models
				for(String modelName : modelNames) {
					try {
						loader.getMeshLoader().loadModMesh(instance, modelName, DerpsDecor.getInstance().getJarResource("models/" + modelName + ".zip"), null);
						Mesh mesh = loader.getMeshLoader().getModMesh(DerpsDecor.getInstance(), modelName);
						mesh.setFirstDraw(true);
						meshMap.put(modelName, mesh);
					} catch(ResourceException | IOException exception) {
						instance.logException("Failed to load model \"" + modelName + "\"", exception);
					}
				}
			}
		});
	}

	public static StarLoaderTexture getTexture(String name) {
		return textureMap.get(name);
	}

	public static StarLoaderTexture getIcon(String name) {
		return iconMap.get(name);
	}

	public static Sprite getSprite(String name) {
		return spriteMap.get(name);
	}

	public static Mesh getMesh(String name) {
		if(meshMap.containsKey(name)) return (Mesh) meshMap.get(name).getChilds().get(0);
		else return null;
	}

	public static UnicodeFont getFont(String fontName, int size, Color color, Color outlineColor, int outlineSize) {
		try {
			Font font = fontMap.get(fontName).deriveFont((float) size);
			UnicodeFont unicodeFont = new UnicodeFont(font);
			unicodeFont.getEffects().add(new OutlineEffect(outlineSize, outlineColor));
			unicodeFont.getEffects().add(new ColorEffect(color));
			unicodeFont.addGlyphs(0x4E00, 0x9FBF);
			unicodeFont.addAsciiGlyphs();
			unicodeFont.loadGlyphs();
			return unicodeFont;
		} catch(Exception ignored) {
		}
		return null;
	}

	public static UnicodeFont getFont(String fontName, int size, Color color) {
		try {
			Font font = fontMap.get(fontName).deriveFont((float) size);
			UnicodeFont unicodeFont = new UnicodeFont(font);
			unicodeFont.getEffects().add(new ColorEffect(color));
			unicodeFont.addGlyphs(0x4E00, 0x9FBF);
			unicodeFont.addAsciiGlyphs();
			unicodeFont.loadGlyphs();
			return unicodeFont;
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}
}
