// System package
package system

// Imports
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL2.*
import graphics.Font
import graphics.Canvas
import geometry.Point
import views.*

/**
 * Window class. Represents window.
 */
// Window class
open class Window : View()
{
    // Window handle
    internal var handle: Long = NULL

    // NanoVG handle
    private var context: Long = NULL

    // True if glfw window size should not be updated
    // on width and height properties change
    private var windowSizeLock = false

    // True if application window was resized
    private var windowResized = false

    // List of all tasks to be performed by this window
    private val taskQueue = ArrayList<Task>()

    // Collection of new tasks which will be added after taskQueue iteration
    private val tasksToAdd = ArrayList<Task>()

    // List of all views
    override var views = mutableMapOf<String, View>()

    // Task iterator
    private var iterator = taskQueue.iterator()

    /**
     * Swap interval. Delay between buffer swap.
     */
    var swapInterval: Int = 1
        set(value)
        {
            // Update property
            field = value

            // Update window interval
            updateSwapIntervalRequest(this)
        }

    /**
     * True if window is focused.
     */
    var focused: Boolean = false
        private set

    /**
     * State of window.
     */
    var windowState: WindowStates = WindowStates.HIDDEN
        private set

    /**
     * Window title.
     */
    var title: String = "Window"
        set(value)
        {
            // Update property
            field = value

            // Updates window title
            Application.updateWindowTitleRequest(this)
        }

    /**
     * Client area / frame-buffer width.
     */
    override var width: Int = 640
        set(value)
        {
            // Update property
            field = value

            // Size lock is needed when window size
            // is changed by the user using borders
            // of the window. Otherwise would window thread
            // end in endless loop
            if (windowSizeLock)
                return

            // Update window
            Application.updateWindowSizeRequest(this)
        }

    /**
     * Client area / frame-buffer height.
     */
    override var height: Int = 480
        set(value)
        {
            // Update property
            field = value

            // Size lock is needed when window size
            // is changed by the user using borders
            // of the window. Otherwise would window thread
            // end in endless loop
            if (windowSizeLock)
                return

            // Update window
            Application.updateWindowSizeRequest(this)
        }

    /**
     * Window / non-client area width
     */
    var wWidth = width
        private set

    /**
     * Window / non-client area height
     */
    var wHeight = height
        private set

    /**
     * Frames per second processed by the window.
     * Updated every one second.
     */
    var fps: Int = 0
        private set

    /**
     * Updates per second executed by the window.
     * Updated every one second.
     */
    var ups: Int = 0
        private set

    // Initialization
    init
    {
        // Make GLWF window creation request
        Application.createWindowRequest(this)

        // Add window to windows collection
        Application.addWindow(this)
    }

    // Main loop function
    internal fun loop()
    {
        // Make context current
        // Each thread can have only one context current
        glfwMakeContextCurrent(handle)

        // This line is critical for LWJGL's inter-operation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use
        GL.createCapabilities()

        // Set v-sync
        glfwSwapInterval(swapInterval)

        // Initialize NanoVG drawing context
        context = nvgCreate(NVG_STENCIL_STROKES)

        // Check if context was initialized properly
        if (context == NULL)
            throw RuntimeException("Unable to initialize NanoVG")

        // Load fonts
        Font.loadFonts(context)

        // Speed lock variables
        // Last time from update
        var lastTime = System.nanoTime()

        // Temporary fps per second (fps)
        var tempFrames = 0

        // Temporary ups per second (ups)
        var tempUpdates = 0

        // Delta
        var delta = 0.0

        // Timer which triggers once in one second
        var timer = System.currentTimeMillis()

        // Window background color
        GL11.glClearColor(0.9f, 0.9f, 0.9f, 1f)

        // Check if user closed the window
        while (!glfwWindowShouldClose(handle))
        {
            // Current time in nanoseconds
            var now = System.nanoTime()

            // Increment delta
            delta += (now - lastTime) / (1000000000.0 / 60.0)

            // Update last time to current time
            lastTime = now

            // Is delta bigger than one?
            // Do we want to update?
            if (delta >= 1.0)
            {
                // Do update
                update()

                // Increment temporary ups
                tempUpdates++

                // Decrement delta
                delta--
            }

            // Do render
            // We always want to render to prevent flicker
            render()

            // Increment temporary fps
            tempFrames++

            // Once every one second
            if (System.currentTimeMillis() - timer > 1000)
            {
                // Update FPS and UPS
                fps = tempFrames
                ups = tempUpdates

                // Increment timer by one second
                timer += 1000

                // Reset fps and ups
                tempFrames = 0
                tempUpdates = 0
            }
        }

        // Loop ended, window was closed
        // Destroy NanoVg context
        nvgDelete(context)

        // Destroy window
        destroy()
    }

