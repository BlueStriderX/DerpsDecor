package thederpgamer.decor.drawer;

import api.listener.fastevents.TextBoxDrawListener;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.client.view.SegmentDrawer;
import org.schema.game.client.view.textbox.AbstractTextBox;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.data.image.ScalableImageSubSprite;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.manager.ResourceManager;

import javax.vecmath.Vector3f;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/14/2021
 */
public class HoloProjectorDrawer implements TextBoxDrawListener {

    @Override
    public void draw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox abstractTextBox) {
        if(textBoxElement.rawText.contains("~") && textBoxElement.rawText.split("~").length == 5) abstractTextBox.getBg().setSprite(ResourceManager.getSprite("transparent"));
        else abstractTextBox.getBg().setSprite(Controller.getResLoader().getSprite("screen-gui-"));
    }

    @Override
    public void preDrawBackground(SegmentDrawer.TextBoxSeg textBoxSeg, AbstractTextBox abstractTextBox) {
        for(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement : textBoxSeg.v) {
            if(textBoxElement.rawText.contains("~") && textBoxElement.rawText.split("~").length == 5) {
                SegmentPiece segmentPiece = textBoxElement.c.getSegmentBuffer().getPointUnsave(textBoxElement.v);
                if(segmentPiece != null && !segmentPiece.isActive()) {
                    String[] values = textBoxElement.rawText.split("~");
                    int xOffset = Integer.parseInt(values[0]);
                    int yOffset = Integer.parseInt(values[1]);
                    int zOffset = Integer.parseInt(values[2]);
                    int scale = Integer.parseInt(values[3]);
                    String src = values[4];

                    Vector3f offset = new Vector3f(xOffset, yOffset, zOffset);
                    if(src.toLowerCase().endsWith(".png") || src.toLowerCase().endsWith(".jpg")) {
                        Sprite image = ImageManager.getImage(src);
                        if(image != null) {
                            Transform pos = textBoxElement.worldpos;
                            pos.origin.add(offset);
                            float maxDim = Math.max(image.getWidth(), image.getHeight());
                            ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[]{new ScalableImageSubSprite(((float) scale / maxDim) * -1, pos)};
                            Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                        }
                    }
                }
                textBoxElement.text.setTextSimple("");
            }
        }
    }

    @Override
    public void preDraw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox abstractTextBox) {

    }
}
