// Views package
package views

// Imports
import geometry.Point
import graphics.Canvas

/**
 * View class. Represents base class for all user controls / views.
 */
open class View
{
    /**
     * Identification string
     */
    var name: String = ""

    /**
     * Width of view
     */
    open var width: Int = 100

    /**
     * Height of view
     */
    open var height: Int = 100

    /**
     * Location of view
     */
    var position: Point = Point(0, 0)

    /**
     * List of all views
     */
    open var views = mutableMapOf<String, View>()

    /**
     * Render view
     */
    open fun render(canvas: Canvas) { }
}