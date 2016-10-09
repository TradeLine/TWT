package ggg

import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.WindowConstants

object Viwer {
    fun show() {
        //Create and set up the window.
        //javax.swing.SwingUtilities.invokeLater { buildWindow() }
        buildWindow()
        println("Window closed")
    }

    private fun buildWindow() {
        val frame = Window()
        frame.setVisible(true)
    }


    class Window: JDialog() {
        init {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(250, 100);
            modalityType = ModalityType.APPLICATION_MODAL
        }

        override fun dispose() {
            super.dispose()
        }
    }
}