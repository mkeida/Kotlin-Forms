// View package
package views

// Imports
import system.*
import geometry.*
import graphics.*
import graphics.paint.RadialGradient
import graphics.paint.Solid

/**
 * Button class
 */
open class Button : View()
{
    /**
     * Button text
     */
    var text: String = "Button"

    /**
     * Render button
     */
    override fun render(canvas: Canvas)
    {
        // Call parent render method
        super.render(canvas)

        // Render client rectangle
        val gradientStroke = RadialGradient(
            Window.cursor.x,
            Window.cursor.y,
            0,
            150,
            Color(150, 150, 150),
            Color.white
        )

        canvas.renderRectangle(position, width, height, Solid(Color.white), gradientStroke, 6, 0, 5, 0, 0)

        // Render text
        var font = Font("Segoe UI", 20)

        canvas.renderText(text, Point(position.x + (width / 2), position.y + (height / 2) + (font.size / 4)), font, Solid(Color.black))
    }

    /**
     * Adds view to window views collection
     */
    fun add(window: Window)
    {
        window.views[name] = this
    }
}