    // Window update function
    private fun update()
    {
        title = "FPS ($fps) | UPS ($ups)"

        // Check if there are any tasks which are meant to be added
        // Adds tasks which were added during iteration
        if (tasksToAdd.size > 0)
        {
            // Add tasks
            taskQueue.addAll(tasksToAdd)

            // Clear added tasks
            tasksToAdd.clear()
        }

        // Handle task queue
        // Check if there are any tasks in queue
        if (taskQueue.size > 0)
        {
            // Initialize task iterator
            iterator = taskQueue.iterator()

            // Iterate through task queue
            while (iterator.hasNext())
            {
                // Perform task
                if (iterator.next().perform())
                {
                    // Task was performed successfully
                    // Remove task
                    iterator.remove()
                }
            }
        }
    }

    // Window render function
    private fun render()
    {
        // Update viewport if window was resized
        if (windowResized)
        {
            // Update viewport
            glViewport(0, 0, width, height)

            // Reset window resize
            windowResized = false
        }

        // Clear drawing area
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)

        // Handle pixel radio
        val pxRatio = width.toFloat() / wWidth.toFloat()

        // NanoVG begin frame
        nvgBeginFrame(context, width, height, pxRatio)

        // Render all child views
        views.forEach { it.value.render(Canvas(context)) }

        // NanoVG end frame
        nvgEndFrame(context)

        // Swap the fillColor buffers
        glfwSwapBuffers(handle)
    }

    // Registers all window callbacks
    internal fun registerCallbacks()
    {
        // Key-callback
        glfwSetKeyCallback(handle, fun (handle, key, scancode, action, mods )
        {
            if (key == GLFW_KEY_N && action == GLFW_RELEASE)
            {
                val myForm = TestWindow()
                myForm.show()
            }

            if (key == GLFW_KEY_X && action == GLFW_RELEASE)
            {
                Application.exit()
            }
        })

        // Focus-callback
        glfwSetWindowFocusCallback(handle, fun (_, focus)
        {
            // Update focused var
            focused = focus
        })

        // Iconify (minimize - maximize) callback
        glfwSetWindowIconifyCallback(handle, fun (_, value)
        {
            // Update window state var
            windowState = if (value) WindowStates.MINIMIZED else WindowStates.NORMAL
        })

        // Window frame-buffer resize callback
        // Buffer-size is used for pixel-based calls
        glfwSetFramebufferSizeCallback(handle, fun (_, width, height)
        {
            // Set window size update lock
            // This prevents window to stuck in
            // endless loop
            windowSizeLock = true

            // Update width and height
            this.width = width
            this.height = height

            // Disable window size lock
            windowSizeLock = false
        })

        // Window resize callback
        glfwSetWindowSizeCallback(handle, fun(_, width, height)
        {
            // Window was resized
            windowResized = true

            // Update non-client area size
            wWidth = width
            wHeight = height
        })

        // Window maximize callback. Check if window
        // is maximized or windowed
        glfwSetWindowMaximizeCallback(handle, fun(_, maximized)
        {
            windowState = if (maximized) WindowStates.MAXIMIZED else WindowStates.NORMAL
        })

        // Window cursor position changed callback
        glfwSetCursorPosCallback(handle, fun(_, x, y)
        {
            // Update cursor position
            Window.cursor = Point(x.toInt(), y.toInt())
        })
    }

    // Destroys window
    private fun destroy()
    {
        // Close the window
        // Window closing must be performed from main thread
        Application.destroyWindowRequest(this);

        // Remove form from collection
        Application.removeWindow(this);
    }

    /**
     * Shows window.
     */
    // Show GLFW window
    fun show()
    {
        // Update window state
        windowState = WindowStates.NORMAL

        // Make the window visible
        Application.showWindowRequest(this)
    }

    /**
     * Hides window.
     */
    // Hides GLFW window
    fun hide()
    {
        // Update window state
        windowState = WindowStates.HIDDEN;

        // Hide the window
        Application.hideWindowRequest(this)
    }

    /**
     * Closes window.
     */
    // Closes GLFW window
    fun close()
    {
        destroy()
    }

    /**
     * Gives focus to window.
     */
    // Gives window focus
    fun focus()
    {
        // Focus the window
        Application.focusWindowRequest(this)
    }

    // Adds new swap interval request to the queue
    private fun updateSwapIntervalRequest(window: Window)
    {
        // Creates new anonymous task subclass
        tasksToAdd.add(object: Task()
        {
            // Perform event
            override fun perform(): Boolean
            {
                // Check if current context is set
                if (glfwGetCurrentContext() == NULL)
                {
                    // Fail
                    return false
                }

                // Update window's swap interval
                glfwSwapInterval(window.swapInterval)

                // Success
                return true
            }
        })
    }

    // Companion object for static class members
    companion object
    {
        // Cursor position
        var cursor : Point = Point(0, 0)
    }
}