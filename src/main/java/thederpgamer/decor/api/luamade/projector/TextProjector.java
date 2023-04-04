package thederpgamer.decor.api.luamade.projector;

import luamade.lua.data.LuaVec3i;
import luamade.lua.element.block.Block;
import luamade.luawrap.LuaMadeCallable;
import luamade.luawrap.LuaMadeUserdata;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.systems.modules.TextProjectorModule;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class TextProjector extends LuaMadeUserdata {
	private final Block block;
	private final TextProjectorDrawData drawData;
	private final TextProjectorModule module;

	public TextProjector(Block block, TextProjectorDrawData drawData, TextProjectorModule module) {
		this.block = block;
		this.drawData = drawData;
		this.module = module;
	}

	@LuaMadeCallable
	public Block getBlock() {
		return block;
	}

	@LuaMadeCallable
	public LuaVec3i getOffset() {
		return new LuaVec3i(drawData.offset);
	}

	@LuaMadeCallable
	public void setOffset(LuaVec3i offset) {
		drawData.offset.x = offset.x;
		drawData.offset.y = offset.y;
		drawData.offset.z = offset.z;
		module.setDrawData(drawData.indexAndOrientation, drawData);
	}

	@LuaMadeCallable
	public LuaVec3i getRotation() {
		return new LuaVec3i(drawData.rotation);
	}

	@LuaMadeCallable
	public void setRotation(LuaVec3i rotation) {
		drawData.rotation.x = rotation.x;
		drawData.rotation.y = rotation.y;
		drawData.rotation.z = rotation.z;
		module.setDrawData(drawData.indexAndOrientation, drawData);
	}

	@LuaMadeCallable
	public Integer getScale() {
		return drawData.scale;
	}

	@LuaMadeCallable
	public void setScale(Integer scale) {
		drawData.scale = scale;
		module.setDrawData(drawData.indexAndOrientation, drawData);
	}

	@LuaMadeCallable
	public Boolean isHolographic() {
		return drawData.holographic;
	}

	@LuaMadeCallable
	public void setHolographic(Boolean holographic) {
		drawData.holographic = holographic;
		module.setDrawData(drawData.indexAndOrientation, drawData);
	}

	@LuaMadeCallable
	public String getText() {
		return drawData.text;
	}

	@LuaMadeCallable
	public void setText(String text) {
		drawData.text = text;
		module.setDrawData(drawData.indexAndOrientation, drawData);
	}

	@LuaMadeCallable
	public String getColor() {
		return drawData.color;
	}

	@LuaMadeCallable
	public void setColor(String color) {
		drawData.color = color;
		module.setDrawData(drawData.indexAndOrientation, drawData);
	}
}
