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
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/15/2021
 */
public class ResourceManager {

	private static final String[] textureNames = {
			"holo-projector-front",
			"holo-projector-icon",
			"text-projector-front",
			"text-projector-icon",
			"holo-table-icon",

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
	};

	private static final String[] spriteNames = {
			"transparent", "projector-debug-grid", "projectors-infographic"
	};

	private static final String[] modelNames = {
			// "strut_connector",
			// "strut_tube",
			// "display_screen",
			"holo_table"
			// "storage_capsule_closed",
			// "storage_capsule_open",
			// "activation_lever_off",
			// "activation_lever_on"
	};

	private static final String[] fontNames = {"Monda-Extended-Regular", "Monda-Extended-Bold"};

	private static final HashMap<String, StarLoaderTexture> textureMap = new HashMap<>();
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
		try {
			BufferedImage baseImage = ImageIO.read(baseTexture);
			/*
			//Generate color variants
			for(HullBlock.Color color : HullBlock.Color.values()) {
				String outputName = (baseTexture.getName().substring(0, baseTexture.getName().lastIndexOf(".")) + "-" + color.name).toLowerCase(Locale.ENGLISH).replaceAll(" ", "-") + ".png";
				File outputFile = new File(outputDir, outputName);
				BufferedImage outputImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = outputImage.createGraphics();
				g.drawImage(baseImage, 0, 0, null);
				g.setColor(color.getColor());
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
				g.fillRect(0, 0, baseImage.getWidth(), baseImage.getHeight());
				g.dispose();
				ImageIO.write(outputImage, "png", outputFile);
			}

			 */
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void loadResources(final DerpsDecor instance, final ResourceLoader loader) {
		// Load fonts
		for(String fontName : fontNames) {
			try {
				fontMap.put(
						fontName,
						Font.createFont(
								Font.TRUETYPE_FONT,
								instance.getJarResource(
										"fonts/" + fontName + ".ttf")));
			} catch(Exception exception) {
				LogManager.logException("Failed to load font \"" + fontName + "\"", exception);
			}
		}

		StarLoaderTexture.runOnGraphicsThread(
				new Runnable() {
					@Override
					public void run() {
						// Load Textures
						for(String textureName : textureNames) {
							try {
								if(textureName.endsWith("icon")) {
									textureMap.put(
											textureName,
											StarLoaderTexture.newIconTexture(ImageIO.read(DerpsDecor.class.getResourceAsStream("/textures/" + textureName + ".png"))));
								} else {
									textureMap.put(
											textureName,
											StarLoaderTexture.newBlockTexture(ImageIO.read(DerpsDecor.class.getResourceAsStream("/textures/" + textureName + ".png"))));
								}
							} catch(Exception exception) {
								LogManager.logException(
										"Failed to load texture \"" + textureName + "\"", exception);
							}
						}

						// Load Sprites
						for(String spriteName : spriteNames) {
							try {
								Sprite sprite = StarLoaderTexture.newSprite(ImageIO.read(DerpsDecor.class.getResourceAsStream("/sprites/" + spriteName + ".png")), instance, spriteName);
								sprite.setPositionCenter(false);
								sprite.setName(spriteName);
								spriteMap.put(spriteName, sprite);
							} catch(Exception exception) {
								LogManager.logException("Failed to load sprite \"" + spriteName + "\"", exception);
							}
						}

						// Load models
						for(String modelName : modelNames) {
							try {
								Vector3f offset = new Vector3f();
								if(modelName.contains("~")) {
									String meshName = modelName.substring(0, modelName.indexOf('~'));
									String offsetString =
											modelName.substring(modelName.indexOf('(') + 1, modelName.lastIndexOf(')'));
									String[] values = offsetString.split(", ");
									assert values.length == 3;
									offset.x = Float.parseFloat(values[0]);
									offset.y = Float.parseFloat(values[1]);
									offset.z = Float.parseFloat(values[2]);
									loader.getMeshLoader().loadModMesh(instance, meshName, DerpsDecor.class.getResourceAsStream("/models/" + meshName + ".zip"), null);
									Mesh mesh = loader.getMeshLoader().getModMesh(DerpsDecor.getInstance(), meshName);
									mesh.getTransform().origin.add(offset);
									meshMap.put(meshName, mesh);
								} else {
									loader.getMeshLoader().loadModMesh(instance, modelName, DerpsDecor.class.getResourceAsStream("models/" + modelName + ".zip"), null);
									Mesh mesh = loader.getMeshLoader().getModMesh(DerpsDecor.getInstance(), modelName);
									mesh.setFirstDraw(true);
									if(modelName.equals("display_screen")) { // Temp fix
										mesh.rotateBy(0.0f, 180.0f, 0.0f);
										mesh.getPos().add(new Vector3f(0.0f, 0.0f, 0.5f));
									}
									meshMap.put(modelName, mesh);
								}
							} catch(ResourceException | IOException exception) {
								LogManager.logException("Failed to load model \"" + modelName + "\"", exception);
							}
						}
					}
				});
	}

	public static StarLoaderTexture getTexture(String name) {
		return textureMap.get(name);
	}

	public static Sprite getSprite(String name) {
		return spriteMap.get(name);
	}

	public static Mesh getMesh(String name) {
		if(meshMap.containsKey(name)) return (Mesh) meshMap.get(name).getChilds().get(0);
		else return null;
	}

	public static UnicodeFont getFont(
			String fontName, int size, Color color, Color outlineColor, int outlineSize) {
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
