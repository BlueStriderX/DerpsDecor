package thederpgamer.decor.manager;

import api.utils.textures.StarLoaderTexture;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.decor.DerpsDecor;
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
            "holo-projector-icon"
    };

    private static final String[] spriteNames = {
            "transparent"
    };

    private static final String[] modelNames = {
            "display_screen"
    };

    private static HashMap<String, StarLoaderTexture> textureMap = new HashMap<>();
    private static HashMap<String, Sprite> spriteMap = new HashMap<>();

    public static void loadResources(final DerpsDecor instance, final ResourceLoader loader) {

        StarLoaderTexture.runOnGraphicsThread(new Runnable() {
            @Override
            public void run() {
                //Load Textures
                for(String textureName : textureNames) {
                    try {
                        if(textureName.endsWith("icon")) {
                            textureMap.put(textureName, StarLoaderTexture.newIconTexture(instance.getJarBufferedImage("thederpgamer/decor/resources/textures/" + textureName + ".png")));
                        } else {
                            textureMap.put(textureName, StarLoaderTexture.newBlockTexture(instance.getJarBufferedImage("thederpgamer/decor/resources/textures/" + textureName + ".png")));
                        }
                    } catch(Exception exception) {
                        LogManager.logException("Failed to load texture \"" + textureName + "\"", exception);
                    }
                }

                //Load Sprites
                for(String spriteName : spriteNames) {
                    try {
                        Sprite sprite = StarLoaderTexture.newSprite(instance.getJarBufferedImage("thederpgamer/decor/resources/sprites/" + spriteName + ".png"), instance, spriteName);
                        sprite.setPositionCenter(true);
                        sprite.setName(spriteName);
                        spriteMap.put(spriteName, sprite);
                    } catch(Exception exception) {
                        LogManager.logException("Failed to load sprite \"" + spriteName + "\"", exception);
                    }
                }

                //Load models
                for(String modelName : modelNames) {
                    try {
                        loader.getMeshLoader().loadModMesh(instance, modelName, instance.getJarResource("thederpgamer/decor/resources/models/" + modelName + ".zip"), null);
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
}
