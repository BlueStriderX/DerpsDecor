package thederpgamer.decor.commands;

import api.common.GameCommon;
import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import javax.annotation.Nullable;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.systems.modules.HoloProjectorModule;
import thederpgamer.decor.systems.modules.TextProjectorModule;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/23/2021]
 */
public class ClearProjectorsCommand implements CommandInterface {

  @Override
  public String getCommand() {
    return "clear_projectors";
  }

  @Override
  public String[] getAliases() {
    return new String[] {"clear_projectors"};
  }

  @Override
  public String getDescription() {
    return "Clears projector data for the current entity. Useful for debugging purposes.\n"
        + "- /%COMMAND% : Clears projector data from the current entity.";
  }

  @Override
  public boolean isAdminOnly() {
    return true;
  }

  @Override
  public boolean onCommand(PlayerState sender, String[] args) {
    SegmentController entity;
    Sendable sendable = GameCommon.getGameObject(sender.getSelectedEntityId());
    if (!(sendable instanceof SegmentController)) {
      if (sender.isControllingCore())
        entity = (SegmentController) sender.getFirstControlledTransformableWOExc();
      else {
        PlayerUtils.sendMessage(
            sender,
            "You must either be piloting an entity or have one selected to use this command.");
        return true;
      }
    } else entity = (SegmentController) sendable;

    if (args == null || args.length == 0)
      if (entity != null) clearAllProjectors(entity);
      else return false;
    return true;
  }

  @Override
  public void serverAction(@Nullable PlayerState sender, String[] args) {}

  @Override
  public StarMod getMod() {
    return DerpsDecor.getInstance();
  }

  private void clearAllProjectors(SegmentController entity) {
    ManagedUsableSegmentController<?> segmentController =
        (ManagedUsableSegmentController<?>) entity;
    short holoId = ElementManager.getBlock("Holo Projector").getId();
    short textId = ElementManager.getBlock("Text Projector").getId();
    if (segmentController.getManagerContainer().getModMCModule(holoId) != null
        && segmentController.getManagerContainer().getModMCModule(holoId).getSize() > 0) {
      try {
        HoloProjectorModule module =
            (HoloProjectorModule) segmentController.getManagerContainer().getModMCModule(holoId);
        module.resetAllProjectors();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    if (segmentController.getManagerContainer().getModMCModule(textId) != null
        && segmentController.getManagerContainer().getModMCModule(textId).getSize() > 0) {
      try {
        TextProjectorModule module =
            (TextProjectorModule) segmentController.getManagerContainer().getModMCModule(textId);
        module.resetAllProjectors();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}
