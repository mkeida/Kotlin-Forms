// Imports
import system.Application
import views.TestWindow

// Main application entry point
fun main(args: Array<String>)
{
    // Create window
    val myWindow = TestWindow()

    // Create new Application object and run it
    Application().run(myWindow)
}