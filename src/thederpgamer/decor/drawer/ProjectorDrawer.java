package thederpgamer.decor.drawer;

import api.common.GameClient;
import api.utils.draw.ModWorldDrawer;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.ShipManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.data.graphics.image.ScalableImageSubSprite;
import thederpgamer.decor.data.projector.HoloProjectorDrawData;
import thederpgamer.decor.data.projector.ProjectorDrawData;
import thederpgamer.decor.data.projector.TextProjectorDrawData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ImageManager;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.modules.HoloProjectorModule;
import thederpgamer.decor.modules.TextProjectorModule;
import thederpgamer.decor.utils.MathUtils;
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

    private final ConcurrentHashMap<SegmentPiece, ProjectorDrawData> projectorDrawMap;
    private final ConcurrentHashMap<SegmentPiece, GUITextOverlay> textDrawMap;
    private float time;

    public ProjectorDrawer() {
        projectorDrawMap = new ConcurrentHashMap<>();
        textDrawMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onInit() {

    }

    @Override
    public void draw() {
        for(Map.Entry<SegmentPiece, ProjectorDrawData> entry : projectorDrawMap.entrySet()) {
            SegmentPiece segmentPiece = entry.getKey();
            ProjectorDrawData drawData = entry.getValue();
            if(canDraw(segmentPiece)) {
                Transform transform = new Transform();
                segmentPiece.getTransform(transform);
                Quat4f currentRot = new Quat4f();
                transform.getRotation(currentRot);
                Quat4f addRot = new Quat4f();
                QuaternionUtil.setEuler(addRot, drawData.rotation.x / 100.0f, drawData.rotation.y / 100.0f, drawData.rotation.z / 100.0f);
                currentRot.mul(addRot);
                MathUtils.roundQuat(currentRot);
                transform.setRotation(currentRot);
                transform.origin.add(new Vector3f(drawData.offset.toVector3f()));
                MathUtils.roundVector(transform.origin);

                ShaderLibrary.scanlineShader.setShaderInterface(this);
                ShaderLibrary.scanlineShader.load();

                if(drawData instanceof HoloProjectorDrawData) {
                    HoloProjectorDrawData holoDrawData = (HoloProjectorDrawData) drawData;
                    Sprite image = ImageManager.getImage(holoDrawData.src);
                    if(image != null) {
                        float maxDim = Math.max(image.getWidth(), image.getHeight());
                        ScalableImageSubSprite[] subSprite = new ScalableImageSubSprite[]{new ScalableImageSubSprite(((float) holoDrawData.scale / maxDim) * -1, transform)};
                        Sprite.draw3D(image, subSprite, 1, Controller.getCamera());
                    }
                } else if(drawData instanceof TextProjectorDrawData) {
                    TextProjectorDrawData textDrawData = (TextProjectorDrawData) drawData;
                    if(!textDrawMap.containsKey(segmentPiece)) {
                        GUITextOverlay textOverlay = new GUITextOverlay(30, 10, GameClient.getClientState());
                        textOverlay.onInit();
                        textOverlay.setFont(ResourceManager.getFont("Monda-Bold", drawData.scale * 10, Color.decode("0x" + ((TextProjectorDrawData) drawData).color)));
                        textOverlay.setBlend(true);
                        textOverlay.doDepthTest = true;
                        projectorDrawMap.put(segmentPiece, drawData);
                        textDrawMap.put(segmentPiece, textOverlay);
                    } else {
                        GUITextOverlay textOverlay = textDrawMap.get(segmentPiece);
                        textOverlay.setScale(-textDrawData.scale / 100.0f, -textDrawData.scale / 100.0f, -textDrawData.scale / 100.0f);
                        textOverlay.setTextSimple(textDrawData.text);
                        textOverlay.setTransform(transform);
                        textOverlay.draw();
                    }
                }

                ShaderLibrary.scanlineShader.unload();
            } else {
                projectorDrawMap.remove(segmentPiece);
                textDrawMap.remove(segmentPiece);
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

    public void addProjector(SegmentPiece segmentPiece) {
        projectorDrawMap.remove(segmentPiece);
        textDrawMap.remove(segmentPiece);
        if(segmentPiece.getSegmentController().getType().equals(SimpleTransformableSendableObject.EntityType.SHIP)) {
            ShipManagerContainer managerContainer = (ShipManagerContainer) getManagerContainer(segmentPiece.getSegmentController());
            if(segmentPiece.getType() == ElementManager.getBlock("Holo Projector").getId()) {
                HoloProjectorModule module = (HoloProjectorModule) managerContainer.getModMCModule(segmentPiece.getType());
                projectorDrawMap.put(segmentPiece, module.getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation())));
            } else if(segmentPiece.getType() == ElementManager.getBlock("Text Projector").getId()) {
                TextProjectorModule module = (TextProjectorModule) managerContainer.getModMCModule(segmentPiece.getType());
                TextProjectorDrawData drawData = (TextProjectorDrawData) module.getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
                GUITextOverlay textOverlay = new GUITextOverlay(30, 10, GameClient.getClientState());
                textOverlay.onInit();
                textOverlay.setFont(ResourceManager.getFont("Monda-Bold", drawData.scale * 10, Color.decode("0x" + drawData.color)));
                textOverlay.setBlend(true);
                textOverlay.doDepthTest = true;
                projectorDrawMap.put(segmentPiece, drawData);
                textDrawMap.put(segmentPiece, textOverlay);
            }
        }
    }

    private ManagerContainer<?> getManagerContainer(SegmentController segmentController) {
        if(segmentController.getType().equals(SimpleTransformableSendableObject.EntityType.SHIP)) {
            return ((Ship) segmentController).getManagerContainer();
        } else if(segmentController.getType().equals(SimpleTransformableSendableObject.EntityType.SPACE_STATION)) {
            return ((SpaceStation) segmentController).getManagerContainer();
        } else return null;
    }

    private boolean canDraw(SegmentPiece segmentPiece) {
        if(segmentPiece.getSegmentController().isFullyLoaded() && segmentPiece.getSegmentController().getSegmentBuffer().existsPointUnsave(segmentPiece.getAbsoluteIndex())) {
            short type = segmentPiece.getSegmentController().getSegmentBuffer().getPointUnsave(segmentPiece.getAbsoluteIndex()).getType();
            return type == ElementManager.getBlock("Holo Projector").getId() || type == ElementManager.getBlock("Text Projector").getId();
        }
        return false;
    }
}
