package thederpgamer.decor.data.system.crew;

import api.common.GameClient;
import api.common.GameServer;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.character.CharactersDrawer;
import org.schema.game.client.view.character.DrawableAIHumanCharacterNew;
import org.schema.game.common.controller.ai.AIGameCreatureConfiguration;
import org.schema.game.common.controller.ai.Types;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.creature.AICharacter;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.server.data.CreatureSpawn;
import org.schema.game.server.data.CreatureType;
import org.schema.schine.graphicsengine.animation.structure.classes.AnimationIndex;
import org.schema.schine.graphicsengine.animation.structure.classes.AnimationIndexElement;
import org.schema.schine.graphicsengine.core.settings.StateParameterNotFoundException;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.utils.AnimationUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import java.util.Objects;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class CrewData {
	public long entityID;
	public long indexAndOrientation;
	public String crewName = "Crew Member";
	public String animationName = AnimationIndex.IDLING_FLOATING.toString();
	public Vector3i spawnPos;
	public boolean looping = true;
	public boolean needsUpdate = true;
	public boolean active = true;
	private transient DrawableAIHumanCharacterNew drawer;

	public CrewData(SegmentPiece segmentPiece) {
		entityID = segmentPiece.getSegmentController().getDbId();
		indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
		Transform transform = new Transform();
		transform.setIdentity();
		segmentPiece.getTransform(transform);
		spawnPos = new Vector3i(transform.origin);
		recall();
	}

	public void spawn() {
		assert getSegmentPiece() != null && getSegmentPiece().getSegmentController().isOnServer();
		if(isAlreadySpawned()) recall();
		else {
			SegmentPiece segmentPiece = getSegmentPiece();
			Transform transform = new Transform();
			transform.setIdentity();
			segmentPiece.getTransform(transform);
			if(spawnPos == null) spawnPos = new Vector3i(transform.origin);
			CreatureSpawn spawn = new CreatureSpawn(segmentPiece.getSegmentController().getSector(new Vector3i()), transform, crewName, CreatureType.CHARACTER) {
				@Override
				public void initAI(AIGameCreatureConfiguration<?, ?> aiConfiguration) {
					try {
						assert aiConfiguration != null;
						aiConfiguration.get(Types.ORIGIN_X).switchSetting(String.valueOf(Integer.MIN_VALUE), true);
						aiConfiguration.get(Types.ORIGIN_Y).switchSetting(String.valueOf(Integer.MIN_VALUE), true);
						aiConfiguration.get(Types.ORIGIN_Z).switchSetting(String.valueOf(Integer.MIN_VALUE), true);
						aiConfiguration.get(Types.ORDER).switchSetting(AIGameCreatureConfiguration.BEHAVIOR_IDLING, true);
					} catch(StateParameterNotFoundException exception) {
						DerpsDecor.getInstance().logException("Failed to initialize AI", exception);
					}
				}
			};
			GameServer.getServerState().getController().queueCreatureSpawn(spawn);
			needsUpdate = true;
		}
	}

	public void updateCrew() {
		try {
			getCrewMember().setFactionId(getSegmentPiece().getSegmentController().getFactionId());
			if(active) AnimationUtils.setAnimation(getCrewMember(), getDrawer(), getAnimation(), looping);
			else AnimationUtils.setAnimation(getCrewMember(), getDrawer(), AnimationIndex.IDLING_FLOATING, true);
		} catch(Exception exception) {
			DerpsDecor.getInstance().logException("Failed to update crew member", exception);
		}
		needsUpdate = false;
	}

	public AICharacter getCrewMember() {
		AICharacter crewMember = null;
		try {
			boolean deleteOld = false;
			for(Sendable sendable : GameServer.getServerState().getLocalAndRemoteObjectContainer().getLocalObjects().values()) {
				if(sendable instanceof AICharacter) {
					AICharacter character = (AICharacter) sendable;
					if(character.getName().equals(crewName)) {
						if(deleteOld) character.setMarkedForDeleteVolatile(true);
						else {
							deleteOld = true;
							crewMember = character;
						}
					}
				}
			}
		} catch(Exception exception) {
			DerpsDecor.getInstance().logException("Failed to initialize crew member as it hasn't been spawned yet", exception);
		}
		return crewMember;
	}

	public SegmentPiece getSegmentPiece() {
		try {
			return Objects.requireNonNull(SegmentPieceUtils.getEntityFromDbId(entityID)).getSegmentBuffer().getPointUnsave(ElementCollection.getPosIndexFrom4(indexAndOrientation));
		} catch(NullPointerException exception) {
			DerpsDecor.getInstance().logException("Failed to get segment piece", exception);
			return null;
		}
	}

	public AnimationIndexElement getAnimation() {
		for(AnimationIndexElement animation : AnimationIndex.animations) {
			if(animation.toString().equalsIgnoreCase(animationName)) return animation;
		}
		return AnimationIndex.IDLING_FLOATING;
	}

	public DrawableAIHumanCharacterNew getDrawer() {
		if(drawer == null) {
			if(GameClient.getClientState().getWorldDrawer().getCharacterDrawer().getPlayerCharacters().containsKey(getCrewMember().getId())) {
				drawer = (DrawableAIHumanCharacterNew) GameClient.getClientState().getWorldDrawer().getCharacterDrawer().getPlayerCharacters().get(getCrewMember().getId());
			} else drawer = (DrawableAIHumanCharacterNew) CharactersDrawer.getDrawer(getCrewMember(), GameClient.getClientState().getGraphicsContext().timer, GameClient.getClientState());
		}
		return drawer;
	}

	public boolean isAlreadySpawned() {
		return getCrewMember() != null;
	}

	public void recall() {
		if(!isAlreadySpawned()) spawn();
		try {
			if(getCrewMember() == null) throw new NullPointerException("Crew member is null!");
			Transform transform = getCrewMember().getWorldTransform();
			transform.origin.set(spawnPos.x, spawnPos.y, spawnPos.z);
			getSegmentPiece().getTransform(transform);
			getCrewMember().getWorldTransform().set(transform);
		} catch(NullPointerException exception) {
			DerpsDecor.getInstance().logException("Failed to recall crew member", exception);
		}
		needsUpdate = true;
	}

	public void removeCrew() {
		if(getCrewMember() != null) getCrewMember().setMarkedForDeleteVolatile(true);
	}

	public void setCrewName(String crewName) {
		if(getCrewMember() == null) spawn();
		if(getCrewMember() != null) {
			getCrewMember().setRealName(crewName);
			this.crewName = crewName;
		}
	}
}