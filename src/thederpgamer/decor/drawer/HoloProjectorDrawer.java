package thederpgamer.decor.drawer;

import api.listener.fastevents.TextBoxDrawListener;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.client.view.SegmentDrawer;
import org.schema.game.client.view.textbox.AbstractTextBox;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.data.image.ScalableImageSubSprite;
import thederpgamer.decor.manager.ImageManager;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/14/2021
 */
public class HoloProjectorDrawer implements TextBoxDrawListener {

    @Override
    public void draw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox abstractTextBox) {
        if(textBoxElement.rawText.contains("~") && textBoxElement.rawText.split("~").length == 5) {
            if(abstractTextBox.getBg() != null) abstractTextBox.getBg().getSprite().setTint(new Vector4f(0, 0, 0, 0));
        } else {
            if(abstractTextBox.getBg() != null) abstractTextBox.getBg().getSprite().setTint(new Vector4f(0, 0, 0, 1));
        }
    }

    @Override
    public void preDrawBackground(SegmentDrawer.TextBoxSeg textBoxSeg, AbstractTextBox abstractTextBox) {
        for(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement : textBoxSeg.v) {
            if(textBoxElement.rawText.contains("~") && textBoxElement.rawText.split("~").length == 5) {
                String[] values = textBoxElement.rawText.split("~");
                int xOffset = Integer.parseInt(values[0]);
                int yOffset = Integer.parseInt(values[1]);
                int zOffset = Integer.parseInt(values[2]);
                int scale = Integer.parseInt(values[3]);
                String src = values[4];

                Vector3f offset = new Vector3f(xOffset, yOffset, zOffset);
                offset.x -= 0.01f;
                offset.y -= 0.01f;
                offset.z -= 0.51f;
                Sprite image = ImageManager.getImage(src);
                Transform pos = textBoxElement.worldpos;
                pos.origin.add(offset);
                if(image != null) {
                    ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[]{new ScalableImageSubSprite(((float) scale / image.getWidth()) * -1, pos)};
                    Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                }
                textBoxElement.text.setTextSimple("");
            }
        }
    }

    @Override
    public void preDraw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox abstractTextBox) {

    }
}
