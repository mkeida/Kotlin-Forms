// System package
package system

// Imports
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.system.MemoryUtil.NULL

/**
 * Framework main application entry class
 */
// Application class
class Application
{
    // Initialization
    init
    {
        // Initialize GLFW library
        initGlfw()
    }

    // Initialize GLWF library
    private fun initGlfw()
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this
        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        // Configure GLFW window hints
        glfwDefaultWindowHints()
    }

    /**
     * Framework entry method.
     * Starts new application life cycle and opens passed form.
     * @param window First form to open on application start
     */
    // Run function. Main framework entry
    fun run(window: Window)
    {
        // Make passed form visible
        window.show()

        // Run main loop
        loop()

        // Main loop ended
        // Terminate GLFW and free the error callback
        // Called after main loop ended with: exit = true
        glfwTerminate()
        glfwSetErrorCallback(null)!!.free()
    }

    // Main loop function
    private fun loop()
    {
        // Main thread will process window events and task queue
        while (!exit)
        {
            // Check if there are any tasks in queue
            if (taskQueue.size > 0)
            {
                // Initialize task iterator
                iterator = taskQueue.iterator()

                // Task queue
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

            // Check if there are any tasks which are meant to be added
            // Adds tasks which were added during iteration
            if (tasksToAdd.size > 0)
            {
                // Add tasks
                taskQueue.addAll(tasksToAdd)

                // Clear added tasks
                tasksToAdd.clear()
            }

            // Pause rendering for all windows
            safeToRender = false

            // Handle events
            glfwPollEvents()

            // Resume rendering
            safeToRender = true
        }
    }

    // Companion object for static class members
    companion object
    {
        // True if application (main loop) should end
        private var exit: Boolean = false

        // List of all currently opened - instantiated windows
        private val windows = ArrayList<Window>()

        // List of all tasks to be performed by main thread
        private val taskQueue = ArrayList<Task>()

        // Collection of new tasks which will be added after taskQueue iteration
        private val tasksToAdd = ArrayList<Task>()

        // Task iterator
        private var iterator = taskQueue.iterator()

        internal var safeToRender = true

        // Add window to windows collection
        @JvmStatic
        internal fun addWindow(window: Window)
        {
            windows.add(window)
        }

        // Remove window from windows collection
        @JvmStatic
        internal fun removeWindow(window: Window)
        {
            // If there is only one form remaining
            if (windows.size == 1)
            {
                // No forms opened
                // Exit application - main while loop
                exit = true
            }

            // Add form to waiting queue
            windows.remove(window)
        }

        // Adds new form creation request to the queue.
        // All windows must be created from main thread
        @JvmStatic
        internal fun createWindowRequest(window: Window)
        {
            // Creates new anonymous task subclass
            tasksToAdd.add(object : Task()
            {
                // Perform event
                override fun perform(): Boolean
                {
                    // Creates new window
                    val handle = glfwCreateWindow(window.width, window.height, window.title, NULL, NULL)

                    // Check for window creation fail
                    if (handle == NULL) throw RuntimeException("Unable to create GLFW window")

                    // Set window handle
                    window.handle = handle

                    // Register window callbacks
                    window.registerCallbacks()

                    // Create new thread which will hold window loop
                    Thread { window.loop() }.start()

                    // Success
                    return true
                }
            })
        }

        // Adds new form removal request to the queue
        // All windows must be destroyed from main thread
        @JvmStatic
        internal fun destroyWindowRequest(window: Window)
        {
            // Creates new anonymous task subclass
            tasksToAdd.add(object : Task()
            {
                // Perform event
                override fun perform(): Boolean
                {
                    // Window handle
                    val handle = window.handle

                    // Check if window handle was already created by main thread
                    if (handle == NULL)
                    {
                        // Handle was still not created
                        return false
                    }

                    // Close the window
                    // Must be called from main thread!
                    glfwSetWindowShouldClose(handle, true)

                    // Free the window callbacks and destroy the window
                    glfwFreeCallbacks(handle)
                    glfwDestroyWindow(handle)

                    // Success
                    return true
                }
            })
        }

        // Adds new window show request to the queue
        @JvmStatic
        internal fun showWindowRequest(window: Window)
        {
            // Creates new anonymous task subclass
            tasksToAdd.add(object : Task()
            {
                // Perform event
                override fun perform(): Boolean
                {
                    // Window handle
                    val handle = window.handle

                    // Check if window handle was already created by main thread
                    if (handle == NULL)
                    {
                        // Fail
                        return false
                    }

                    // Show window
                    // Must be called from main thread!
                    glfwShowWindow(handle)

                    // Success
                    return true
                }
            })
        }

        // Adds new window hide request to the queue
        @JvmStatic
        internal fun hideWindowRequest(window: Window)
        {
            // Creates new anonymous task subclass
            tasksToAdd.add(object : Task()
            {
                // Perform event
                override fun perform(): Boolean
                {
                    // Window handle
                    val handle = window.handle

                    // Check if window handle was already created by main thread
                    if (handle == NULL)
                    {
                        // Fail
                        return false
                    }

                    // Hide window
                    // Must be called from main thread!
                    glfwHideWindow(handle)

                    // Success
                    return true
                }
            })
        }

        // Adds new form focus request to the queue
        @JvmStatic
        internal fun focusWindowRequest(window: Window)
        {
            // Creates new anonymous task subclass
            tasksToAdd.add(object : Task()
            {
                // Perform event
                override fun perform(): Boolean
                {
                    // Window handle
                    val handle = window.handle

                    // Check if window handle was already created by main thread
                    if (handle == NULL)
                    {
                        // Fail
                        return false
                    }

                    // Focus window
                    // Must be called from main thread!
                    glfwFocusWindow(handle)

                    // Success
                    return true
                }
            })
        }

        // Adds new window title update request to the queue
        @JvmStatic
        internal fun updateWindowTitleRequest(window: Window)
        {
            // Creates new anonymous task subclass
            tasksToAdd.add(object : Task()
            {
                // Perform event
                override fun perform(): Boolean
                {
                    // Window handle
                    val handle = window.handle

                    // Check if window handle was already created by main thread
                    if (handle == NULL)
                    {
                        // Fail
                        return false
                    }

                    // Set window title
                    // Must be called from main thread!
                    glfwSetWindowTitle(handle, window.title)

                    // Success
                    return true
                }
            })
        }

        // Adds new window size update request to the queue
        @JvmStatic
        internal fun updateWindowSizeRequest(window: Window)
        {
            // Creates new anonymous task subclass
            tasksToAdd.add(object: Task()
            {
                // Perform event
                override fun perform(): Boolean
                {
                    // Window handle
                    val handle = window.handle

                    // Check if window handle was already created by main thread
                    if (handle == NULL)
                    {
                        // Fail
                        return false
                    }

                    // Set window size
                    // Must be called from main thread!
                    glfwSetWindowSize(handle, window.width, window.height)

                    // Success
                    return true
                }
            })
        }

        // Adds new application exit request to the queue
        @JvmStatic
        private fun exitRequest()
        {
            // Destroy all windows
            for (window in windows)
            {
                // Make destroy window request
                destroyWindowRequest(window)
            }

            // Adds new application exit task to queue
            // Creates new anonymous task subclass
            tasksToAdd.add(object : Task()
            {
                // Perform event
                override fun perform(): Boolean
                {
                    // Is task queue empty?
                    if (taskQueue.size == 0)
                    {
                        // End main loop
                        exit = true

                        // Success
                        return true
                    }

                    // There are still some tasks in queue
                    return false
                }
            })
        }

        /**
         * Ends application and closes all currently opened windows if there are any.
         */
        // End application
        @JvmStatic
        fun exit()
        {
            exitRequest()
        }
    }
}