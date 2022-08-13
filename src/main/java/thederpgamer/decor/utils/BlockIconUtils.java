package thederpgamer.decor.utils;

import api.utils.draw.ModWorldDrawer;
import api.utils.textures.StarLoaderTexture;
import api.utils.textures.TextureSwapper;
import com.bulletphysics.linearmath.Transform;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;
import org.schema.common.util.StringTools;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.client.view.gui.GUI3DBlockElement;
import org.schema.game.client.view.tools.SingleBlockDrawer;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.manager.ResourceManager;

import javax.imageio.ImageIO;
import javax.vecmath.Matrix3f;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/30/2022]
 */
public class BlockIconUtils extends ModWorldDrawer {

	private Transform orientation = new Transform();
	private Transform orientationTmp = new Transform();
	private Matrix3f rot = new Matrix3f();
	private Transform mView = new Transform();
	private FloatBuffer fb = BufferUtils.createFloatBuffer(16);
	private float[] ff = new float[16];

	private boolean initialized = false;

	public BlockIconUtils() {
		orientation.setIdentity();
		orientationTmp.setIdentity();
	}

	@Override
	public void update(Timer timer) {

	}

	@Override
	public void cleanUp() {

	}

	@Override
	public boolean isInvisible() {
		return false;
	}

	@Override
	public void onInit() {
		if(initialized) return;
		initialized = true;
		ArrayList<ElementInformation> types = new ArrayList<>();
		for(Block block : ElementManager.getAllBlocks()) types.add(block.getBlockInfo());

		File iconsFolder = new File(DataUtils.getWorldDataPath() + "/block-icons");
		if(!iconsFolder.exists()) iconsFolder.mkdirs();

		for(final ElementInformation e : types) {
			if(e.getName().contains("environmental")) continue;
			final FrameBufferObjects fbo = new FrameBufferObjects(e.getName(), 64, 64);
			try {
				fbo.initialize();
			} catch(GLException exception) {
				exception.printStackTrace();
			}
			fbo.enable();
			GL11.glClearColor(0, 0, 0, 0);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
			GL11.glViewport(0, 0, 64, 64);
			GlUtil.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GlUtil.glDisable(GL11.GL_DEPTH_TEST);

			GUI3DBlockElement.setMatrix();
			Matrix4f modelviewMatrix = Controller.modelviewMatrix;
			fb.rewind();
			modelviewMatrix.store(fb);
			fb.rewind();
			fb.get(ff);
			mView.setFromOpenGLMatrix(ff);
			mView.origin.set(0, 0, 0);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			//GUIElement.enableOrthogonal3d(1024, 1024);
			GUIElement.enableOrthogonal3d(64, 64);
			GlUtil.glPushMatrix();
			//GlUtil.translateModelview(32 + (x * 64), 32 + (y * 64), 0f);
			GlUtil.translateModelview(32, 32, 0f);
			GlUtil.scaleModelview(32f, -32f, 32f);
			if(e.getBlockStyle() == BlockStyle.SPRITE) {
				orientationTmp.basis.set(mView.basis);
				mView.basis.setIdentity();
			} else {
				rot.set(orientation.basis);
				mView.basis.mul(rot);
			}

			GlUtil.glMultMatrix(mView);
			if(e.getBlockStyle() == BlockStyle.SPRITE) mView.basis.set(orientationTmp.basis);
			SingleBlockDrawer drawer = new SingleBlockDrawer();
			drawer.setLightAll(false);
			GlUtil.glPushMatrix();
			if(e.getBlockStyle() != BlockStyle.NORMAL) GlUtil.rotateModelview((Float) EngineSettings.ICON_BAKERY_BLOCKSTYLE_ROTATE_DEG.getCurrentState(), 0, 1, 0);
			GlUtil.rotateModelview(45.0f / 2.0f, 1, 0, 0);
			GlUtil.rotateModelview(45, 0, -1, 0);
			if(e.isController()) GlUtil.rotateModelview(180, 0, 1, 0);
			drawer.drawType(e.getId());
			GlUtil.glPopMatrix();
			GUIElement.disableOrthogonal();
			AbstractScene.mainLight.draw();
			GlUtil.glDisable(GL11.GL_NORMALIZE);
			GlUtil.glEnable(GL11.GL_DEPTH_TEST);

			try {
				String path = DataUtils.getWorldDataPath() + "/block-icons/" + e.getName().toLowerCase().replaceAll(" ", "-") + "-icon";
				if(e.isReactorChamberAny()) {
					Block root = ElementManager.getBlock((short) e.chamberRoot);
					if(root != null) path = root.getBlockInfo().getName().toLowerCase().replaceAll(" ", "-") + "-icon";
				}

				final File outputFile = new File(path + ".png");
				if(!outputFile.exists()) outputFile.createNewFile();
				GlUtil.writeScreenToDisk(path, "png", 64, 64, 4, fbo);
				StarLoaderTexture.runOnGraphicsThread(new Runnable() {
					@Override
					public void run() {
						try {
							BufferedImage image = ImageIO.read(outputFile);
							StarLoaderTexture texture = StarLoaderTexture.newIconTexture(image);
							ResourceManager.setBlockIconTexture(e, texture);
							//e.setBuildIconNum(texture.getTextureId());
						} catch(Exception exception) {
							exception.printStackTrace();
						}
					}
				});
			} catch(Exception ex) {
				ex.printStackTrace();
			}

			fbo.disable();
			fbo.cleanUp();
		}
		GL11.glViewport(0, 0, GLFrame.getWidth(), GLFrame.getHeight());
	}

	private Sprite findSprite(int buildIconNum) {
		System.out.println(buildIconNum);
		try {
			int sheetNum = buildIconNum / 256;
			Sprite sheet = TextureSwapper.getSpriteFromName("build-icons-" + StringTools.formatTwoZero(sheetNum) + "-16x16-gui-");
			sheet.setSelectedMultiSprite(buildIconNum % 16);
			File temp = new File(DataUtils.getWorldDataPath() + "/block-icons/build-icons-" + StringTools.formatTwoZero(sheetNum) + "-16x16-gui-.png");
			if(!temp.exists()) temp.createNewFile();
			ImageIO.write(TextureSwapper.getImageFromSprite(sheet), "png", temp);
			return sheet;
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}
}
