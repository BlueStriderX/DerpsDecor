package thederpgamer.decor.data.drawdata;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import thederpgamer.decor.data.graphics.mesh.SystemMesh;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/03/2022]
 */
public class HoloTableDrawData implements ProjectorInterface {
	public long tableIndex;
	public long targetIndex;
	public Vector3i offset;
	public Vector3i rotation;
	public int scale;
	public boolean changed;
	public transient SystemMesh systemMesh;

	public HoloTableDrawData(long tableIndex, long targetIndex, Vector3i offset, Vector3i rotation, int scale, boolean changed) {
		this.tableIndex = tableIndex;
		this.targetIndex = targetIndex;
		this.offset = offset;
		this.rotation = rotation;
		this.scale = scale;
		this.changed = changed;
	}

	public HoloTableDrawData(SegmentPiece table, SegmentPiece target) {
		assert table != null;
		assert target != null;
		tableIndex = table.getAbsoluteIndex();
		targetIndex = target.getAbsoluteIndex();
		scale = 1;
		offset = new Vector3i();
		rotation = new Vector3i();
		changed = true;
		systemMesh = new SystemMesh(table, target);
	}

	@Override
	public void copyTo(ProjectorInterface drawData) {
		if(drawData instanceof HoloTableDrawData) {
			HoloTableDrawData holoTableDrawData = (HoloTableDrawData) drawData;
			holoTableDrawData.tableIndex = tableIndex;
			holoTableDrawData.targetIndex = targetIndex;
			holoTableDrawData.offset = offset;
			holoTableDrawData.rotation = rotation;
			holoTableDrawData.scale = scale;
			holoTableDrawData.changed = changed;
			holoTableDrawData.systemMesh = systemMesh;
		}
	}
}
