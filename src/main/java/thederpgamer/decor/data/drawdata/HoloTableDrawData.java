package thederpgamer.decor.data.drawdata;

import api.common.GameClient;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.ElementCollectionDrawer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementCollectionMesh;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Vector4f;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/03/2022]
 */
public class HoloTableDrawData implements ProjectorInterface {
	public long entityId;
	public long tableIndex;
	public long targetIndex;
	public boolean changed;
	public Vector4f color = new Vector4f(0.0F, 0.35F, 1.0F, 0.8F);
	private boolean canDraw = true;
	private transient ElementCollection<?, ?, ?> tableCollection;
	private transient ElementCollectionMesh mesh;

	public HoloTableDrawData(long entityId, long tableIndex, long targetIndex, Vector3i offset, Vector3i rotation, int scale, boolean changed) {
		this.tableIndex = tableIndex;
		this.targetIndex = targetIndex;
		this.changed = changed;
		this.entityId = entityId;
		SegmentPiece table = Objects.requireNonNull(SegmentPieceUtils.getEntityFromDbId(entityId)).getSegmentBuffer().getPointUnsave(tableIndex);
		SegmentPiece target = Objects.requireNonNull(SegmentPieceUtils.getEntityFromDbId(entityId)).getSegmentBuffer().getPointUnsave(targetIndex);
		createMesh(table, table);
		changed = true;
	}

	public HoloTableDrawData(SegmentPiece table, SegmentPiece target) {
		assert table != null;
		assert target != null;
		entityId = table.getSegmentController().dbId;
		tableIndex = table.getAbsoluteIndex();
		targetIndex = target.getAbsoluteIndex();
		changed = true;
		createMesh(table, target);
		changed = true;
	}

