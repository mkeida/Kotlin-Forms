package views

// Import
import geometry.Point
import system.Window
import views.*

// Test window
class TestWindow : Window()
{
    init
    {
        for (x in 0..2)
        {
            for (y in 0..2)
            {
                val btn = Button()

                btn.name = "Button $x x $y"
                btn.text = btn.name
                btn.width = 100
                btn.height = 40
                btn.position = Point(x * btn.width + (x * 20) + 20, y * btn.height + (y * 20) + 20)

                btn.add(this)
            }
        }
    }
}