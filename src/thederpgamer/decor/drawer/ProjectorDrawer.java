package thederpgamer.decor.drawer;

import api.utils.draw.ModWorldDrawer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.data.graphics.image.ScalableImageSubSprite;
import thederpgamer.decor.data.projector.HoloProjectorDrawData;
import thederpgamer.decor.data.projector.ProjectorDrawData;
import thederpgamer.decor.data.projector.TextProjectorDrawData;
import thederpgamer.decor.manager.ResourceManager;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mod world drawer for projector blocks.
 *
 * @author TheDerpGamer
 * @since 07/18/2021
 */
public class ProjectorDrawer extends ModWorldDrawer implements Drawable, Shaderable {

    private float time;
    private final ConcurrentHashMap<SegmentPiece, ProjectorDrawData> drawMap = new ConcurrentHashMap<>();

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
        if(!drawMap.isEmpty()) {
            for(Map.Entry<SegmentPiece, ProjectorDrawData> entry : drawMap.entrySet()) {
                SegmentPiece segmentPiece = entry.getKey();
                if(!segmentPiece.getSegmentController().getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex()) || segmentPiece.getSegmentController().getSegmentBuffer().getPointUnsave(segmentPiece.getAbsoluteIndex()).getType() != segmentPiece.getType() || segmentPiece.isActive() || !segmentPiece.getSegmentController().isFullyLoaded()) drawMap.remove(segmentPiece);
                else {
                    ProjectorDrawData drawData = entry.getValue();
                    if(drawData.transform != null) {
                        if(drawData instanceof HoloProjectorDrawData) {
                            HoloProjectorDrawData holoProjectorDrawData = (HoloProjectorDrawData) drawData;
                            Sprite image = holoProjectorDrawData.image;
                            if(image != null) {
                                ShaderLibrary.scanlineShader.setShaderInterface(this);
                                ShaderLibrary.scanlineShader.load();
                                float maxDim = Math.max(image.getWidth(), image.getHeight());
                                ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[]{new ScalableImageSubSprite(((float) drawData.scale / maxDim) * -1, drawData.transform)};
                                image.setTransform(drawData.transform);
                                Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                                ShaderLibrary.scanlineShader.unload();
                            }
                        } else if(drawData instanceof TextProjectorDrawData) {
                            TextProjectorDrawData textProjectorDrawData = (TextProjectorDrawData) drawData;
                            if(textProjectorDrawData.textOverlay != null) {
                                ShaderLibrary.scanlineShader.setShaderInterface(this);
                                ShaderLibrary.scanlineShader.load();
                                if(textProjectorDrawData.textOverlay.getFont() == null) textProjectorDrawData.textOverlay.setFont(ResourceManager.getFont("Monda-Bold", drawData.scale + 10, Color.decode("0x" + textProjectorDrawData.color)));
                                textProjectorDrawData.textOverlay.setTransform(drawData.transform);
                                textProjectorDrawData.textOverlay.draw();
                                ShaderLibrary.scanlineShader.unload();
                            }
                        }
                    }
                }
            }
        }
    }

    public void addDraw(SegmentPiece segmentPiece, ProjectorDrawData drawData) {
        drawMap.remove(segmentPiece);
        drawMap.put(segmentPiece, drawData);
    }
}
