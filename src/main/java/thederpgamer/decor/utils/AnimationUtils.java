package thederpgamer.decor.utils;

import org.schema.game.client.view.character.DrawableAIHumanCharacterNew;
import org.schema.game.common.data.creature.AICharacter;
import org.schema.schine.graphicsengine.animation.LoopMode;
import org.schema.schine.graphicsengine.animation.structure.classes.AnimationIndexElement;
import thederpgamer.decor.DerpsDecor;

/**
 * Provides utility methods for managing animations for AI characters.
 */
public class AnimationUtils {
	/**
	 * Sets the animation for the specified crew member.
	 *
	 * @param crewMember the crew member
	 * @param animation  the animation
	 *
	 * @throws Exception if an error occurs
	 */
	public static void setAnimation(AICharacter crewMember, DrawableAIHumanCharacterNew drawer, AnimationIndexElement animation, boolean looping) throws Exception {
		if(crewMember == null) {
			DerpsDecor.getInstance().logWarning("Error setting animation for crew member: Crew member is null");
			return;
		}
		drawer.setAnimForced(animation, 0.2f, (looping) ? LoopMode.LOOP : LoopMode.DONT_LOOP);
	}
}