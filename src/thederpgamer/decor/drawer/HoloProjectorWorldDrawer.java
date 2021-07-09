package thederpgamer.decor.drawer;

import api.utils.draw.ModWorldDrawer;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.DrawableScene;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.data.ProjectorDrawData;
import thederpgamer.decor.data.image.ScalableImageSubSprite;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.utils.DataUtils;
import java.util.Map;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjectorWorldDrawer extends ModWorldDrawer implements Shaderable {

    private float t;

    @Override
    public void onInit() {

    }

    @Override
    public void update(Timer timer) {
        t += timer.getDelta() * 2f;
        for(Map.Entry<SegmentPiece, ProjectorDrawData> entry : DataUtils.projectorDrawMap.entrySet()) {
            if(entry.getKey() != null && entry.getKey().getSegmentController().isFullyLoaded()) {
                Transform newTransform = new Transform();
                entry.getKey().getTransform(newTransform);
                newTransform.origin.x = newTransform.origin.x + entry.getValue().xOffset;
                newTransform.origin.y = newTransform.origin.y + entry.getValue().yOffset;
                newTransform.origin.z = newTransform.origin.z + entry.getValue().zOffset;
                if(entry.getValue().src != null && !entry.getValue().src.isEmpty()) {
                    Sprite image = ImageManager.getImage(entry.getValue().src);
                    if(image != null) {
                        ShaderLibrary.scanlineShader.setShaderInterface(this);
                        ShaderLibrary.scanlineShader.load();
                        ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[] {new ScalableImageSubSprite(entry.getValue().scale, newTransform)};
                        Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                        ShaderLibrary.scanlineShader.unload();
                    }
                }
            }
        }
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void onExit() {

    }

    @Override
    public void updateShader(DrawableScene drawableScene) {

    }

    @Override
    public void updateShaderParameters(Shader shader) {
        GlUtil.updateShaderFloat(shader, "uTime", t);
        GlUtil.updateShaderVector2f(shader, "uResolution", 20, 1000);
        GlUtil.updateShaderInt(shader, "uDiffuseTexture", 0);
    }
}
