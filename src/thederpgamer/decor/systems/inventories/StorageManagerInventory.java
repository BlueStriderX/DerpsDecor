package thederpgamer.decor.systems.inventories;

import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.player.inventory.InventoryHolder;
import org.schema.game.common.data.player.inventory.StashInventory;

/**
 * <Description>
 *
 * @author TheDerpGamer
 */
public class StorageManagerInventory extends StashInventory {

  private final transient SegmentPiece segmentPiece;

  public StorageManagerInventory(InventoryHolder inventoryHolder, SegmentPiece segmentPiece) {
    super(inventoryHolder, segmentPiece.getAbsoluteIndex());
    this.segmentPiece = segmentPiece;
  }
}
