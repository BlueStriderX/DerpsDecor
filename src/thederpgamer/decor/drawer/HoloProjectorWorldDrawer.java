package thederpgamer.decor.drawer;

import api.common.GameClient;
import api.common.GameCommon;
import api.utils.draw.ModWorldDrawer;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.FastMath;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.Element;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.data.ProjectorDrawData;
import thederpgamer.decor.data.image.ScalableImageSubSprite;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.utils.DataUtils;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import java.util.Map;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjectorWorldDrawer extends ModWorldDrawer implements Drawable, Shaderable {

    private float t;
    private Matrix3f mY = new Matrix3f();
    private Matrix3f mYB = new Matrix3f();
    private Matrix3f mYC = new Matrix3f();
    private Matrix3f mX = new Matrix3f();
    private Matrix3f mXB = new Matrix3f();
    private Matrix3f mXC = new Matrix3f();

    @Override
    public void onInit() {
        mY.setIdentity();
        mY.rotY(FastMath.HALF_PI);
        mYB.setIdentity();
        mYB.rotY(-FastMath.HALF_PI);
        mYC.setIdentity();
        mYC.rotY(FastMath.PI);

        mX.setIdentity();
        mX.rotX(FastMath.HALF_PI);
        mXB.setIdentity();
        mXB.rotX(-FastMath.HALF_PI);
        mXC.setIdentity();
        mXC.rotX(FastMath.PI);
    }

    @Override
    public void postWorldDraw() {
        for(Map.Entry<Integer, ProjectorDrawData> entry : DataUtils.projectorDrawMap.entrySet()) {
            try {
                SegmentController controller = (SegmentController) GameCommon.getGameObject(entry.getValue().entityId);
                if(controller != null && controller.isFullyLoaded()) {
                    SegmentPiece segmentPiece = controller.getSegmentBuffer().getPointUnsave(entry.getValue().index);
                    if(segmentPiece != null && (!segmentPiece.isActive() || (segmentPiece.isActive() && GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().isSuspended()))) {
                        Transform newTransform = new Transform();
                        segmentPiece.getTransform(newTransform);

                        int orientation = segmentPiece.getFullOrientation();
                        switch(orientation) {
                            case(Element.FRONT):
                                newTransform.basis.mul(mYC);
                                break;
                            case(Element.BACK):
                                break;
                            case(Element.TOP):
                                newTransform.basis.mul(mX);
                                break;
                            case(Element.BOTTOM):
                                newTransform.basis.mul(mYC);
                                newTransform.basis.mul(mXB);
                                break;
                            case(Element.RIGHT):
                                newTransform.basis.mul(mY);
                                break;
                            case(Element.LEFT):
                                newTransform.basis.mul(mYB);
                                break;
                        }

                        Vector3f offset = new Vector3f(entry.getValue().xOffset, entry.getValue().yOffset, entry.getValue().zOffset);
                        offset.x -= 0.01f;
                        offset.y -= 0.01f;
                        offset.z -= 0.51f;


                        if(entry.getValue().src != null && !entry.getValue().src.isEmpty()) {
                            Sprite image = ImageManager.getImage(entry.getValue().src);
                            if(image != null) {
                                ShaderLibrary.scanlineShader.setShaderInterface(this);
                                ShaderLibrary.scanlineShader.load();
                                Transform tr = new Transform();
                                tr.setIdentity();
                                tr.origin.set(offset);
                                newTransform.mul(tr);
                                ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[] {new ScalableImageSubSprite(((float) entry.getValue().scale / image.getWidth()) * -1, newTransform)};
                                Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                                ShaderLibrary.scanlineShader.unload();
                            }
                        }
                    }
                }
            } catch(Exception ignored) { }
        }
    }

    @Override
    public void update(Timer timer) {
        t += timer.getDelta() * 2f;
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
