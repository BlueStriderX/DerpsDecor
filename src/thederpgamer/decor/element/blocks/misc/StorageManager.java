package thederpgamer.decor.element.blocks.misc;

import api.listener.events.block.SegmentPieceActivateByPlayer;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.ArrayList;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.InventoryMap;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.player.inventory.InventoryHolder;
import org.schema.game.common.data.player.inventory.InventorySlot;
import org.schema.game.common.data.player.inventory.StashInventory;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.element.blocks.InventoryBlock;
import thederpgamer.decor.systems.inventories.StorageManagerInventory;
import thederpgamer.decor.utils.ServerUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 */
public class StorageManager extends InventoryBlock {

  public StorageManager() {
    super("Storage Manager", ElementKeyMap.getInfo(ElementKeyMap.STASH_ELEMENT).getType());
  }

  @Override
  public void initialize() {
    if (GraphicsContext.initialized) {}
  }

  @Override
  public void onPlayerActivation(SegmentPieceActivateByPlayer event) {}

  @Override
  public StorageManagerInventory createInventory(
      InventoryHolder holder, SegmentPiece segmentPiece) {
    return new StorageManagerInventory(holder, segmentPiece);
  }

  @Override
  public void updateInventory(Inventory inventory, SegmentPiece segmentPiece) {
    StashInventory stashInventory = (StashInventory) inventory;
    SegmentController controller = segmentPiece.getSegmentController();
    ManagerContainer<?> managerContainer =
        ServerUtils.getManagerContainer(segmentPiece.getSegmentController());
    Int2ObjectAVLTreeMap<InventorySlot> slotMap = stashInventory.getMap();
    ArrayList<SegmentController> dockedList = new ArrayList<>();
    controller.railController.getDockedRecusive(dockedList);
    for (SegmentController docked : dockedList) {
      ManagerContainer<?> dockedContainer = ServerUtils.getManagerContainer(docked);
      InventoryMap inventoryMap = dockedContainer.getInventories();
    }
  }
}
