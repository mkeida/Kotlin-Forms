// Graphics package
package graphics

// Imports
import geometry.Point
import graphics.paint.LinearGradient
import graphics.paint.Paint
import graphics.paint.RadialGradient
import graphics.paint.Solid
import org.lwjgl.nanovg.NanoVG.*

/**
 * Canvas class. Represents drawing area.
 */
class Canvas(var context: Long)
{
    /**
     * Draws rectangle
     * @param position Rectangle position
     * @param width Rectangle width
     * @param height Rectangle height
     * @param
     */
    fun renderRectangle(position: Point, width: Int, height: Int, fill: Paint, stroke: Paint = Solid(Color("#FFFFFF")), strokeWidth: Int = 0, rad1: Int = 0, rad2: Int = 0, rad3: Int = 0, rad4: Int = 0)
    {
        // Begin new path
        nvgBeginPath(context)

        // Check fill type
        if (fill is Solid)
        {
            // Solid fill
            nvgFillColor(context, fill.color.nvgColor)
        }
        else if (fill is LinearGradient)
        {
            // Linear gradient
            nvgLinearGradient(context,
                fill.sx.toFloat(),
                fill.sy.toFloat(),
                fill.ex.toFloat(),
                fill.ey.toFloat(),
                fill.color1.nvgColor,
                fill.color2.nvgColor,
                fill.NVGPaint
            )

            // Render rectangle with given paint fill
            nvgFillPaint(context, fill.NVGPaint)
        }
        else if (fill is RadialGradient)
        {
            // Radial gradient
            nvgRadialGradient(
                context,
                fill.cx.toFloat(),
                fill.cy.toFloat(),
                fill.rad1.toFloat(),
                fill.rad2.toFloat(),
                fill.color1.nvgColor,
                fill.color2.nvgColor,
                fill.NVGPaint
            )

            // Render rectangle with given stroke paint
            nvgFillPaint(context, fill.NVGPaint)
        }

        // Draw rectangle with given parameters
        nvgRoundedRectVarying(
            context,
            position.x.toFloat(),
            position.y.toFloat(),
            width.toFloat(),
            height.toFloat(),
            rad1.toFloat(),
            rad2.toFloat(),
            rad3.toFloat(),
            rad4.toFloat()
        )

        // Check stroke type
        if (stroke is Solid)
        {
            // Solid fill
            nvgStrokeColor(context, stroke.color.nvgColor)
        }
        else if (stroke is LinearGradient)
        {
            // Linear gradient
            nvgLinearGradient(
                context,
                stroke.sx.toFloat(),
                stroke.sy.toFloat(),
                stroke.ex.toFloat(),
                stroke.ey.toFloat(),
                stroke.color1.nvgColor,
                stroke.color2.nvgColor,
                stroke.NVGPaint
            )

            // Render rectangle with given stroke paint
            nvgStrokePaint(context, stroke.NVGPaint)
        }
        else if (stroke is RadialGradient)
        {
            // Radial gradient
            nvgRadialGradient(
                context,
                stroke.cx.toFloat(),
                stroke.cy.toFloat(),
                stroke.rad1.toFloat(),
                stroke.rad2.toFloat(),
                stroke.color1.nvgColor,
                stroke.color2.nvgColor,
                stroke.NVGPaint
            )

            // Render rectangle with given stroke paint
            nvgStrokePaint(context, stroke.NVGPaint)
        }

        // Stroke width
        nvgStrokeWidth(context, strokeWidth.toFloat())

        // Render stroke
        nvgStroke(context)

        // Render rectangle
        nvgFill(context)
    }

    /**
     * Draws text
     */
    fun renderText(text: String, position: Point, font: Font, fillPaint: Solid)
    {
        // Begin new path
        nvgBeginPath(context)

        // Draw text
        nvgTextAlign(context, 1 shl 1)
        nvgFontSize(context, font.size.toFloat())
        nvgFontFace(context, font.name)
        nvgFillColor(context, fillPaint.color.nvgColor)
        nvgText(context, position.x.toFloat(), position.y.toFloat(), text)

        // Draw our rectangle
        nvgFill(context)
    }
}