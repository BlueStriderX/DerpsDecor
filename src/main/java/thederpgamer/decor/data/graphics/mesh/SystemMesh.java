package thederpgamer.decor.data.graphics.mesh;

import api.utils.game.SegmentControllerUtils;
import com.bulletphysics.linearmath.Transform;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.lwjgl.opengl.GL11;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.util.FastCopyLongOpenHashSet;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.DrawableScene;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.decor.utils.SegmentPieceUtils;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/03/2022]
 */
public class SystemMesh implements Drawable, Shaderable {
	private final transient ArrayList<Vector3f> points = new ArrayList<>();
	private final transient Vector4f lineColor = new Vector4f(0.15F, 0.15F, 0.15F, 1.0F);
	private final transient Vector4f faceColor = new Vector4f(0.15F, 0.15F, 0.15F, 0.5F);
	private transient boolean initialized;
	private final transient SegmentPiece table;
	private final transient SegmentPiece target;

	public SystemMesh(SegmentPiece table, SegmentPiece target) {
		assert table != null;
		assert target != null;
		this.table = table;
		this.target = target;
		createMesh();
	}

	private void createMesh() {
		HashMap<Short, LongArrayList> longs = new HashMap<>();
		try {
			if(SegmentPieceUtils.isControlling(table, target) && table.getSegmentController().getId() == target.getSegmentController().getId()) {
				for(ElementCollectionManager<?, ?, ?> cm : SegmentControllerUtils.getAllCollectionManagers((ManagedUsableSegmentController<?>) table.getSegmentController())) {
					if(cm.getEnhancerClazz() == target.getType()) {
						FastCopyLongOpenHashSet l = cm.rawCollection;
						for(long l2 : l) {
							if(!longs.containsKey(target.getType())) longs.put(target.getType(), new LongArrayList());
							longs.get(target.getType()).add(l2);
						}
					}
				}
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		ArrayList<Vector3f> tempPoints = new ArrayList<>();
		for(Map.Entry<Short, LongArrayList> entry : longs.entrySet()) {
			for(Long l : entry.getValue()) {
				SegmentPiece segmentPiece = table.getSegmentController().getSegmentBuffer().getPointUnsave(l);
				if(segmentPiece != null && segmentPiece.getType() != 0) tempPoints.add(new Vector3f(segmentPiece.x, segmentPiece.y, segmentPiece.z));
			}
		}
		//Optimize points and remove any points that aren't a corner, edge, or vertex
		ArrayList<Vector3f> optimizedPoints = new ArrayList<>();
		for(int i = 0; i < tempPoints.size(); i++) {
			Vector3f point = tempPoints.get(i);
			int count = 0;
			for(int j = 0; j < tempPoints.size(); j++) {
				if(i == j) continue;
				Vector3f point2 = tempPoints.get(j);
				if(point.x == point2.x && point.y == point2.y && point.z == point2.z) count++;
			}
			if(count < 3) optimizedPoints.add(point);
		}
		points.addAll(optimizedPoints);
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public void draw() {
		if(!initialized) onInit();
		//Go through the points and draw lines between them
		//Shade the faces in between with a semi transparent color
		GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		for(Vector3f point : points) {
			//Draw line
			GlUtil.glPushMatrix();
			GlUtil.glTranslatef(point.x, point.y, point.z);
			GlUtil.glColor4f(lineColor.x, lineColor.y, lineColor.z, lineColor.w);
			GlUtil.glBegin(1);
			for(Vector3f point2 : points) {
				if(point.x == point2.x && point.y == point2.y && point.z == point2.z) continue;
				GL11.glVertex3f(0.0F, 0.0F, 0.0F);
				GL11.glVertex3f(point2.x - point.x, point2.y - point.y, point2.z - point.z);
			}
			GlUtil.glEnd();
			GlUtil.glPopMatrix();
			//Draw face
			GlUtil.glPushMatrix();
			GlUtil.glTranslatef(point.x, point.y, point.z);
			GlUtil.glColor4f(faceColor.x, faceColor.y, faceColor.z, faceColor.w);
			GlUtil.glBegin(4);
			for(Vector3f point2 : points) {
				if(point.x == point2.x && point.y == point2.y && point.z == point2.z) continue;
				GL11.glVertex3f(0.0F, 0.0F, 0.0F);
				GL11.glVertex3f(point2.x - point.x, point2.y - point.y, point2.z - point.z);
				GL11.glVertex3f(point2.x - point.x, point2.y - point.y, point2.z - point.z);
			}
			GlUtil.glEnd();
			GlUtil.glPopMatrix();
		}
	}

	@Override
	public boolean isInvisible() {
		return false;
	}

	@Override
	public void onInit() {
		if(!initialized) {
			initialized = true;
			createMesh();
			Transform transform = new Transform();
			table.getTransform(transform);
			Vector3f upVector = GlUtil.getUpVector(new Vector3f(), transform);
			transform.origin.add(upVector);
			//Scale model so it's localized above the table
			//The actual model can be slightly larger than the table itself, 1.5x the size of the table is a good size
			//Scale each point so it's relative to the table before drawing the model
			Vector3f modelMin = new Vector3f();
			Vector3f modelMax = new Vector3f();
			for(Vector3f point : points) {
				Vector3f pointVector = new Vector3f(point.x, point.y, point.z);
				pointVector.sub(transform.origin);
				pointVector.scale(1.5F);
				pointVector.add(transform.origin);
				if(pointVector.x < modelMin.x) modelMin.x = pointVector.x;
				if(pointVector.y < modelMin.y) modelMin.y = pointVector.y;
				if(pointVector.z < modelMin.z) modelMin.z = pointVector.z;
				if(pointVector.x > modelMax.x) modelMax.x = pointVector.x;
				if(pointVector.y > modelMax.y) modelMax.y = pointVector.y;
				if(pointVector.z > modelMax.z) modelMax.z = pointVector.z;
			}
			Vector3f modelSize = new Vector3f();
			modelSize.sub(modelMax, modelMin);
			Vector3f modelCenter = new Vector3f();
			modelCenter.add(modelMin, modelMax);
			modelCenter.scale(0.5F);
			for(Vector3f point : points) {
				//First, localize the points so they all fit with in the blocks size (1x1x1)
				Vector3f pointVector = new Vector3f(point.x, point.y, point.z);
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
				pointVector.add(transform.origin);
				point.x = (int) pointVector.x;
				point.y = (int) pointVector.y;
				point.z = (int) pointVector.z;
			}
		}
	}

	@Override
	public void onExit() {
	}

	@Override
	public void updateShader(DrawableScene drawableScene) {
	}

	@Override
	public void updateShaderParameters(Shader shader) {
	}
}
