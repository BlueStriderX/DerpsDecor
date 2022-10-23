package thederpgamer.decor.data.graphics.mesh;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.ManagerModule;
import org.schema.game.common.controller.elements.UsableControllableElementManager;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementCollectionMesh;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.DrawableScene;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.utils.ModuleUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/03/2022]
 */
public class SystemMesh implements Drawable, Shaderable {

  private SegmentPiece table;
  private SegmentPiece target;

  public SystemMesh(SegmentPiece table, SegmentPiece target) {
    this.table = table;
    this.target = target;
    createMesh();
  }

  public SegmentPiece getTable() {
    return table;
  }

  public SegmentPiece getTarget() {
    return target;
  }

  private void createMesh() {
    HashMap<ElementCollection<?, ?, ?>, ElementCollectionMesh> meshMap = new HashMap<>();
    try {
      if (SegmentPieceUtils.isControlling(table, target)
          && table.getSegmentController().getId() == target.getSegmentController().getId()) {
        ManagerContainer<?> managerContainer =
            ModuleUtils.getManagerContainer(table.getSegmentController());
        for (ManagerModule<?, ?, ?> module : managerContainer.getModules()) {
          if (module.getElementManager() instanceof UsableControllableElementManager) {
            UsableControllableElementManager<?, ?, ?> elementManager =
                (UsableControllableElementManager<?, ?, ?>) module.getElementManager();
            for (ElementCollectionManager<?, ?, ?> collectionManager :
                elementManager.getCollectionManagers()) {
              for (ElementCollection<?, ?, ?> elementCollection :
                  collectionManager.getElementCollections()) {
                if (elementCollection.getElementCollectionId().getType() == target.getType()) {
                  // if(elementCollection.getMesh() == null)
                  // elementCollection.calculateMesh(target.getAbsoluteIndex(), true);
                  meshMap.put(elementCollection, elementCollection.getMesh());
                }
              }
            }
          }
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    try {
      for (Map.Entry<ElementCollection<?, ?, ?>, ElementCollectionMesh> entry :
          meshMap.entrySet()) {
        entry.getKey().calculateMesh(target.getAbsoluteIndex(), false);
        entry.getValue().initializeMesh();
        try {
          Field field = entry.getValue().getClass().getDeclaredField("triangles");
          field.setAccessible(true);
          float[] triangles = (float[]) field.get(entry.getValue());
          addTriangles(triangles);
        } catch (Exception exception1) {
          exception1.printStackTrace();
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void addTriangles(float[] triangles) {}

  @Override
  public void cleanUp() {}

  @Override
  public void draw() {}

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
}
