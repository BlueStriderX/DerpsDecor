package thederpgamer.decor.drawer;

import api.utils.draw.ModWorldDrawer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.tuple.Pair;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.DrawableScene;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.data.system.strut.StrutData;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public class StrutDrawer extends ModWorldDrawer implements Drawable, Shaderable {

  public final ConcurrentHashMap<Pair<SegmentPiece, SegmentPiece>, StrutData> drawMap =
      new ConcurrentHashMap<>();

  @Override
  public void update(Timer timer) {}

  @Override
  public void draw() {
    for (Map.Entry<Pair<SegmentPiece, SegmentPiece>, StrutData> entry : drawMap.entrySet()) {
      if (checkDraw(entry.getKey())) entry.getValue().draw();
      else drawMap.remove(entry.getKey());
    }
  }

  @Override
  public void cleanUp() {}

  @Override
  public boolean isInvisible() {
    return false;
  }

  @Override
  public void onInit() {}

  @Override
  public void onExit() {}

  @Override
  public void updateShader(DrawableScene drawableScene) {}

  @Override
  public void updateShaderParameters(Shader shader) {}

  private boolean checkDraw(Pair<SegmentPiece, SegmentPiece> pair) {
    SegmentController segmentController = pair.getLeft().getSegmentController();
    return segmentController != null
        && segmentController.isFullyLoadedWithDock()
        && segmentController
            .getSegmentBuffer()
            .getPointUnsave(pair.getLeft().getAbsoluteIndex())
            .equals(pair.getLeft())
        && segmentController
            .getSegmentBuffer()
            .getPointUnsave(pair.getRight().getAbsoluteIndex())
            .equals(pair.getRight());
  }
}
