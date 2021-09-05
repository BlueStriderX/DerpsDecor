package thederpgamer.decor.drawer;

import api.common.GameCommon;
import api.utils.draw.ModWorldDrawer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.DrawableScene;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.data.drawdata.StrutDrawData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.systems.modules.StrutConnectorModule;
import thederpgamer.decor.utils.ServerUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public class StrutDrawer extends ModWorldDrawer implements Drawable, Shaderable {

    public final ConcurrentHashMap<SegmentPiece[], StrutDrawData> drawMap = new ConcurrentHashMap<>();
    private short strutId;

    @Override
    public void onInit() {
        strutId = ElementManager.getBlock("Strut Connector").getId();
    }

    @Override
    public void draw() {
        for(Map.Entry<SegmentPiece[], StrutDrawData> entry : drawMap.entrySet()) {
            if(checkDraw(entry.getValue(), entry.getKey()[0].getSegmentController())) {
                if(GameCommon.isOnSinglePlayer()) { //Dumb network bullshit >:U
                    if(entry.getKey()[0].getSegmentController().isFullyLoaded()) entry.getValue().draw();
                    else drawMap.remove(entry.getKey());
                } else if(GameCommon.isClientConnectedToServer()) {
                    if(entry.getKey()[0].getSegmentController().isInClientRange()) entry.getValue().draw();
                    else drawMap.remove(entry.getKey());
                }
            } else {
                StrutConnectorModule module = getModule(entry.getKey()[0]);
                module.blockMap.remove(entry.getKey());
                module.updateToServer();
                drawMap.remove(entry.getKey());
            }
        }
    }

    @Override
    public void update(Timer timer) {

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

    }

    private boolean checkDraw(StrutDrawData drawData, SegmentController segmentController) {
        return segmentController.getSegmentBuffer().existsPointUnsave(drawData.pieceAIndex) && segmentController.getSegmentBuffer().existsPointUnsave(drawData.pieceBIndex);
    }

    private StrutConnectorModule getModule(SegmentPiece segmentPiece) {
        return (StrutConnectorModule) Objects.requireNonNull(ServerUtils.getManagerContainer(segmentPiece.getSegmentController())).getModMCModule(strutId);
    }
}
