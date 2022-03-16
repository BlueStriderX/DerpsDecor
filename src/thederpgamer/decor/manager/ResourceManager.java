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

import javax.vecmath.Vector3f;
import java.awt.*;
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
			//"holo-projector-icon",
			"text-projector-front",
			//"text-projector-icon",
			//"strut-connector-icon",
			//"display-screen-icon",
			//"holo-table-icon",
			//"cake-icon"
	};

	private static final String[] spriteNames = {
			"transparent",
			"projector-debug-grid",
			"projectors-infographic"
	};

	private static final String[] modelNames = {
			"strut_connector",
			"strut_tube",
			"display_screen",
			"holo_table",
			"storage_capsule_closed",
			"storage_capsule_open",
			"activation_lever_off",
			"activation_lever_on"
	};

	private static final String[] fontNames = {
			"Monda-Extended-Regular",
			"Monda-Extended-Bold"
	};

	private static final HashMap<String, StarLoaderTexture> textureMap = new HashMap<>();
	private static final HashMap<String, Sprite> spriteMap = new HashMap<>();
	private static final HashMap<String, Mesh> meshMap = new HashMap<>();
	private static final HashMap<String, Font> fontMap = new HashMap<>();

	public static void loadResources(final DerpsDecor instance, final ResourceLoader loader) {
		// Load fonts
		for(String fontName : fontNames) {
			try {
				fontMap.put(fontName, Font.createFont(Font.TRUETYPE_FONT, instance.getJarResource("thederpgamer/decor/resources/fonts/" + fontName + ".ttf")));
			} catch(Exception exception) {
				LogManager.logException("Failed to load font \"" + fontName + "\"", exception);
			}
		}

		StarLoaderTexture.runOnGraphicsThread(new Runnable() {
			@Override
			public void run() {
				// Load Textures
				for(String textureName : textureNames) {
					try {
						textureMap.put(textureName, StarLoaderTexture.newBlockTexture(instance.getJarBufferedImage("thederpgamer/decor/resources/textures/" + textureName + ".png")));
					} catch(Exception exception) {
						LogManager.logException("Failed to load texture \"" + textureName + "\"", exception);
					}
				}

				// Load Sprites
				for(String spriteName : spriteNames) {
					try {
						Sprite sprite = StarLoaderTexture.newSprite(instance.getJarBufferedImage("thederpgamer/decor/resources/sprites/" + spriteName + ".png"), instance, spriteName);
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
							String offsetString = modelName.substring(modelName.indexOf('(') + 1, modelName.lastIndexOf(')'));
							String[] values = offsetString.split(", ");
							assert values.length == 3;
							offset.x = Float.parseFloat(values[0]);
							offset.y = Float.parseFloat(values[1]);
							offset.z = Float.parseFloat(values[2]);
							loader.getMeshLoader().loadModMesh(instance, meshName, instance.getJarResource("thederpgamer/decor/resources/models/" + meshName + ".zip"), null);
							Mesh mesh = loader.getMeshLoader().getModMesh(DerpsDecor.getInstance(), meshName);
							mesh.getTransform().origin.add(offset);
							meshMap.put(meshName, mesh);
						} else {
							loader.getMeshLoader().loadModMesh(instance, modelName, instance.getJarResource("thederpgamer/decor/resources/models/" + modelName + ".zip"), null);
							Mesh mesh = loader.getMeshLoader().getModMesh(DerpsDecor.getInstance(), modelName);
							mesh.setFirstDraw(true);
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
