// Graphics package
package graphics.paint

// Imports
import graphics.Color
import org.lwjgl.nanovg.NVGPaint

/**
 * Represents radial gradient fill paint
 */
class RadialGradient(var cx: Int, var cy: Int, var rad1: Int, var rad2: Int, var color1: Color, var color2: Color, internal var NVGPaint : NVGPaint? = org.lwjgl.nanovg.NVGPaint.create()) : Paint()