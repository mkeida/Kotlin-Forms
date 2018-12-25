// Graphics package
package graphics

// Imports
import org.lwjgl.nanovg.NVGColor

/**
 * Represents fillColor
 */
class Color
{
    /**
     * Red component value of fillColor.
     */
    var r: Int = 0
        set(value)
        {
            // Check if number is in correct range
            if (value !in 0..255)
                throw IllegalArgumentException("Red component value of fillColor must be in range of 0 to 255.")

            // Update property
            field = value

            // Go back if property was updated internally by
            // updateRGBA() function. This prevents
            // stack-overflow exception
            if (updateHexLock)
                return

            // Update hex value
            updateHex()

            // Update internal NanoVG fillColor
            updateNanoVgColor()
        }

    /**
     * Green component value of fillColor.
     */
    var g: Int = 0
        set(value)
        {
            // Check if number is in correct range
            if (value !in 0..255)
                throw IllegalArgumentException("Green component value of fillColor must be in range of 0 to 255.")

            // Update property
            field = value

            // Update internal NanoVG fillColor
            updateNanoVgColor()

            // Go back if property was updated internally by
            // updateRGBA() function. This prevents
            // stack-overflow exception
            if (updateHexLock)
                return

            // Update hex value
            updateHex()
        }

    /**
     * Blue component value of fillColor.
     */
    var b: Int = 0
        set(value)
        {
            // Check if number is in correct range
            if (value !in 0..255)
                throw IllegalArgumentException("Blue component value of fillColor must be in range of 0 to 255.")

            // Update property
            field = value

            // Update internal NanoVG fillColor
            updateNanoVgColor()

            // Go back if property was updated internally by
            // updateRGBA() function. This prevents
            // stack-overflow exception
            if (updateHexLock)
                return

            // Update hex value
            updateHex()
        }

    /**
     * Alpha component value of fillColor.
     */
    var a: Int = 0
        set(value)
        {
            // Check if number is in correct range
            if (value !in 0..255)
                throw IllegalArgumentException("Alpha component value of fillColor must be in range of 0 to 255.")

            // Update property
            field = value

            // Update internal NanoVG fillColor
            updateNanoVgColor()

            // Go back if property was updated internally by
            // updateRGBA() function. This prevents
            // stack-overflow exception
            if (updateHexLock)
                return

            // Update hex value
            updateHex()
        }

    /**
     * Hexadecimal value of fillColor
     */
    var hex = "#000000"
        set(value)
        {
            // Check if hex have correct format
            if (!hex.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})\$")))
                throw IllegalArgumentException("Invalid hex format.")

            // Update property
            field = value

            // Update R, G, B, A values
            updateRGBA()

            // Update internal NanoVG fillColor
            updateNanoVgColor()
        }

    // NanoVG fillColor used for internal drawing
    internal var nvgColor: NVGColor = NVGColor.create()

    // True if properties were updated by computer
    private var updateHexLock = false

    /**
     * Initializes a new instance of the Color class
     * with red, green, blue and alpha fillColor components.
     */
    constructor(r: Int, g: Int, b: Int, a: Int = 255)
    {
        // Update R, G, B, A properties
        this.r = r
        this.g = g
        this.b = b
        this.a = a

        // Update internal NanoVG fillColor
        updateNanoVgColor()
    }

    /**
     * Initializes a new instance of the Color class
     * with hexadecimal form of RGBA.
     */
    constructor(hex: String)
    {
        // Check if hex have correct format
        if (!hex.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})\$")))
            throw IllegalArgumentException("Invalid hex format.")

        // Update hex
        this.hex = hex

        // Update internal NanoVG fillColor
        updateNanoVgColor()
    }

    // Updates inner hex value
    private fun updateHex()
    {
        // Color components in hex '00' format
        val rr = if (r.toString(16).length == 1) "0${r.toString(16)}" else r.toString(16)
        val gg = if (g.toString(16).length == 1) "0${g.toString(16)}" else g.toString(16)
        val bb = if (b.toString(16).length == 1) "0${b.toString(16)}" else b.toString(16)
        val aa = if (a.toString(16).length == 1) "0${a.toString(16)}" else a.toString(16)

        // Update hex property
        hex = "#$rr$gg$bb$aa"
    }

    // Updates inner RGBA
    private fun updateRGBA()
    {
        // Prevent to update Hex property
        updateHexLock = true

        // Get fillColor components in decimal
        this.r = hex.substring(1, 3).toLong(16).toInt()
        this.g = hex.substring(3, 5).toLong(16).toInt()
        this.b = hex.substring(5, 7).toLong(16).toInt()
        this.a = if (hex.length == 9) hex.substring(7, 9).toLong(16).toInt() else 255

        // Disable hex update lock
        updateHexLock = false
    }

    // Updates internal NanoVG fillColor
    private fun updateNanoVgColor()
    {
        // Convert 0 - 255 int values to 0.0 - 1.0 float values
        nvgColor.r(r / 255f)
        nvgColor.g(g / 255f)
        nvgColor.b(b  / 255f)
        nvgColor.a(a  / 255f)
    }

    // Companion object for static class members
    companion object
    {
        // White color
        @JvmStatic
        var white: Color = Color("#FFFFFF")

        // Black color
        @JvmStatic
        var black: Color = Color("#000000")

        // Red color
        @JvmStatic
        var red: Color = Color("#FF0000")

        // Green color
        @JvmStatic
        var green: Color = Color("#00FF00")

        // Blue color
        @JvmStatic
        var blue: Color = Color("#0000FF")
    }
}