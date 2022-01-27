package thederpgamer.decor.element.blocks;

import api.common.GameClient;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCategory;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.player.inventory.InventoryHolder;
import org.schema.game.network.objects.remote.RemoteInventory;
import thederpgamer.decor.utils.ServerUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 */
public abstract class InventoryBlock extends Block implements ActivationInterface {

  public InventoryBlock(String name, ElementCategory category) {
    super(name, category);
  }

  @Override
  public void onPlayerActivation(SegmentPieceActivateByPlayer event) {
    ManagerContainer<?> managerContainer =
        ServerUtils.getManagerContainer(event.getSegmentPiece().getSegmentController());
    assert managerContainer != null;
    Inventory inventory = managerContainer.getInventory(event.getSegmentPiece().getAbsoluteIndex());
    if (inventory == null) {
      inventory = createInventory(managerContainer, event.getSegmentPiece());
      inventory
          .getInventoryHolder()
          .getInventoryNetworkObject()
          .getInventoriesChangeBuffer()
          .add(
              new RemoteInventory(
                  inventory,
                  inventory.getInventoryHolder(),
                  true,
                  inventory.getInventoryHolder().getInventoryNetworkObject().isOnServer()));
    }
    updateInventory(inventory, event.getSegmentPiece());
    GameClient.getClientState()
        .getGlobalGameControlManager()
        .getIngameControlManager()
        .getPlayerGameControlManager()
        .inventoryAction(inventory);
  }

  @Override
  public void onLogicActivation(SegmentPieceActivateEvent event) {}

  public abstract Inventory createInventory(InventoryHolder holder, SegmentPiece segmentPiece);

  public abstract void updateInventory(Inventory inventory, SegmentPiece segmentPiece);
}
