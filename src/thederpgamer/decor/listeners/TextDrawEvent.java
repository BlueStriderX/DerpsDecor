package thederpgamer.decor.listeners;

import api.common.GameClient;
import api.listener.fastevents.TextBoxDrawListener;
import com.bulletphysics.linearmath.Transform;
import org.apache.commons.lang3.StringUtils;
import org.schema.game.client.view.SegmentDrawer;
import org.schema.game.client.view.textbox.AbstractTextBox;
import org.schema.game.common.data.element.Element;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.data.image.ScalableImageSubSprite;
import thederpgamer.decor.manager.ConfigManager;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.utils.MessageType;
import java.lang.reflect.Field;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/15/2021
 */
public class TextDrawEvent implements TextBoxDrawListener {

    @Override
    public void preDraw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox abstractTextBox) {
        try {
            Field maxDrawField = abstractTextBox.getClass().getDeclaredField("maxTextDistance");
            maxDrawField.setAccessible(true);
            maxDrawField.set(abstractTextBox, ConfigManager.getMainConfig().getInt("max-display-draw-distance"));
        } catch(NoSuchFieldException | IllegalAccessException exception) {
            LogManager.logException("Encountered an exception in TextDrawEvent preDraw() method", exception);
        }
    }

    @Override
    public void draw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox box) {
        box.cleanUp();
    }

    @Override
    public void preDrawBackground(SegmentDrawer.TextBoxSeg seg, AbstractTextBox abstractTextBox) {
        for(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement : seg.v) {
            try {
                float xOffset = 0.0f;
                float yOffset = 0.0f;
                float zOffset = 0.0f;
                float scale = 1.0f;

                byte orientation = textBoxElement.c.getSegmentBuffer().getPointUnsave(textBoxElement.v).getOrientation();
                switch (orientation) {
                    case Element.FRONT:
                        zOffset += 0.5f;
                        yOffset -= 0.5f;
                        break;
                }


                if (textBoxElement.rawText.contains("<img>")) {
                    String str = StringUtils.substringBetween(textBoxElement.rawText, "<img>", "</img>");
                    String[] args = str.split(",");
                    String src = null;
                    boolean hideText = true;

                    try {
                        for (String arg : args) {
                            if (arg.startsWith("src=")) {
                                src = arg.split("src=")[1];
                            } else if (arg.startsWith("scale=")) {
                                scale = (float) Math.min(ConfigManager.getMainConfig().getDouble("max-image-scale"), Float.parseFloat(arg.split("scale=")[1]));
                            } else if (arg.startsWith("hide_text=")) {
                                hideText = Boolean.parseBoolean(arg.split("hide_text=")[1]);
                            } else if (arg.startsWith("x=")) {
                                xOffset = (float) Math.min(ConfigManager.getMainConfig().getDouble("max-image-offset"), Float.parseFloat(arg.split("x=")[1]));
                            } else if (arg.startsWith("y=")) {
                                yOffset = (float) Math.min(ConfigManager.getMainConfig().getDouble("max-image-offset"), Float.parseFloat(arg.split("y=")[1]));
                            } else if (arg.startsWith("z=")) {
                                zOffset = (float) Math.min(ConfigManager.getMainConfig().getDouble("max-image-offset"), Float.parseFloat(arg.split("z=")[1]));
                            }
                        }
                        //LogManager.logMessage(MessageType.INFO, "An image link was entered into a display module on entity \"" + textBoxElement.c.getName() + "\" by player " + GameClient.getClientPlayerState().getName() + ":\n\"" + str + "\"");
                        //Todo: Above line logs the message every frame the display module is drawn. Needs to be done in a way so that it only logs the message when the text is modified, not every time it's drawn.
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (scale <= 0) scale = 1.0f;
                    if (hideText) textBoxElement.text.setTextSimple("");
                    boolean blockImage = false;

                    if (src != null) {
                        if (ConfigManager.getMainConfig().getString("image-filter-mode").equalsIgnoreCase("blacklist")) {
                            for (String s : ConfigManager.getMainConfig().getList("image-filter")) {
                                if (src.toLowerCase().contains(s.toLowerCase())) {
                                    blockImage = true;
                                    break;
                                }
                            }
                        } else if (ConfigManager.getMainConfig().getString("image-filter-mode").equalsIgnoreCase("whitelist")) {
                            blockImage = true;
                            for (String s : ConfigManager.getMainConfig().getList("image-filter")) {
                                if (src.toLowerCase().contains(s.toLowerCase())) {
                                    blockImage = false;
                                    break;
                                }
                            }
                        }

                        if (!blockImage) {
                            Sprite image = ImageManager.getImage(src);
                            Transform newTransform = new Transform(textBoxElement.worldpos);
                            newTransform.origin.x = newTransform.origin.x + xOffset;
                            newTransform.origin.y = newTransform.origin.y + yOffset;
                            newTransform.origin.z = newTransform.origin.z + zOffset;
                            if(image != null) {
                                scale = (scale / image.getWidth()) * -1;
                                image.setScale(scale, scale, scale);
                                ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[]{new ScalableImageSubSprite(scale, newTransform)};
                                Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                            }
                        } else LogManager.logMessage(MessageType.WARNING, "An inappropriate image displayed by player " + GameClient.getClientPlayerState().getName() + " was blocked by the server filter:\n\"" + str + "\"");
                    }
                }
            } catch (Exception ignored) { }
        }
    }
}