package thederpgamer.decor.data.drawdata;

import api.common.GameCommon;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.module.ByteArrayTagSerializable;
import com.bulletphysics.linearmath.Transform;
import org.lwjgl.opengl.GL11;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.Element;
import org.schema.game.common.data.world.SegmentData;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import org.schema.schine.graphicsengine.forms.Mesh;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.PaintColor;

import javax.vecmath.Vector3f;
import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public class StrutDrawData implements Drawable, ByteArrayTagSerializable {

    private final Vector3f pointA = new Vector3f();
    private final Vector3f pointB = new Vector3f();
    public long pieceAIndex;
    public long pieceBIndex;
    public int entityId;
    private SegmentPiece pieceA;
    private SegmentPiece pieceB;

    private final Transform transform = new Transform();
    private final Vector3f scale = new Vector3f();
    private PaintColor color = PaintColor.WHITE;
    private Mesh mesh;

    public StrutDrawData(PaintColor color, SegmentPiece pieceA, SegmentPiece pieceB) {
        assert pieceA != null && pieceB != null;
        this.pieceA = pieceA;
        this.pieceB = pieceB;
        this.color = color;

        this.pieceAIndex = pieceA.getAbsoluteIndex();
        this.pieceBIndex = pieceB.getAbsoluteIndex();
        this.entityId = pieceA.getSegmentController().getId();
    }

    public StrutDrawData(PacketReadBuffer packetReadBuffer) throws IOException {
        onTagDeserialize(packetReadBuffer);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void draw() {
        if(!GraphicsContext.initialized) return;
        if(mesh == null) mesh = ResourceManager.getMesh("strut_tube");

        GlUtil.glEnable(GL11.GL_BLEND);
        GlUtil.glEnable(GL11.GL_LIGHTING);
        GlUtil.glEnable(GL11.GL_DEPTH_TEST);
        GlUtil.glEnable(GL11.GL_COLOR_MATERIAL);
        GlUtil.glColor4f(color.color);

        setTransform();
        mesh.loadVBO(true);
        GlUtil.glPushMatrix();
        GlUtil.glMultMatrix(transform);
        GlUtil.glPushMatrix();
        mesh.renderVBO();
        GlUtil.glPopMatrix();
        GlUtil.glPopMatrix();
        mesh.unloadVBO(true);
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeString(color.toString());
        packetWriteBuffer.writeInt(entityId);
        packetWriteBuffer.writeLong(pieceAIndex);
        packetWriteBuffer.writeLong(pieceBIndex);
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        color = PaintColor.valueOf(packetReadBuffer.readString());
        entityId = packetReadBuffer.readInt();
        pieceAIndex = packetReadBuffer.readLong();
        pieceBIndex = packetReadBuffer.readLong();

        SegmentController entity = (SegmentController) GameCommon.getGameObject(entityId);
        pieceA = entity.getSegmentBuffer().getPointUnsave(pieceAIndex);
        pieceB = entity.getSegmentBuffer().getPointUnsave(pieceBIndex);
    }

    private void setTransform() {
        if(pieceA != null && pieceB != null) {
            try {
                pointA.set(pieceA.x, pieceA.y, pieceA.z);
                pointB.set(pieceB.x, pieceB.y, pieceB.z);

                Vector3f sub = new Vector3f(SegmentData.SEG_HALF, SegmentData.SEG_HALF, SegmentData.SEG_HALF);
                pointA.sub(sub);
                pointB.sub(sub);

                pieceA.getSegmentController().getWorldTransform().transform(pointA);
                pieceB.getSegmentController().getWorldTransform().transform(pointB);

                float sHorizontal = -0.05f;
                Vector3f offsetA = new Vector3f();
                int orientationA = pieceA.getOrientation();
                switch(orientationA) {
                    case (Element.FRONT):
                    case (Element.BOTTOM):
                        offsetA.x -= sHorizontal;
                        break;
                    case (Element.BACK):
                    case (Element.TOP):
                        offsetA.x += sHorizontal;
                        break;
                    case (Element.RIGHT):
                        offsetA.z -= sHorizontal;
                        break;
                    case (Element.LEFT):
                        offsetA.z += sHorizontal;
                        break;
                }

                Vector3f offsetB = new Vector3f();
                int orientationB = pieceB.getOrientation();
                switch(orientationB) {
                    case (Element.FRONT):
                    case (Element.BOTTOM):
                        offsetB.x -= sHorizontal;
                        break;
                    case (Element.BACK):
                    case (Element.TOP):
                        offsetB.x += sHorizontal;
                        break;
                    case (Element.RIGHT):
                        offsetB.z -= sHorizontal;
                        break;
                    case (Element.LEFT):
                        offsetB.z += sHorizontal;
                        break;
                }

                pointA.sub(offsetA);
                pointB.sub(offsetB);

                //Set position
                Vector3f midPoint = new Vector3f((pointA.x + pointB.x) / 2, (pointA.y + pointB.y) / 2, (pointA.z + pointB.z) / 2);
                transform.origin.set(midPoint);

                //Set rotation
                Vector3f dir = Vector3fTools.sub(pointA, pointB);
                transform.basis.setIdentity();
                transform.basis.rotX(Vector3fTools.getAngleX(dir));
                transform.basis.rotY(Vector3fTools.getAngleY(dir));
                transform.basis.rotZ(Vector3fTools.getAngleZ(dir));

                //Set scale
                scale.set(Vector3fTools.sub(pointA, pointB));
                scale.scale(0.5f);

                //X scale
                transform.basis.m00 *= 0.9f;
                transform.basis.m10 *= 0.9f;
                transform.basis.m20 *= 0.9f;

                //Y Scale
                transform.basis.m01 *= scale.x;
                transform.basis.m11 *= scale.x;
                transform.basis.m21 *= scale.x;

                //Z Scale
                transform.basis.m02 *= 0.9f;
                transform.basis.m12 *= 0.9f;
                transform.basis.m22 *= 0.9f;
            } catch(Exception ignored) { }
        }
    }
}