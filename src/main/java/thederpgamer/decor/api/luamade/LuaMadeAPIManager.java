package thederpgamer.decor.api.luamade;

import api.mod.StarLoader;
import luamade.lua.element.block.Block;
import luamade.luawrap.LuaMadeUserdata;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import thederpgamer.decor.api.luamade.projector.HoloProjector;
import thederpgamer.decor.api.luamade.projector.TextProjector;
import thederpgamer.decor.data.drawdata.HoloProjectorDrawData;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.systems.modules.HoloProjectorModule;
import thederpgamer.decor.systems.modules.TextProjectorModule;
import thederpgamer.decor.utils.ProjectorUtils;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class LuaMadeAPIManager {
	public static boolean initialize() {
		if(StarLoader.getModFromName("LuaMade") != null) {
			LuaMadeUserdata.graftMethod(Block.class, "isHoloProjector", new LuaFunction() {
				@Override
				public LuaValue call(LuaValue arg) {
					Block block = (Block) arg.checkuserdata(Block.class);
					return LuaBoolean.valueOf(block.getId() == ElementManager.getBlock("Holo Projector").getId());
				}
			});
			LuaMadeUserdata.graftMethod(Block.class, "getHoloProjector", new LuaFunction() {
				@Override
				public LuaValue call(LuaValue arg) {
					Block block = (Block) arg.checkuserdata(Block.class);
					if(block.getId() == ElementManager.getBlock("Holo Projector").getId()) {
						HoloProjectorDrawData drawData = (HoloProjectorDrawData) ProjectorUtils.getDrawData(block.getSegmentPiece());
						HoloProjectorModule module = (HoloProjectorModule) ProjectorUtils.getModule(block.getSegmentPiece());
						return new HoloProjector(block, drawData, module);
					} else return LuaValue.NIL;
				}
			});
			LuaMadeUserdata.graftMethod(Block.class, "isTextProjector", new LuaFunction() {
				@Override
				public LuaValue call(LuaValue arg) {
					Block block = (Block) arg.checkuserdata(Block.class);
					return LuaBoolean.valueOf(block.getId() == ElementManager.getBlock("Text Projector").getId());
				}
			});
			LuaMadeUserdata.graftMethod(Block.class, "getTextProjector", new LuaFunction() {
				@Override
				public LuaValue call(LuaValue arg) {
					Block block = (Block) arg.checkuserdata(Block.class);
					if(block.getId() == ElementManager.getBlock("Text Projector").getId()) {
						TextProjectorDrawData drawData = (TextProjectorDrawData) ProjectorUtils.getDrawData(block.getSegmentPiece());
						TextProjectorModule module = (TextProjectorModule) ProjectorUtils.getModule(block.getSegmentPiece());
						return new TextProjector(block, drawData, module);
					} else return LuaValue.NIL;
				}
			});
			return true;
		} else return false;
	}
}
