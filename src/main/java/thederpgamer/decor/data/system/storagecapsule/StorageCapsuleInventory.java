package thederpgamer.decor.data.system.storagecapsule;

/**
 * An inventory that can hold a large amount of a single item.
 *
 * @author TheDerpGamer
 */
public class StorageCapsuleInventory {//extends Inventory {

	/*
	private String customName;
	private InventorySlot slot;

	public StorageCapsuleInventory(InventoryHolder state, long parameter) {
		super(state, parameter);
	}

	@Override
	public int getActiveSlotsMax() {
		return 0;
	}

	@Override
	public String getCustomName() {
		return customName;
	}

	@Override
	public void clear() {
		System.err.println("[INVENTORY] CLEARING INVENTORY (CLEAR)");
		setVolume(0.0);
		slot = null;
		inventoryMap.clear();
	}

	@Override
	public void clear(IntOpenHashSet mod) {
		System.err.println("[INVENTORY] CLEARING INVENTORY (CLEAR)");
		setVolume(0.0);
		slot = null;
		inventoryMap.clear();
	}

	@Override
	public InventorySlot getSlot(int inventorySlot) {
		return slot;
	}

	@Override
	public void removeFromCountAndSlotMap(InventorySlot slot) {
		if(this.slot == slot) this.slot = null;
		else {
			if(this.slot.isMultiSlot() && slot.isMultiSlot() && this.slot.isMultiSlotCompatibleTo(slot.getType())) this.slot.setMulti(slot.getType(), Math.max(0, slot.getMultiCount(slot.getType())));
			else this.slot = null;
		}
	}

	@Override
	public void removeFromCountAndSlotMap(InventorySlot slot, int slotPos) {
		removeFromCountAndSlotMap(slot);
	}

	@Override
	public void deleteAllSlotsWithType(short type, Collection<Integer> changedOut) {
		if(slot != null && slot.getType() == type) {
			slot = null;
			changedOut.add(0);
		}
	}

	@Override
	public InventorySlot deserializeSlot(DataInput buffer) throws IOException {
		if(slot == null) slot = new InventorySlot();
		slot.setType(buffer.readShort());
		slot.setCount(buffer.readInt());
		slot.metaId = buffer.readInt();
		slot.slot = 0;
		slot.setInfinite(false);
		byte subSlots = buffer.readByte();
		if(subSlots >= 0) {
			slot.multiSlot = buffer.readUTF();
			slot.setType(Short.MIN_VALUE);
			slot.setInfinite(false);
			for(int j = 0; j < subSlots; ++j) {
				InventorySlot subSlot = deserializeSlot(buffer);
				slot.getSubSlots().add(subSlot);
			}
			Collections.sort(slot.getSubSlots(), InventorySlot.subSlotSorter);
		}
		return slot;
	}

	@Override
	public void deserialize(DataInput buffer) throws IOException {
		int size = buffer.readInt();

		for(int i = 0; i < size; ++i) {
			InventorySlot desSlot = this.deserializeSlot(buffer);
			InventorySlot inventorySlot = (InventorySlot)this.inventoryMap.get(desSlot.slot);
			List subSlots;
			Iterator i$;
			InventorySlot ss;
			long now;
			long now;
			if (inventorySlot != null) {
				if (inventorySlot.isMultiSlot()) {
					subSlots = inventorySlot.getSubSlots();
					i$ = subSlots.iterator();

					while(i$.hasNext()) {
						ss = (InventorySlot)i$.next();
						now = this.countMap.get(ss.getType()) - (long)ss.count();
						this.countMap.put(ss.getType(), now);
						if (now <= 0L) {
							this.removeFromSlotMap(ss.getType(), desSlot.slot);
						}
					}
				} else {
					now = this.countMap.get(inventorySlot.getType()) - (long)inventorySlot.count();
					this.countMap.put(inventorySlot.getType(), now);
					if (now <= 0L) {
						this.removeFromSlotMap(inventorySlot.getType(), desSlot.slot);
					}
				}

				this.setVolume(this.getVolume() - inventorySlot.getVolume());
			}

			this.inventoryMap.put(desSlot.slot, desSlot);
			if (desSlot.isMultiSlot()) {
				subSlots = desSlot.getSubSlots();
				i$ = subSlots.iterator();

				while(i$.hasNext()) {
					ss = (InventorySlot)i$.next();
					now = this.countMap.get(ss.getType()) + (long)ss.count();
					this.countMap.put(ss.getType(), now);
					if (now > 0L) {
						this.addToSlotMap(ss.getType(), desSlot.slot);
					}
				}
			} else {
				now = this.countMap.get(desSlot.getType()) + (long)desSlot.count();
				this.countMap.put(desSlot.getType(), now);
				if (now > 0L) {
					this.addToSlotMap(desSlot.getType(), desSlot.slot);
				}
			}

			this.setVolume(this.getVolume() + desSlot.getVolume());
		}

		assert this.checkVolumeInt();

		assert this.checkCountAndSlots();
	}

	@Override
	public boolean existsInInventory(short type) {
		return slot != null && slot.getType() == type;
	}

	@Override
	public void addToCountAndSlotMap(InventorySlot slot) throws InventoryExceededException {
		if(this.slot == null) this.slot = slot;
		else {
			if(this.slot.isMultiSlot() && slot.isMultiSlot() && this.slot.isMultiSlotCompatibleTo(slot.getType())) this.slot.mergeMulti(slot, 0);
			else this.slot = null;
		}
	}

	@Override
	public void addToSlotMap(short type, int slot) {
		setType(type);
	}

	public Tag toMetaData() {
		return new Tag(Tag.Type.STRUCT, null, new Tag[] {new Tag(Tag.Type.STRING, "customName", customName), FinishTag.INST});
	}

	public void fromMetaData(Tag tag) {
		Tag[] value = tag.getStruct();
		if(value.length > 0) customName = value[0].getString();
	}

	public short getType(int slot) {
		return !isSlotEmpty(slot) ? getSlot(slot).getType() : 0;
	}

	public void fromTagStructure(Tag tag) {
		if("storage_capsule".equals(tag.getName())) {
			Tag[] value = (Tag[]) tag.getValue();
			fromMetaData(value[0]);
			super.fromTagStructure(value[1]);
		} else super.fromTagStructure(tag);
	}

	public Tag toTagStructure() {
		return new Tag(Tag.Type.STRUCT, "storage_capsule", new Tag[]{toMetaData(), super.toTagStructure(), FinishTag.INST});
	}

	public short getType() {
		return slot.getType();
	}

	public void setType(short type) {
		slot.setType(type);
	}

	public int getCount() {
		return slot.count();
	}

	public void setCount(int count) {
		slot.setCount(count);
	}
	*/
}
