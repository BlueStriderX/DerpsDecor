package thederpgamer.decor.drawer;

import api.utils.draw.ModWorldDrawer;
import com.bulletphysics.linearmath.QuaternionUtil;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.data.graphics.image.ScalableImageSubSprite;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.MathUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
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

    private final ConcurrentHashMap<SegmentPiece, Object> drawMap = new ConcurrentHashMap<>();
    private float time;

    @Override
    public void cleanUp() {

    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void onInit() {

    }

    @Override
    public void update(Timer timer) {
        time += timer.getDelta() * 2f;
    }

    @Override
    public void draw() {
        if(!drawMap.isEmpty()) {
            ShaderLibrary.scanlineShader.setShaderInterface(this);
            ShaderLibrary.scanlineShader.load();
            for(Map.Entry<SegmentPiece, Object> entry : drawMap.entrySet()) {
                SegmentPiece segmentPiece = entry.getKey();
                if(!segmentPiece.getSegmentController().getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex()) || segmentPiece.getSegmentController().getSegmentBuffer().getPointUnsave(segmentPiece.getAbsoluteIndex()).getType() != segmentPiece.getType() || segmentPiece.isActive() || !segmentPiece.getSegmentController().isFullyLoaded()) drawMap.remove(segmentPiece);
                else {
                    if(entry.getValue() instanceof HoloProjectorDrawData) {
                        HoloProjectorDrawData drawData = (HoloProjectorDrawData) entry.getValue();
                        Sprite image = drawData.image;
                        if(image != null) {
                            float maxDim = Math.max(image.getWidth(), image.getHeight());
                            drawData.transform = SegmentPieceUtils.getFullPieceTransform(segmentPiece);
                            doOffsetAndRotation(drawData);
                            ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[] {new ScalableImageSubSprite(((float) drawData.scale / maxDim) * -1, drawData.transform)};
                            image.setTransform(drawData.transform);
                            Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                        }
                    } else if(entry.getValue() instanceof TextProjectorDrawData) {
                        TextProjectorDrawData drawData = (TextProjectorDrawData) entry.getValue();
                        if(drawData.textOverlay != null) {
                            if(drawData.textOverlay.getFont() == null) drawData.textOverlay.setFont(ResourceManager.getFont("Monda-Bold", drawData.scale + 10, Color.decode("0x" + drawData.color)));
                            drawData.transform = SegmentPieceUtils.getFullPieceTransform(segmentPiece);
                            doOffsetAndRotation(drawData);
                            drawData.textOverlay.setTransform(drawData.transform);
                            drawData.textOverlay.draw();
                        }
                    }
                }
            }
            ShaderLibrary.scanlineShader.unload();
        }
    }

    private void doOffsetAndRotation(HoloProjectorDrawData drawData) {
        Quat4f currentRot = new Quat4f();
        drawData.transform.getRotation(currentRot);
        Quat4f addRot = new Quat4f();
        QuaternionUtil.setEuler(addRot, drawData.rotation.x / 100.0f, drawData.rotation.y / 100.0f, drawData.rotation.z / 100.0f);
        currentRot.mul(addRot);
        MathUtils.roundQuat(currentRot);
        drawData.transform.setRotation(currentRot);
        drawData.transform.origin.add(new Vector3f(drawData.offset.toVector3f()));
        MathUtils.roundVector(drawData.transform.origin);
    }

    private void doOffsetAndRotation(TextProjectorDrawData drawData) {
        Quat4f currentRot = new Quat4f();
        drawData.transform.getRotation(currentRot);
        Quat4f addRot = new Quat4f();
        QuaternionUtil.setEuler(addRot, drawData.rotation.x / 100.0f, drawData.rotation.y / 100.0f, drawData.rotation.z / 100.0f);
        currentRot.mul(addRot);
        MathUtils.roundQuat(currentRot);
        drawData.transform.setRotation(currentRot);
        drawData.transform.origin.add(new Vector3f(drawData.offset.toVector3f()));
        MathUtils.roundVector(drawData.transform.origin);
    }

    @Override
    public void onExit() {

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

    public void addDraw(SegmentPiece segmentPiece, Object drawData) {
        drawMap.remove(segmentPiece);
        drawMap.put(segmentPiece, drawData);
    }
}
