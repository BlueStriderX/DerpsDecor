package thederpgamer.decor.data.drawdata;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.module.ByteArrayTagSerializable;
import org.lwjgl.opengl.GL11;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.Element;
import org.schema.game.common.data.world.SegmentData;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.GlUtil;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public class StrutDrawData implements Drawable, ByteArrayTagSerializable {

    public boolean initialized = false;
    public Vector4f color;
    public long pieceAIndex;
    public long pieceBIndex;
    public long entityId;

    private static final int VERTICES = 32;
    private static final float RADIUS = 0.2f;

    private final Vector3f pointA = new Vector3f();
    private final Vector3f pointB = new Vector3f();
    private final Vector3f[] points = new Vector3f[VERTICES + 1];

    public StrutDrawData(Vector4f color, SegmentPiece pieceA, SegmentPiece pieceB) {
        assert pieceA != null && pieceB != null;
        this.color = color;
        pieceAIndex = pieceA.getAbsoluteIndex();
        pieceBIndex = pieceB.getAbsoluteIndex();
        entityId = pieceA.getSegmentController().getDbId();

        pointA.set(pieceA.getAbsolutePos(new Vector3f()));
        pointB.set(pieceB.getAbsolutePos(new Vector3f()));

        Vector3f sub = new Vector3f(SegmentData.SEG_HALF, SegmentData.SEG_HALF, SegmentData.SEG_HALF);
        pointA.sub(sub);
        pointB.sub(sub);
        pieceA.getSegmentController().getWorldTransform().transform(pointA);
        pieceB.getSegmentController().getWorldTransform().transform(pointB);

        float sHorizontal = -0.05f;
        Vector3f offsetA = new Vector3f();
        int orientationA = pieceA.getOrientation();
        switch(orientationA) {
            case(Element.FRONT):
            case(Element.BOTTOM):
                offsetA.x -= sHorizontal;
                break;
            case(Element.BACK):
            case(Element.TOP):
                offsetA.x += sHorizontal;
                break;
            case(Element.RIGHT):
                offsetA.z -= sHorizontal;
                break;
            case(Element.LEFT):
                offsetA.z += sHorizontal;
                break;
        }

        Vector3f offsetB = new Vector3f();
        int orientationB = pieceB.getOrientation();
        switch(orientationB) {
            case(Element.FRONT):
            case(Element.BOTTOM):
                offsetB.x -= sHorizontal;
                break;
            case(Element.BACK):
            case(Element.TOP):
                offsetB.x += sHorizontal;
                break;
            case(Element.RIGHT):
                offsetB.z -= sHorizontal;
                break;
            case(Element.LEFT):
                offsetB.z += sHorizontal;
                break;
        }

        pointA.sub(offsetA);
        pointB.sub(offsetB);
    }

    public StrutDrawData(PacketReadBuffer packetReadBuffer) throws IOException {
        onTagDeserialize(packetReadBuffer);
    }

    @Override
    public void onInit() {
        buildMesh();
        initialized = true;
    }

    @Override
    public void draw() {
        if(!initialized) onInit();
        GlUtil.glPushMatrix();
        GlUtil.glEnable(GL11.GL_COLOR_MATERIAL);
        GlUtil.glEnable(GL11.GL_BLEND);
        GlUtil.glEnable(GL11.GL_LIGHTING);
        GlUtil.glEnable(GL11.GL_DEPTH_TEST);
        GlUtil.glDisable(GL11.GL_TEXTURE_2D);

        GlUtil.glColor4f(color);
        GlUtil.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex3f(pointA.x, pointA.y, pointA.z);
        for(int i = 0; i <= VERTICES; i ++) GL11.glVertex3f(pointA.x + points[i].x, pointA.y + points[i].y, pointA.z + points[i].z);
        GlUtil.glEnd();

        GlUtil.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex3f(pointB.x, pointB.y, pointB.z);
        for(int i = 0; i <= VERTICES; i ++) GL11.glVertex3f(pointB.x + points[i].x, pointB.y + points[i].y, pointB.z + points[i].z);
        GlUtil.glEnd();

        GlUtil.glBegin(GL11.GL_QUAD_STRIP);
        for(int i = 0; i <= VERTICES; i ++) {
            GL11.glVertex3f(pointA.x + points[i].x, pointA.y + points[i].y, pointA.z + points[i].z);
            GL11.glVertex3f(pointB.x + points[i].x, pointB.y + points[i].y, pointB.z + points[i].z);
        }
        GlUtil.glEnd();
        GlUtil.glPopMatrix();
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
        packetWriteBuffer.writeVector4f(color);
        packetWriteBuffer.writeLong(pieceAIndex);
        packetWriteBuffer.writeLong(pieceBIndex);
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        color = packetReadBuffer.readVector4f();
        pieceAIndex = packetReadBuffer.readLong();
        pieceBIndex = packetReadBuffer.readLong();
    }

    private Vector3f getFirstPerpVector(Vector3f in) {
        Vector3f result = new Vector3f();
        result.x = in.z;
        result.y = in.z;
        result.z = -(in.x + in.y);
        float length = result.length();
        result.x /= length;
        result.y /= length;
        result.z /= length;
        return result;
    }

    private void buildMesh() {
        float x = pointA.x - pointB.x;
        float y = pointA.y - pointB.y;
        float z = pointA.z - pointB.z;
        Vector3f firstPerp = getFirstPerpVector(new Vector3f(x, y, z));
        Vector3f secondPerp = new Vector3f();

        secondPerp.x = y * firstPerp.z - z * firstPerp.y;
        secondPerp.y = z * firstPerp.x - x * firstPerp.z;
        secondPerp.z = x * firstPerp.y - y * firstPerp.x;
        float length = secondPerp.length();
        secondPerp.x /= length;
        secondPerp.y /= length;
        secondPerp.z /= length;

        final float full = (float) (2.0f * Math.PI);
        for(int i = 0; i < VERTICES; i ++) {
            float angle = full * (i / (float) VERTICES);
            points[i] = new Vector3f();
            points[i].x = (float) (RADIUS * (Math.cos(angle) * firstPerp.x + Math.sin(angle) * secondPerp.x));
            points[i].y = (float) (RADIUS * (Math.cos(angle) * firstPerp.y + Math.sin(angle) * secondPerp.y));
            points[i].z = (float) (RADIUS * (Math.cos(angle) * firstPerp.z + Math.sin(angle) * secondPerp.z));
        }
        points[VERTICES] = new Vector3f(points[0]);
    }
}