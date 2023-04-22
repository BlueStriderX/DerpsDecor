package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.HoloTableDrawData;
import thederpgamer.decor.element.blocks.ActivationInterface;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.manager.ResourceManager;
import thederpgamer.decor.utils.ProjectorUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public class HoloTable extends Block implements ActivationInterface {
	public HoloTable() {
		super("Holo Table", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription("A decorative hologram table. Can display a miniaturized hologram of a connected system.\nConnect a light source to change the color.");
		blockInfo.setInRecipe(true);
		blockInfo.setShoppable(true);
		blockInfo.setCanActivate(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
		blockInfo.setOrientatable(true);
		blockInfo.setIndividualSides(6);
		blockInfo.setBlockStyle(BlockStyle.NORMAL.id);
		blockInfo.controlling.add(ElementKeyMap.WEAPON_ID);
		blockInfo.controlling.add(ElementKeyMap.WEAPON_CONTROLLER_ID);
		blockInfo.controlling.add(ElementKeyMap.THRUSTER_ID);
		blockInfo.controlling.add(ElementKeyMap.SHIELD_CAP_ID);
		blockInfo.controlling.add(ElementKeyMap.SHIELD_REGEN_ID);
		for(short id : blockInfo.controlling) {
			ElementInformation info = ElementKeyMap.getInfo(id);
			if(info != null) info.controlledBy.add(blockInfo.getId());
		}
		blockInfo.lodShapeStyle = 1;
		blockInfo.sideTexturesPointToOrientation = false;
		if(GraphicsContext.initialized) {
			try {
				blockInfo.setBuildIconNum(ResourceManager.getTexture("holo-table-icon").getTextureId());
				BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "holo_table", null);
			} catch(Exception ignored) {}
		}
		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(), new FactoryResource(1, ElementKeyMap.TEXT_BOX), new FactoryResource(1, (short) 660));
		BlockConfig.add(blockInfo);
	}

	@Override
	public void onPlayerActivation(SegmentPieceActivateByPlayer event) {
		event.getSegmentPiece().setActive(!event.getSegmentPiece().isActive());
		if(event.getSegmentPiece().isActive()) {
			HoloTableDrawData drawData = (HoloTableDrawData) ProjectorUtils.getDrawData(event.getSegmentPiece());
			if(drawData != null) {
				drawData.setCanDraw(true);
				drawData.changed = true;
			}
		} else {
			HoloTableDrawData drawData = (HoloTableDrawData) ProjectorUtils.getDrawData(event.getSegmentPiece());
			if(drawData != null) {
				drawData.setCanDraw(false);
				drawData.changed = true;
			}
		}
	}

	@Override
	public void onLogicActivation(SegmentPieceActivateEvent event) {
		event.getSegmentPiece().setActive(!event.getSegmentPiece().isActive());
		if(event.getSegmentPiece().isActive()) {
			HoloTableDrawData drawData = (HoloTableDrawData) ProjectorUtils.getDrawData(event.getSegmentPiece());
			if(drawData != null) {
				drawData.setCanDraw(true);
				drawData.changed = true;
			}
		} else {
			HoloTableDrawData drawData = (HoloTableDrawData) ProjectorUtils.getDrawData(event.getSegmentPiece());
			if(drawData != null) {
				drawData.setCanDraw(false);
				drawData.changed = true;
			}
		}
	}
}