	private void createMesh(SegmentPiece table, SegmentPiece target) {
		try {
			tableCollection = SegmentPieceUtils.getElementCollectionFromPiece(target);
			if(tableCollection == null) {
				System.err.println("Table collection is null!");
				return;
			}
			mesh = tableCollection.getMesh();
			if(mesh == null) {
				System.err.println("Mesh is null!");
				mesh = ElementCollection.getMeshInstance();
			}
			mesh.calculate(tableCollection, 0L, tableCollection.getNeighboringCollectionUnsave());
			mesh.initializeMesh();
			mesh.setColor(color);
			mesh.markDraw();
			ElementCollectionDrawer.MContainerDrawJob drawJob = new ElementCollectionDrawer.MContainerDrawJob();
			drawJob.register((ManagedSegmentController<?>) table.getSegmentController());
			ElementCollectionDrawer drawer = GameClient.getClientState().getWorldDrawer().getSegmentDrawer().getElementCollectionDrawer();
			Field drawMap = drawer.getClass().getDeclaredField("drawMap");
			drawMap.setAccessible(true);
			Object2ObjectOpenHashMap<SegmentController, ElementCollectionDrawer.MContainerDrawJob> drawMapObject = (Object2ObjectOpenHashMap<SegmentController, ElementCollectionDrawer.MContainerDrawJob>) drawMap.get(drawer);
			drawMapObject.put(table.getSegmentController(), drawJob);
			drawMap.set(drawer, drawMapObject);
			Field toDraw = drawer.getClass().getDeclaredField("toDraw");
			toDraw.setAccessible(true);
			List<ElementCollection<?, ?, ?>> toDrawList = (List<ElementCollection<?, ?, ?>>) toDraw.get(drawer);
			toDrawList.add(tableCollection);
			toDraw.set(drawer, toDrawList);
			/*
			Field triangles = mesh.getClass().getDeclaredField("triangles");
			triangles.setAccessible(true);
			float[] triangleArray = (float[]) triangles.get(mesh);
			float[] newArray = new float[triangleArray.length];
			Vector3f pointVector = new Vector3f();
			Transform tableTransform = new Transform();
			table.getTransform(tableTransform);
			Vector3f upVector = GlUtil.getUpVector(new Vector3f(), tableTransform);
			tableTransform.origin.add(upVector);
			//Scale model so it's localized above the table
			//The actual model can be slightly larger than the table itself, 1.5x the size of the table is a good size
			//Scale each point so it's relative to the table before drawing the model
			Vector3f modelMin = new Vector3f();
			Vector3f modelMax = new Vector3f();
			for(int i = 0; i < triangleArray.length - 3; i += 3) {
				pointVector.set(triangleArray[i], triangleArray[i + 1], triangleArray[i + 2]);
				pointVector.sub(tableTransform.origin);
				pointVector.scale(1.5F);
				pointVector.add(tableTransform.origin);
				if(pointVector.x < modelMin.x) modelMin.x = pointVector.x;
				if(pointVector.y < modelMin.y) modelMin.y = pointVector.y;
				if(pointVector.z < modelMin.z) modelMin.z = pointVector.z;
				if(pointVector.x > modelMax.x) modelMax.x = pointVector.x;
				if(pointVector.y > modelMax.y) modelMax.y = pointVector.y;
				if(pointVector.z > modelMax.z) modelMax.z = pointVector.z;
				newArray[i] = pointVector.x;
				newArray[i + 1] = pointVector.y;
				newArray[i + 2] = pointVector.z;
			}
			Vector3f modelSize = new Vector3f();
			modelSize.sub(modelMax, modelMin);
			Vector3f modelCenter = new Vector3f();
			modelCenter.add(modelMin, modelMax);
			modelCenter.scale(0.5F);
			Vector3f segmentOffset = new Vector3f(Segment.HALF_DIM, Segment.HALF_DIM, Segment.HALF_DIM);
			for(int i = 0; i < triangleArray.length - 3; i += 3) {
				//First, localize the points, so they all fit with in the blocks size (1x1x1)
				pointVector.set(newArray[i], newArray[i + 1], newArray[i + 2]);
				pointVector.sub(modelCenter);
				//Find the largest dimension of the model
				float largestDimension = modelSize.x;
				if(modelSize.y > largestDimension) largestDimension = modelSize.y;
				if(modelSize.z > largestDimension) largestDimension = modelSize.z;
				//Scale the points so they fit within the blocks size (1x1x1)
				pointVector.scale(1.0F / largestDimension);
				//Then, move the points so they're centered within the blocks size (1x1x1)
				pointVector.add(new Vector3f(0.5F, 0.5F, 0.5F));
				//Finally, scale the points so they're the correct size for the table
				pointVector.scale(1.5F);
				//Move the points so they're relative to the table
				pointVector.add(tableTransform.origin);
				pointVector.add(segmentOffset);
				newArray[i] = pointVector.x;
				newArray[i + 1] = pointVector.y;
				newArray[i + 2] = pointVector.z;
			}
			triangles.set(mesh, newArray);

			 */
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void copyTo(ProjectorInterface drawData) {
		if(drawData instanceof HoloTableDrawData) {
			HoloTableDrawData holoTableDrawData = (HoloTableDrawData) drawData;
			holoTableDrawData.tableIndex = tableIndex;
			holoTableDrawData.targetIndex = targetIndex;
			holoTableDrawData.changed = changed;
		}
	}

	public boolean canDraw() {
		return canDraw && mesh != null;
	}

	public void draw() {
		if(!canDraw) return;
		if(changed) {
			changed = false;
			SegmentPiece table = Objects.requireNonNull(SegmentPieceUtils.getEntityFromDbId(entityId)).getSegmentBuffer().getPointUnsave(tableIndex);
			SegmentPiece target = Objects.requireNonNull(SegmentPieceUtils.getEntityFromDbId(entityId)).getSegmentBuffer().getPointUnsave(targetIndex);
			color.set(SegmentPieceUtils.getConnectedColor(table));
			createMesh(table, target);
		}
		if(mesh != null) {
			mesh.markDraw();
			mesh.draw();
		}
	}

	public void setCanDraw(boolean draw) {
		canDraw = draw;
	}
}
