 package org.example

import java.io.File
import java.awt.FlowLayout
import javax.swing.*
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

// 1. DATA CLASS
data class Student(val name: String, val mark: Int, val grade: String)

// 2. THE PROCESSOR CLASS (The "Engine")
class GradeProcessor {
    // This is the HIGH-ORDER FUNCTION
    // It takes a File and a Lambda (the "Policy")
    fun processXml(xmlFile: File, gradingLogic: (Int) -> String): List<Student> {
        val students = mutableListOf<Student>()
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        doc.documentElement.normalize()
        val nodes = doc.getElementsByTagName("student")

        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node.nodeType == org.w3c.dom.Node.ELEMENT_NODE) {
                val elem = node as Element
                val name = elem.getElementsByTagName("name").item(0).textContent
                val mark = elem.getElementsByTagName("mark").item(0).textContent.toInt()
                
                // Using the LAMBDA here
                val grade = gradingLogic(mark)
                students.add(Student(name, mark, grade))
            }
        }
        return students
    }
}

// 3. THE UI CLASS (The "View")
class MainUI {
    private val processor = GradeProcessor()

    fun show() {
        val frame = JFrame("GCE XML Grader")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(400, 150)
        frame.layout = FlowLayout()

        val btn = JButton("Upload XML & Calculate")
        
        // This is a Lambda for the button click!
        btn.addActionListener {
            val chooser = JFileChooser()
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                
                // CALLING THE HOF WITH A LAMBDA
                val results = processor.processXml(file) { mark ->
                    // This is our "Note" to the box
                    when {
                        mark >= 80 -> "A"
                        mark >= 70 -> "B"
                        mark >= 50 -> "Pass"
                        else -> "Fail"
                    }
                }

                // Show results in a popup
                val output = results.joinToString("\n") { "${it.name}: ${it.grade}" }
                JOptionPane.showMessageDialog(frame, output)
            }
        }

        frame.add(btn)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}

fun main() {
    SwingUtilities.invokeLater { MainUI().show() }
}