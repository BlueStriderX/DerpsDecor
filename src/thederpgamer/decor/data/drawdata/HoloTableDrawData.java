package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import thederpgamer.decor.data.graphics.mesh.SystemMesh;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/03/2022]
 */
public class HoloTableDrawData {

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
		//Todo: Load mesh from json?
	}

	public HoloTableDrawData(SegmentPiece table, SegmentPiece target) {
		if(table != null) this.tableIndex = ElementCollection.getIndex4(table.getAbsoluteIndex(), table.getOrientation());
		if(target != null) this.targetIndex = ElementCollection.getIndex4(target.getAbsoluteIndex(), target.getOrientation());
		this.scale = 1;
		this.offset = new Vector3i();
		this.rotation = new Vector3i();
		this.changed = true;
		this.systemMesh = new SystemMesh(table, target);
	}

	public Transform getTransform() {
		return new Transform();
		//return systemMesh.getTransform();
	}
}
