package thederpgamer.decor.drawer;

import api.utils.draw.ModWorldDrawer;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.data.graphics.image.ScalableImageSubSprite;
import thederpgamer.decor.data.projector.HoloProjectorDrawData;
import thederpgamer.decor.data.projector.ProjectorDrawData;
import thederpgamer.decor.data.projector.TextProjectorDrawData;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Mod world drawer for projector blocks.
 *
 * @author TheDerpGamer
 * @since 07/18/2021
 */
public class ProjectorDrawer extends ModWorldDrawer implements Drawable, Shaderable {

    private float time;
    private final ConcurrentLinkedQueue<ProjectorDrawData> drawQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void onInit() {

    }

    @Override
    public void cleanUp() {

    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void update(Timer timer) {
        time += timer.getDelta() * 2f;
    }

    @Override
    public void updateShader(DrawableScene drawableScene) {

    }

    @Override
    public void updateShaderParameters(Shader shader) {
        GlUtil.updateShaderFloat(shader, "uTime", time);
        GlUtil.updateShaderVector2f(shader, "uResolution", 20, 1000);
        GlUtil.updateShaderInt(shader, "uDiffuseTexture", 0);
    }

    @Override
    public void onExit() {

    }

    @Override
    public void draw() {
        if(!drawQueue.isEmpty()) {
            ShaderLibrary.scanlineShader.setShaderInterface(this);
            ShaderLibrary.scanlineShader.load();
            while(!drawQueue.isEmpty()) {
                ProjectorDrawData drawData = drawQueue.poll();
                if(drawData instanceof HoloProjectorDrawData) {
                    HoloProjectorDrawData holoProjectorDrawData = (HoloProjectorDrawData) drawData;
                    Sprite image = holoProjectorDrawData.image;
                    if(image != null) {
                        float maxDim = Math.max(image.getWidth(), image.getHeight());
                        ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[]{new ScalableImageSubSprite(((float) drawData.scale / maxDim) * -1, drawData.transform)};
                        image.setTransform(drawData.transform);
                        Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                    }
                } else if(drawData instanceof TextProjectorDrawData) {
                    TextProjectorDrawData textProjectorDrawData = (TextProjectorDrawData) drawData;
                    textProjectorDrawData.textOverlay.setTransform(drawData.transform);
                    textProjectorDrawData.textOverlay.draw();
                }
            }
            ShaderLibrary.scanlineShader.unload();
        }
    }

    public void queueDraw(ProjectorDrawData drawData) {
        drawQueue.add(drawData);
    }
}
