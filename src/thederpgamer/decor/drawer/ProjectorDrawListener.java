package thederpgamer.decor.drawer;

import api.common.GameClient;
import api.listener.fastevents.TextBoxDrawListener;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.client.view.SegmentDrawer;
import org.schema.game.client.view.textbox.AbstractTextBox;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.data.image.ScalableImageSubSprite;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.manager.ResourceManager;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/14/2021
 */
public class ProjectorDrawListener implements TextBoxDrawListener {

    private final HashMap<Long, GUITextOverlay> textDrawMap = new HashMap<>();
    private float timer;

    @Override
    public void draw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox abstractTextBox) {

    }

    @Override
    public void preDrawBackground(final SegmentDrawer.TextBoxSeg textBoxSeg, final AbstractTextBox abstractTextBox) {
        for (SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement : textBoxSeg.v) {
            SegmentPiece segmentPiece = textBoxElement.c.getSegmentBuffer().getPointUnsave(textBoxElement.v);
            if (segmentPiece != null && !segmentPiece.isActive()) {
                try {
                    if (segmentPiece.getType() == ElementKeyMap.TEXT_BOX || segmentPiece.getType() == ElementManager.getBlock("Display Screen").getId()) {
                        abstractTextBox.getBg().setSprite(Controller.getResLoader().getSprite("screen-gui-"));
                        if (textBoxElement.rawText.contains("~") && (textBoxElement.rawText.split("~").length == 8 || textBoxElement.rawText.split("~").length == 9)) textBoxElement.rawText = "";
                    } else {
                        if (textBoxElement.rawText.contains("~") && (textBoxElement.rawText.split("~").length == 8 || textBoxElement.rawText.split("~").length == 9)) {
                            if (segmentPiece.getType() == ElementManager.getBlock("Holo Projector").getId()) {
                                abstractTextBox.getBg().setSprite(ResourceManager.getSprite("transparent"));
                                String[] values = textBoxElement.rawText.split("~");
                                int xOffset = Integer.parseInt(values[0]);
                                int yOffset = Integer.parseInt(values[1]);
                                int zOffset = Integer.parseInt(values[2]);
                                int xRot = Integer.parseInt(values[3]);
                                int yRot = Integer.parseInt(values[4]);
                                int zRot = Integer.parseInt(values[5]);
                                int scale = Integer.parseInt(values[6]);
                                String src = values[7];
                                Vector3f offset = new Vector3f(xOffset, yOffset, zOffset);

                                if (src.toLowerCase().endsWith(".png") || src.toLowerCase().endsWith(".jpg")) {
                                    Sprite image = ImageManager.getImage(src);
                                    if (image != null) {
                                        Transform pos = textBoxElement.worldpos;
                                        pos.origin.add(offset);

                                        Quat4f currentRot = new Quat4f();
                                        pos.getRotation(currentRot);
                                        Quat4f addRot = new Quat4f();
                                        QuaternionUtil.setEuler(addRot, xRot / 100.0f, yRot / 100.0f, zRot / 100.0f);
                                        currentRot.mul(addRot);
                                        pos.setRotation(currentRot);

                                        float maxDim = Math.max(image.getWidth(), image.getHeight());
                                        ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[]{new ScalableImageSubSprite(((float) scale / maxDim) * -1, pos)};
                                        Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                                    }
                                }
                                textBoxElement.text.setTextSimple("");
                            } else if (segmentPiece.getType() == ElementManager.getBlock("Text Projector").getId()) {
                                abstractTextBox.getBg().setSprite(ResourceManager.getSprite("transparent"));
                                String[] values = textBoxElement.rawText.split("~");
                                int xOffset = Integer.parseInt(values[0]);
                                int yOffset = Integer.parseInt(values[1]);
                                int zOffset = Integer.parseInt(values[2]);
                                int xRot = Integer.parseInt(values[3]);
                                int yRot = Integer.parseInt(values[4]);
                                int zRot = Integer.parseInt(values[5]);
                                int scale = Integer.parseInt(values[6]);
                                String colorCode = values[7];
                                String text = values[8];

                                Vector3f offset = new Vector3f(xOffset, yOffset, zOffset);
                                Transform pos = textBoxElement.worldpos;
                                pos.origin.add(offset);

                                Quat4f currentRot = new Quat4f();
                                pos.getRotation(currentRot);
                                Quat4f addRot = new Quat4f();
                                QuaternionUtil.setEuler(addRot, xRot / 100.0f, yRot / 100.0f, zRot / 100.0f);
                                currentRot.mul(addRot);
                                pos.setRotation(currentRot);
                                if (!textDrawMap.containsKey(segmentPiece.getAbsoluteIndex()) || !textDrawMap.get(segmentPiece.getAbsoluteIndex()).getTransform().equals(pos)) {
                                    GUITextOverlay textOverlay = new GUITextOverlay(30, 10, GameClient.getClientState());
                                    textOverlay.onInit();
                                    textOverlay.setFont(ResourceManager.getFont("Monda-Bold", scale, Color.decode("0x" + colorCode)));
                                    textOverlay.setTextSimple(text);
                                    textOverlay.setTransform(pos);
                                    textOverlay.setScale(-scale / 10.0f, -scale / 10.0f, -scale / 10.0f);
                                    textDrawMap.remove(segmentPiece.getAbsoluteIndex());
                                    textDrawMap.put(segmentPiece.getAbsoluteIndex(), textOverlay);
                                } else textDrawMap.get(segmentPiece.getAbsoluteIndex()).draw();
                                textBoxElement.text.setTextSimple("");
                            }
                        }
                    }
                } catch (Exception ignored) { }
            }
        }

        if(timer == 0) {
           textDrawMap.clear();
            timer = 5000f;
        } else timer --;
    }

    @Override
    public void preDraw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox abstractTextBox) {

    }
}
