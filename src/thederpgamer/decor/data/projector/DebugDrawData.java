package thederpgamer.decor.data.projector;

import api.common.GameClient;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.Element;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/27/2021
 */
public class DebugDrawData {

    public final Vector2f center = new Vector2f();
    public final Vector2f pos = new Vector2f();

    public final SegmentPiece segmentPiece;
    public final Sprite debugGrid;
    public final GUITextOverlay posOverlay;

    public DebugDrawData(SegmentPiece segmentPiece, ProjectorDrawData drawData) {
        Vector3f facePos = SegmentPieceUtils.getPieceFacePos(segmentPiece, Element.FRONT);
        this.center.set(facePos.z, facePos.y);
        this.pos.set(0.0f, 0.0f);
        this.segmentPiece = segmentPiece;
        this.debugGrid = ResourceManager.getSprite("projector-debug-grid");
        this.debugGrid.setTransform(drawData.transform);
        this.posOverlay = new GUITextOverlay(10, 10, GameClient.getClientState());
        this.posOverlay.onInit();
        this.posOverlay.setFont(FontLibrary.FontSize.SMALL.getFont());
        this.posOverlay.setTransform(drawData.transform);
        this.posOverlay.setScale(-10 / 1000.0f, -10 / 1000.0f, -10 / 1000.0f);
        this.posOverlay.setBlend(true);
        this.posOverlay.doDepthTest = true;
    }

    public void update() {
        pos.set(posOverlay.getPos().x, posOverlay.getPos().y);
        posOverlay.setTextSimple(pos.toString());
    }
}
