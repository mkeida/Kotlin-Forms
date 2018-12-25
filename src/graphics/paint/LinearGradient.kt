// Graphics package
package graphics.paint

// Imports
import graphics.Color
import org.lwjgl.nanovg.NVGPaint

/**
 * Represents linear gradient fill paint
 */
class LinearGradient(var sx: Int, var sy: Int, var ex: Int, var ey: Int, var color1: Color, var color2: Color, internal var NVGPaint : NVGPaint? = org.lwjgl.nanovg.NVGPaint.create()) : Paint()