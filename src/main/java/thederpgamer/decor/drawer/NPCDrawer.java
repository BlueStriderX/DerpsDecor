package thederpgamer.decor.drawer;

import api.utils.draw.ModWorldDrawer;
import org.schema.game.client.view.character.DrawableAIHumanCharacterNew;
import org.schema.game.common.data.creature.AICharacter;
import org.schema.schine.graphicsengine.core.Timer;

import java.util.HashMap;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class NPCDrawer extends ModWorldDrawer {

	private final HashMap<AICharacter, DrawableAIHumanCharacterNew> drawMap = new HashMap<>();

	@Override
	public void onInit() {
	}

	@Override
	public void draw() {
		for(DrawableAIHumanCharacterNew drawableAIHumanCharacterNew : drawMap.values()) drawableAIHumanCharacterNew.draw();
	}

	@Override
	public void update(Timer timer) {
	}

	@Override
	public void cleanUp() {
		for(DrawableAIHumanCharacterNew drawableAIHumanCharacterNew : drawMap.values()) drawableAIHumanCharacterNew.cleanUp();
	}

	@Override
	public boolean isInvisible() {
		return false;
	}

	public HashMap<AICharacter, DrawableAIHumanCharacterNew> getMap() {
		return drawMap;
	}
}
