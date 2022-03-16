package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.client.controller.PlayerTextAreaInput;
import org.schema.game.client.controller.element.world.ClientSegmentProvider;
import org.schema.game.client.controller.manager.ingame.PlayerInteractionControlManager;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.controller.SendableSegmentProvider;
import org.schema.game.common.data.SendableGameState;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.game.network.objects.remote.RemoteTextBlockPair;
import org.schema.game.network.objects.remote.TextBlockPair;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.blocks.ActivationInterface;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.utils.BlockIconUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/15/2021
 */
public class DisplayScreen extends Block implements ActivationInterface {

	public DisplayScreen() {
		super("Display Screen", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
	}

	@Override
	public void initialize() {
		if (GraphicsContext.initialized) {
			try {
				BlockIconUtils.createBlockIcon(blockInfo);
				BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "display_screen", null);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		blockInfo.setDescription(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getDescription());
		blockInfo.setCanActivate(true);
		blockInfo.setInRecipe(true);
		blockInfo.setShoppable(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
		blockInfo.setOrientatable(true);
		blockInfo.setIndividualSides(6);
		blockInfo.setBlockStyle(BlockStyle.NORMAL24.id);
		blockInfo.lodShapeStyle = 1;

		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(), new FactoryResource(1, ElementKeyMap.TEXT_BOX));
		BlockConfig.add(blockInfo);
	}

	@Override
	public void onPlayerActivation(final SegmentPieceActivateByPlayer event) {
		final PlayerInteractionControlManager cm = event.getControlManager();
		String text = event.getSegmentPiece().getSegment().getSegmentController().getTextMap().get(ElementCollection.getIndex4(event.getSegmentPiece().getAbsoluteIndex(), event.getSegmentPiece().getOrientation()));
		if(text == null) text = "";

		final PlayerTextAreaInput t = new PlayerTextAreaInput("EDIT_DISPLAY_BLOCK_POPUP", cm.getState(), 400, 300, SendableGameState.TEXT_BLOCK_LIMIT, SendableGameState.TEXT_BLOCK_LINE_LIMIT + 1, "Edit Holoprojector", "", text, FontLibrary.FontSize.SMALL) {

			@Override
			public void onDeactivate() {
				cm.suspend(false);
			}

			@Override
			public String[] getCommandPrefixes() {
				return null;
			}

			@Override
			public boolean onInput(String entry) {
				SendableSegmentProvider ss = ((ClientSegmentProvider) event.getSegmentPiece().getSegment().getSegmentController().getSegmentProvider()).getSendableSegmentProvider();
				TextBlockPair f = new TextBlockPair();
				f.block = ElementCollection.getIndex4(event.getSegmentPiece().getAbsoluteIndex(), event.getSegmentPiece().getOrientation());
				f.text = entry;
				System.err.println("[CLIENT]Text entry:\n\"" + f.text + "\"");
				ss.getNetworkObject().textBlockResponsesAndChangeRequests.add(new RemoteTextBlockPair(f, false));
				return true;
			}

			@Override
			public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
				return null;
			}

			@Override
			public boolean isOccluded() {
				return false;
			}


			@Override
			public void onFailedTextCheck(String msg) {

			}
		};

		t.getTextInput().setAllowEmptyEntry(true);
		t.getInputPanel().onInit();
		t.activate();
	}

	@Override
	public void onLogicActivation(SegmentPieceActivateEvent event) {

	}
}
