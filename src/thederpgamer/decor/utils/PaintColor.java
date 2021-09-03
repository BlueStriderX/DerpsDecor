package thederpgamer.decor.utils;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.awt.*;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/02/2021
 */
public enum PaintColor {
    ORANGE(238, new Vector3f(Color.decode("0xF08C1A").getColorComponents(new float[3]))),
    RED(239, new Vector3f(Color.decode("0xDB3535").getColorComponents(new float[3]))),
    PURPLE(240, new Vector3f(Color.decode("0xAC56C7").getColorComponents(new float[3]))),
    BROWN(241, new Vector3f(Color.decode("0x915C23").getColorComponents(new float[3]))),
    GREEN(242, new Vector3f(Color.decode("0x2CA128").getColorComponents(new float[3]))),
    YELLOW(243, new Vector3f(Color.decode("0xE3C82D").getColorComponents(new float[3]))),
    BLACK(244, new Vector3f(Color.decode("0x000000").getColorComponents(new float[3]))),
    WHITE(245, new Vector3f(Color.decode("0xFFFFFF").getColorComponents(new float[3]))),
    BLUE(246, new Vector3f(Color.decode("0x2871A8").getColorComponents(new float[3])));

    public short id;
    public Vector4f color;

    PaintColor(int id, Vector3f color) {
        this.id = (short) id;
        this.color = new Vector4f(color.x, color.y, color.z, 1.0f);
    }

    public static PaintColor fromId(short id) {
        for(PaintColor color : PaintColor.values()) if(color.id == id) return color;
        return WHITE;
    }
}
