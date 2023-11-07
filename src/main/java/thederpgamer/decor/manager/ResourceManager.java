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
	private static final String[] textureNames = {"holo-projector-front", "text-projector-front", "npc-station-front"};
	private static final String[] iconNames = {"holo-projector-icon", "text-projector-icon", "npc-station-icon"};
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
