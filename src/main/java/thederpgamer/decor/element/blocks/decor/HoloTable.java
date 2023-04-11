package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.data.drawdata.HoloTableDrawData;
import thederpgamer.decor.data.graphics.mesh.SystemMesh;
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
		blockInfo.setDescription("A decorative hologram table.");
		blockInfo.setInRecipe(false);
		blockInfo.setShoppable(false);
		blockInfo.setCanActivate(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
		blockInfo.setOrientatable(true);
		blockInfo.setIndividualSides(6);
		blockInfo.setBlockStyle(BlockStyle.NORMAL.id);
		blockInfo.controlling.add(ElementKeyMap.REACTOR_MAIN);
		blockInfo.controlling.add(ElementKeyMap.REACTOR_STABILIZER);
		ElementKeyMap.getInfo(ElementKeyMap.REACTOR_MAIN).controlledBy.add(blockInfo.getId());
		ElementKeyMap.getInfo(ElementKeyMap.REACTOR_STABILIZER).controlledBy.add(blockInfo.getId());
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
				if(drawData.systemMesh == null) {
					SegmentPiece table = event.getSegmentPiece().getSegmentController().getSegmentBuffer().getPointUnsave(drawData.tableIndex);
					SegmentPiece target = event.getSegmentPiece().getSegmentController().getSegmentBuffer().getPointUnsave(drawData.targetIndex);
					drawData.systemMesh = new SystemMesh(table, target);
				}
			}
		}
	}

	@Override
	public void onLogicActivation(SegmentPieceActivateEvent event) {
	}
}
