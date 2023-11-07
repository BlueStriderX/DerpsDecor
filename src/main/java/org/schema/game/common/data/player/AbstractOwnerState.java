package org.schema.game.common.data.player;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.damage.effects.MetaWeaponEffectInterface;
import org.schema.game.common.controller.elements.FactoryAddOn;
import org.schema.game.common.controller.elements.InventoryMap;
import org.schema.game.common.controller.elements.cargo.CargoElementManager;
import org.schema.game.common.controller.elements.factory.FactoryProducerInterface;
import org.schema.game.common.controller.trade.manualtrade.ManualTrade;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.MetaObjectState;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.creature.AIPlayer;
import org.schema.game.common.data.element.Element;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FixedRecipe;
import org.schema.game.common.data.element.meta.MetaObject;
import org.schema.game.common.data.element.meta.RecipeInterface;
import org.schema.game.common.data.element.meta.weapon.Weapon;
import org.schema.game.common.data.player.faction.FactionInterface;
import org.schema.game.common.data.player.inventory.*;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.network.objects.remote.RemoteInventoryClientAction;
import org.schema.game.network.objects.remote.RemoteInventoryMultMod;
import org.schema.game.network.objects.remote.RemoteInventorySlotRemove;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.network.TopLevelType;
import org.schema.schine.network.objects.Sendable;
import org.schema.schine.network.server.ServerMessage;
import org.schema.schine.resource.tag.FinishTag;
import org.schema.schine.resource.tag.Tag;
import org.schema.schine.resource.tag.Tag.Type;
import org.schema.schine.resource.tag.TagSerializable;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public abstract class AbstractOwnerState implements InventoryHolder, FactionInterface, Damager, FactoryProducerInterface, TagSerializable {
	public static final int NORM_INV = 0;
	public static final int CAPSULE_INV = 1;
	public static final int MICRO_INV = 2;
	public static final int MACRO_BLOCK_INV = 3;
	public static final int NORM_FORCE = -1;
	public static final long FACTORY_TIME = 10000;
	public final Vector3i sittingPos = new Vector3i();
	public final Vector3i sittingPosTo = new Vector3i();
	public final Vector3i sittingPosLegs = new Vector3i();
	private final ArrayList<InventoryMultMod> ntInventoryMultMods = new ArrayList<InventoryMultMod>();
	private final Object2ObjectOpenHashMap<Inventory, IntOpenHashSet> changedSet = new Object2ObjectOpenHashMap<Inventory, IntOpenHashSet>();
	private final ObjectArrayFIFOQueue<InventorySlotRemoveMod> ntInventorySlotRemoveMods = new ObjectArrayFIFOQueue<InventorySlotRemoveMod>();
	private final InventoryMap invMap = new InventoryMap();
	public int sittingOnId = -1;
	public PlayerState conversationPartner;
	public List<ManualTrade> activeManualTrades = new ObjectArrayList<ManualTrade>();
	protected Inventory inventory;
	protected Inventory creativeInventory;
	protected Inventory virtualCreativeInventory;
	protected PersonalFactoryInventory personalFactoryInventoryCapsule;
	protected PersonalFactoryInventory personalFactoryInventoryMicro;
	protected PersonalFactoryInventory personalFactoryInventoryMacroBlock;
	protected String conversationScript = "";
	protected long sittingStarted;
	protected String sittingUIDServer = "none";
	protected String sittingUIDServerInitial = "none";
	private ObjectArrayFIFOQueue<InventoryClientAction> clientInventoryActions = new ObjectArrayFIFOQueue<InventoryClientAction>();
	private Short2IntOpenHashMap delayedInventoryMods = new Short2IntOpenHashMap();
	private boolean writtenForUnload;
	private RecipeInterface currentRecipe;
	private Vector4f tint = new Vector4f(1, 1, 1, 1);
	private long lastFactoryTime;
	private long lastLagSent;

	public int getSelectedBuildSlot() {
		return getNetworkObject().getBuildSlot().getByte();
	}

	public void setSelectedBuildSlot(int i) {
		getNetworkObject().getBuildSlot().set((byte) Math.min(getInventory().getActiveSlotsMax(), Math.max(0, i)), true);
	}

	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * @param inventory the inventory to set
	 */
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public abstract NetworkPlayerInterface getNetworkObject();

	public boolean isCreativeModeEnabled() {
		return false;
	}

	public boolean isPrivateNetworkObject() {
		return false;
	}

	public abstract void damage(float damage, Destroyable destroyable, Damager from);

	public abstract void heal(float heal, Destroyable destroyable, Damager from);

	public abstract float getMaxHealth();

	public abstract byte getFactionRights();

	public abstract float getHealth();

	public abstract Vector3f getRight(Vector3f out);

	public abstract Vector3f getUp(Vector3f out);

	public abstract Vector3f getForward(Vector3f out);

	public abstract boolean isInvisibilityMode();

	public PlayerState getConversationPartner() {
		return conversationPartner;
	}

	public void handleDelayedModifications() {
		if(!delayedInventoryMods.isEmpty()) {
			synchronized(delayedInventoryMods) {
				IntOpenHashSet modifiedSlots = new IntOpenHashSet();
				for(short type : delayedInventoryMods.keySet()) {
					Inventory inventory = getInventory();
					int slot = inventory.incExistingOrNextFreeSlot(type, delayedInventoryMods.get(type));
					modifiedSlots.add(slot);
				}
				sendInventoryModification(modifiedSlots, Long.MIN_VALUE);
				delayedInventoryMods.clear();
			}
		}
	}

	public abstract boolean isHarvestingButton();

	protected abstract void onNoSlotFree(short type, int amount);

	public void modDelayPersonalInventory(short type, int i) {
		int prev = delayedInventoryMods.containsKey(type) ? delayedInventoryMods.get(type) : 0;
		delayedInventoryMods.put(type, prev + i);
	}

	public Vector4f getTint() {
		return tint;
	}

	/* (non-Javadoc)
	 * @see org.schema.game.common.data.Damager#sendHitConfirm()
	 */
	@Override
	public void sendHitConfirm(byte damageType) {
	}

	/* (non-Javadoc)
	 * @see org.schema.game.common.data.Damager#isSegmentController()
	 */
	@Override
	public boolean isSegmentController() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SimpleTransformableSendableObject<?> getShootingEntity() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.schema.game.common.data.Damager#getPlayerState()
	 */
	@Override
	public AbstractOwnerState getOwnerState() {
		return this;
	}

	@Override
	public void sendClientMessage(String str, int type) {
		if(!isOnServer()) {
			switch(type) {
				case (ServerMessage.MESSAGE_TYPE_INFO):
					((GameClientState) getState()).getController().popupInfoTextMessage(str, 0);
					break;
				default:
					((GameClientState) getState()).getController().popupAlertTextMessage(str, 0);
					break;
			}
		}
	}

	@Override
	public float getDamageGivenMultiplier() {
		return 1;
	}

	@Override
	public InterEffectSet getAttackEffectSet(long weaponId, DamageDealerType damageDealerType) {
		MetaObject o = ((MetaObjectState) getState()).getMetaObjectManager().getObject((int) weaponId);
		if(o instanceof Weapon) {
			InterEffectSet effectSet = ((Weapon) o).getEffectSet();
			assert (effectSet != null) : weaponId + "; " + o;
			return effectSet;
		} else {
			assert (false) : "WEP: " + weaponId;
			return null;
		}
	}

	public MetaWeaponEffectInterface getMetaWeaponEffect(long weaponId, DamageDealerType damageDealerType) {
		MetaObject o = ((MetaObjectState) getState()).getMetaObjectManager().getObject((int) weaponId);
		if(o instanceof Weapon) {
			return ((Weapon) o).getMetaWeaponEffect();
		} else {
			return null;
		}
	}

	public abstract boolean isVulnerable();

	public void updateLocal(Timer timer) {
		manufactureStep(timer);
		if(!clientInventoryActions.isEmpty()) {
			assert (isOnServer());
			Object2ObjectOpenHashMap<Inventory, IntOpenHashSet> moddedSlots = new Object2ObjectOpenHashMap<Inventory, IntOpenHashSet>();
			synchronized(clientInventoryActions) {
				while(!clientInventoryActions.isEmpty()) {
					try {
						InventoryClientAction d = clientInventoryActions.dequeue();
						assert (d.ownInventoryOwnerId == getId());
						Sendable s = getState().getLocalAndRemoteObjectContainer().getLocalObjects().get(d.otherInventoryOwnerId);
						if(s != null) {
							InventoryHolder holder;
							if(s instanceof ManagedSegmentController<?>) {
								holder = ((ManagedSegmentController<?>) s).getManagerContainer();
							} else {
								holder = (InventoryHolder) s;
							}
							Inventory inventory = getInventory(d.ownInventoryPosId);
							if(inventory != null) {
								System.err.println("[SERVER] execute action: " + d);
								inventory.doSwitchSlotsOrCombine(d.slot, d.otherSlot, d.subSlot,
									holder.getInventory(d.otherInventoryPosId), d.count, moddedSlots);
							} else {
								assert (false);
							}
						} else {
							assert (false);
						}
					} catch(InventoryExceededException e) {
						e.printStackTrace();
					}
				}
			}
			if(!moddedSlots.isEmpty()) {
				for(Entry<Inventory, IntOpenHashSet> e : moddedSlots.entrySet()) {
					e.getKey().sendInventoryModification(e.getValue());
				}
			}
		}
		if(isOnServer()) {
			if(!sittingUIDServerInitial.equals("none")) {
				Sendable sendable = getState().getLocalAndRemoteObjectContainer().getUidObjectMap().get(sittingUIDServerInitial);
				if(sendable == null && this instanceof AIPlayer) {
					sendable = ((AIPlayer) this).getCreature().getAffinity();
				}
				if(getAbstractCharacterObject() != null && sendable != null && sendable instanceof SegmentController) {
					sittingUIDServerInitial = "none";
					SegmentController c = (SegmentController) sendable;
					System.err.println("[SERVER] read sitting from tag. found UID: " + sendable + "; " + sittingPos + "; " + sittingPosTo + "; " + sittingPosLegs);
					if(getAbstractCharacterObject().getGravity().source == null || getAbstractCharacterObject().getGravity().source != c) {
						SegmentController align = c;
						getAbstractCharacterObject().scheduleGravity(new Vector3f(0, 0, 0), align);
					}
					sitDown(c, sittingPos, sittingPosTo, sittingPosLegs);
				} else {
//					System.err.println("[SERVER] read sitting from tag. not found UID or character not initialized: "+getAbstractCharacterObject()+"; "+sittingOnLoadedUID);
				}
			}
			if(sittingOnId >= 0) {
				Sendable sendable = getState().getLocalAndRemoteObjectContainer().getLocalObjects().get(sittingOnId);
				boolean ok = false;
				if(sendable != null && sendable instanceof SegmentController) {
					SegmentController c = (SegmentController) sendable;
					if(!c.isHidden()) {
						SegmentPiece pSitting = c.getSegmentBuffer().getPointUnsave(sittingPos);//autorequest true previously
						if(pSitting == null) {
							//wait for loading
							ok = true;
						} else if(pSitting.getType() == Element.TYPE_NONE) {
							Vector3i axis = new Vector3i();
							axis.sub(sittingPosLegs, sittingPosTo);
							//block below sitting
							axis.add(sittingPos);
							SegmentPiece pBelowSitting = c.getSegmentBuffer().getPointUnsave(axis);//autorequest true previously
							if(pBelowSitting == null || pBelowSitting.getType() != Element.TYPE_NONE) {
								//wait for loading or when there is a block
								ok = true;
							} else {
								System.err.println("[AbstractOwner] " + getState() + " Character sitting pos invalid");
							}
						} else {
							ok = ElementKeyMap.isValidType(pSitting.getType()) && ElementKeyMap.getInfo(pSitting.getType()).getBlockStyle() == BlockStyle.WEDGE;
						}
					} else {
						System.err.println("[AbstractOwner] " + getState() + " " + this + " Character sitting but hidden");
					}
					sittingUIDServer = c.getUniqueIdentifier();
				}
				if(System.currentTimeMillis() - sittingStarted > 5000) {
					//check with delay
					if(getAbstractCharacterObject() == null || getAbstractCharacterObject().getGravity().source == null || getAbstractCharacterObject().getGravity().source.getId() != sittingOnId) {
						if(getAbstractCharacterObject() == null) {
							System.err.println("[AbstractOwner] " + getState() + " " + this + " Character has to stand up: No character assigned");
						} else if(getAbstractCharacterObject().getGravity().source == null) {
							System.err.println("[AbstractOwner] " + getState() + " " + this + " Character has to stand up: No gravity source");
						} else {
							System.err.println("[AbstractOwner] " + getState() + " " + this + " Character has to stand up: gravity source id doesnt match sitting id");
						}
						ok = false;
					}
				}
				if(!ok) {
					if(this instanceof PlayerState) {
						((PlayerState) this).sendServerMessage(new ServerMessage(Lng.astr("Can no longer sit here!"), ServerMessage.MESSAGE_TYPE_ERROR, getId()));
					}
					System.err.println("[AbstractOwner] " + getState() + " Character cannot longer sit there");
					sittingOnId = -1;
					sittingUIDServer = "none";
				}
			} else {
				sittingUIDServer = "none";
			}
		}
	}

	private void manufactureStep(Timer timer) {
		if(isOnServer() && isFactoryInUse()) {
			long currentStep = getState().getController().getServerRunningTime() / FACTORY_TIME;
			if(currentStep > lastFactoryTime) {
				changedSet.clear();
				produce(getPersonalFactoryInventoryCapsule(), changedSet);
				produce(getPersonalFactoryInventoryMicro(), changedSet);
				produce(getPersonalFactoryInventoryMacroBlock(), changedSet);
				int sentInventories = 0;
				int i = 0;
				for(Entry<Inventory, IntOpenHashSet> a : changedSet.entrySet()) {
					if(!a.getValue().isEmpty()) {
						IntOpenHashSet copy = new IntOpenHashSet();
						copy.addAll(a.getValue());
						a.getKey().sendInventoryModification(copy);
						a.getValue().clear();
						i++;
						sentInventories++;
					}
				}
				lastFactoryTime = currentStep;
			}
		}
	}

	public abstract boolean isOnServer();

	public abstract AbstractCharacter<? extends AbstractOwnerState> getAbstractCharacterObject();

	public void sitDown(SegmentController c, Vector3i s, Vector3i sTo, Vector3i slegs) {
		sittingOnId = c.getId();
		sittingPos.set(s);
		sittingPosTo.set(sTo);
		sittingPosLegs.set(slegs);
		sittingStarted = System.currentTimeMillis();
	}

	public abstract boolean isFactoryInUse();

	private void produce(PersonalFactoryInventory ownInventory, Object2ObjectOpenHashMap<Inventory, IntOpenHashSet> changedSet2) {
		assert (isOnServer());
		if(ownInventory.getFactoryType() == ElementKeyMap.FACTORY_CAPSULE_ASSEMBLER_ID) {
			setCurrentRecipe(ElementKeyMap.personalCapsuleRecipe);
		} else if(ownInventory.getFactoryType() == ElementKeyMap.FACTORY_MICRO_ASSEMBLER_ID) {
			setCurrentRecipe(ElementKeyMap.microAssemblerRecipe);
		} else if(ElementKeyMap.isMacroFactory(ownInventory.getFactoryType())) {
			setCurrentRecipe(ElementKeyMap.macroBlockRecipe);
		} else {
			assert (false);
			setCurrentRecipe(null);
		}
		if(getCurrentRecipe() != null) {
			int productCount = FactoryAddOn.getProductCount(getCurrentRecipe());
			for(int productChainIndex = 0; productChainIndex < productCount; productChainIndex++) {
				IntOpenHashSet changedOwnSet = changedSet2.get(ownInventory);
				if(changedOwnSet == null) {
					changedOwnSet = new IntOpenHashSet();
					changedSet2.put(ownInventory, changedOwnSet);
				}
				FactoryAddOn.produce(getCurrentRecipe(), productChainIndex, ownInventory, this, changedOwnSet, (GameServerState) getState());
			}
		}
	}

	/**
	 * @return the personalFactoryInventoryCapsule
	 */
	public PersonalFactoryInventory getPersonalFactoryInventoryCapsule() {
		return personalFactoryInventoryCapsule;
	}

	/**
	 * @param personalFactoryInventoryCapsule the personalFactoryInventoryCapsule to set
	 */
	public void setPersonalFactoryInventoryCapsule(
		PersonalFactoryInventory personalFactoryInventoryCapsule) {
		this.personalFactoryInventoryCapsule = personalFactoryInventoryCapsule;
	}

	/**
	 * @return the personalFactoryInventoryMicro
	 */
	public PersonalFactoryInventory getPersonalFactoryInventoryMicro() {
		return personalFactoryInventoryMicro;
	}

	/**
	 * @param personalFactoryInventoryMicro the personalFactoryInventoryMicro to set
	 */
	public void setPersonalFactoryInventoryMicro(
		PersonalFactoryInventory personalFactoryInventoryMicro) {
		this.personalFactoryInventoryMicro = personalFactoryInventoryMicro;
	}

	/**
	 * @return the personalFactoryInventoryMacroBlock
	 */
	public PersonalFactoryInventory getPersonalFactoryInventoryMacroBlock() {
		return personalFactoryInventoryMacroBlock;
	}

	public Inventory getPersonalInventory() {
		return inventory;
	}

	/* (non-Javadoc)
	 * @see org.schema.game.common.controller.elements.factory.FactoryProducerInterface#getCurrentRecipe()
	 */
	@Override
	public RecipeInterface getCurrentRecipe() {
		return currentRecipe;
	}

	/* (non-Javadoc)
	 * @see org.schema.game.common.controller.elements.factory.FactoryProducerInterface#getCapability()
	 */
	@Override
	public int getFactoryCapability() {
		return 1;
	}

	public void setCurrentRecipe(FixedRecipe capsuleRecipe) {
		currentRecipe = capsuleRecipe;
	}

	/**
	 * @param personalFactoryInventoryMacroBlock the personalFactoryInventoryMacroBlock to set
	 */
	public void setPersonalFactoryInventoryMacroBlock(
		PersonalFactoryInventory personalFactoryInventoryMacroBlock) {
		this.personalFactoryInventoryMacroBlock = personalFactoryInventoryMacroBlock;
	}

	public void standUp() {
		sittingOnId = -1;
		sittingStarted = 0;
		sittingUIDServer = "none";
	}

	public void fromSittingTag(Tag t) {
		Tag[] s = (Tag[]) t.getValue();
		sittingPos.set((Vector3i) s[0].getValue());
		sittingPosTo.set((Vector3i) s[1].getValue());
		sittingPosLegs.set((Vector3i) s[2].getValue());
		sittingUIDServerInitial = (String) s[3].getValue();
	}

	public Tag toSittingTag() {
		return new Tag(Type.STRUCT, null, new Tag[] {
			new Tag(Type.VECTOR3i, null, sittingPos),
			new Tag(Type.VECTOR3i, null, sittingPosTo),
			new Tag(Type.VECTOR3i, null, sittingPosLegs),
			new Tag(Type.STRING, null, sittingUIDServer),
			FinishTag.INST
		});
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AbstractState(" + getName() + ", " + getId() + ")";
	}

	@Override
	public InventoryMap getInventories() {
		invMap.put(NORM_INV, getInventory());
		return invMap;
	}

	@Override
	public Inventory getInventory(long pos) {
		long z = pos;
		if(pos == Long.MIN_VALUE || z == NORM_INV) {
			return getInventory();
		} else if(z == NORM_FORCE) {
			return getPersonalInventory();
		} else if(z == CAPSULE_INV) {
			return getPersonalFactoryInventoryCapsule();
		} else if(z == MACRO_BLOCK_INV) {
			return getPersonalFactoryInventoryMacroBlock();
		} else {
			assert (z == MICRO_INV) : z;
			return getPersonalFactoryInventoryMicro();
		}
	}

	/* (non-Javadoc)
	 * @see org.schema.game.common.data.player.inventory.InventoryHolder#getInventoryNetworkObject()
	 */
	@Override
	public NetworkInventoryInterface getInventoryNetworkObject() {
		return getNetworkObject();
	}

	/* (non-Javadoc)
	 * @see org.schema.game.common.data.player.inventory.InventoryHolder#printInventories()
	 */
	@Override
	public String printInventories() {
		return getInventory().toString();
	}

	/* (non-Javadoc)
	 * @see org.schema.game.common.data.player.inventory.InventoryHolder#sendInventoryModification(java.util.Collection, org.schema.common.util.linAlg.Vector3i)
	 */
	@Override
	public void sendInventoryModification(
		IntCollection slots, long parameter) {
		Inventory inventory = getInventory(parameter);
//		System.err.println("SENDTING TO __ "+inventory+":: "+inventory.getClass().getSimpleName()+" "+slots+"; "+parameter);
		InventoryMultMod m = new InventoryMultMod(slots, inventory, parameter);
		getNetworkObject().getInventoryMultModBuffer().add(new RemoteInventoryMultMod(m, isOnServer()));
	}

	/* (non-Javadoc)
	 * @see org.schema.game.common.data.player.inventory.InventoryHolder#sendInventoryModification(int, org.schema.common.util.linAlg.Vector3i)
	 */
	@Override
	public void sendInventoryModification(int slot, long parameter) {
		/*
		 * parameter can be ignored in this
		 * implementation of the inventory interface
		 * since player states only have one inventory
		 */
		IntArrayList l = new IntArrayList(1);
		l.add(slot);
		sendInventoryModification(l, parameter);
	}

	@Override
	public abstract int getId();

	@Override
	public void sendInventorySlotRemove(int slot, long parameter) {
		Inventory inventory = getInventory(parameter);
		if(inventory != null) {
			getNetworkObject().getInventorySlotRemoveRequestBuffer().add(new RemoteInventorySlotRemove(
				new InventorySlotRemoveMod(slot, parameter), isOnServer()));
		} else {
			try {
				throw new IllegalArgumentException("[INVENTORY] Exception: tried to send inventory " + parameter);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public double getCapacityFor(Inventory inventory) {
		if(inventory == getInventory()) {
			return isInfiniteInventoryVolume() ? 99999999999999d : CargoElementManager.PERSONAL_INVENTORY_BASE_CAPACITY;
		} else if(inventory == getPersonalFactoryInventoryCapsule()) {
			return CargoElementManager.PERSONAL_FACTORY_BASE_CAPACITY;
		} else if(inventory == getPersonalFactoryInventoryMacroBlock()) {
			return CargoElementManager.PERSONAL_FACTORY_BASE_CAPACITY;
		} else if(inventory == getPersonalFactoryInventoryMicro()) {
			return CargoElementManager.PERSONAL_FACTORY_BASE_CAPACITY;
		}
		return 0;
	}

	public boolean isInfiniteInventoryVolume() {
		return false;
	}

	@Override
	public void volumeChanged(double volumeBefore, double volumeNow) {
	}

	@Override
	public void sendInventoryErrorMessage(Object[] astr, Inventory inv) {
	}

	public Inventory getInventory(Vector3i o) {
		if(o == null) {
			return getInventory(NORM_INV);
		} else {
			return getInventory(o.z);
		}
	}

	public abstract void onFiredWeapon(Weapon object);

	public void handleInventoryNT() {
		{
			ObjectArrayList<RemoteInventoryMultMod> receiveBuffer =
				getInventoryNetworkObject().getInventoryMultModBuffer().getReceiveBuffer();
			for(int i = 0; i < receiveBuffer.size(); i++) {
				RemoteInventoryMultMod a = receiveBuffer.get(i);
				synchronized(ntInventoryMultMods) {
					ntInventoryMultMods.add(a.get());
				}
			}
		}
		{
			ObjectArrayList<RemoteInventorySlotRemove> receiveBuffer =
				getInventoryNetworkObject().getInventorySlotRemoveRequestBuffer().getReceiveBuffer();
			for(int i = 0; i < receiveBuffer.size(); i++) {
				InventorySlotRemoveMod a = receiveBuffer.get(i).get();
				synchronized(ntInventorySlotRemoveMods) {
					ntInventorySlotRemoveMods.enqueue(a);
				}
			}
		}
		if(getInventoryNetworkObject().getInventoryClientActionBuffer().getReceiveBuffer().size() > 0) {
			for(RemoteInventoryClientAction ia : getInventoryNetworkObject().getInventoryClientActionBuffer().getReceiveBuffer()) {
				InventoryClientAction inventoryClientAction = ia.get();
				synchronized(clientInventoryActions) {
					clientInventoryActions.enqueue(inventoryClientAction);
				}
			}
		}
	}

	public void updateInventory() {
		if(isOnServer()) {
			handleDelayedModifications();
		} else {
			getInventory().clientUpdate();
			getPersonalFactoryInventoryCapsule().clientUpdate();
			getPersonalFactoryInventoryMicro().clientUpdate();
			getPersonalFactoryInventoryMacroBlock().clientUpdate();
		}
		if(!ntInventoryMultMods.isEmpty()) {
			synchronized(ntInventoryMultMods) {
				ArrayList<InventoryMultMod> failed = new ArrayList<InventoryMultMod>();
				while(!ntInventoryMultMods.isEmpty()) {
					InventoryMultMod a = ntInventoryMultMods.remove(0);
					long z = a.parameter;
					if(a.parameter == Long.MIN_VALUE || z == NORM_INV) {
						getInventory().handleReceived(a, getInventoryNetworkObject());
					} else if(z == NORM_FORCE) {
						getPersonalInventory().handleReceived(a, getInventoryNetworkObject());
					} else if(z == CAPSULE_INV) {
						getPersonalFactoryInventoryCapsule().handleReceived(a, getInventoryNetworkObject());
					} else if(z == MICRO_INV) {
						getPersonalFactoryInventoryMicro().handleReceived(a, getInventoryNetworkObject());
					} else if(z == MACRO_BLOCK_INV) {
						getPersonalFactoryInventoryMacroBlock().handleReceived(a, getInventoryNetworkObject());
					}
				}
				if(!failed.isEmpty()) {
					ntInventoryMultMods.addAll(failed);
				}
			}
		}
		if(!ntInventorySlotRemoveMods.isEmpty()) {
			synchronized(ntInventorySlotRemoveMods) {
				while(!ntInventorySlotRemoveMods.isEmpty()) {
					InventorySlotRemoveMod a = ntInventorySlotRemoveMods.dequeue();
					Inventory inventory = getInventory(a.parameter);
					if(inventory != null) {
						boolean send = isOnServer();
						inventory.removeSlot(a.slot, send);
					}
				}
			}
		}
	}

	public void updateToFullNetworkObject() {
		getInventory().sendAll();
		getPersonalFactoryInventoryCapsule().sendAll();
		getPersonalFactoryInventoryMicro().sendAll();
		getPersonalFactoryInventoryMacroBlock().sendAll();
	}

	public abstract String getConversationScript();

	public void setConversationScript(String scriptName) {
		conversationScript = scriptName;
	}

	/* (non-Javadoc)
	 * @see org.schema.schine.network.objects.Sendable#isWrittenForUnload()
	 */
	public boolean isWrittenForUnload() {
		return writtenForUnload;
	}

	/* (non-Javadoc)
	 * @see org.schema.schine.network.objects.Sendable#setWrittenForUnload(boolean)
	 */
	public void setWrittenForUnload(boolean b) {
		writtenForUnload = b;
	}

	public boolean isSitting() {
		return sittingOnId >= 0;
	}

	public void announceLag(long timeTaken) {
		if(System.currentTimeMillis() - lastLagSent > 1000) {
			assert (getState().isSynched());
			getNetworkObject().getLagAnnouncement().add(timeTaken);
			lastLagSent = System.currentTimeMillis();
		}
	}

	public TopLevelType getTopLevelType() {
		return TopLevelType.PLAYER;
	}

	public boolean isControllingCore() {
		return false;
	}

	public abstract long getDbId();

	public boolean isSpawnProtected() {
		return false;
	}
}
