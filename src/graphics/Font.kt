// Graphics package
package graphics

import org.lwjgl.nanovg.NanoVG

/**
 * Manages fonts
 */
class Font(var name: String, var size: Int)
{
    // Companion object for static class members
    companion object
    {
        // Load all fonts
        @JvmStatic
        internal fun loadFonts(context: Long)
        {
            // Load Segoe UI
            NanoVG.nvgCreateFont(context, "Segoe UI", "src/res/segoeui.ttf")
        }
    }
}