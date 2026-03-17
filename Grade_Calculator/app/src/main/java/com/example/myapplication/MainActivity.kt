 package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

// 1. DATA CLASS
data class Student(val name: String, val mark: Int, val grade: String)

// 2. THE PROCESSOR CLASS (Encapsulation)
class GradeProcessor {
    // HIGH-ORDER FUNCTION: Takes an InputStream and a Lambda for grading
    fun processExcel(inputStream: InputStream, gradingLogic: (Int) -> String): List<Student> {
        val students = mutableListOf<Student>()
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            val name = row.getCell(0)?.stringCellValue ?: "Unknown"
            val mark = row.getCell(1)?.numericCellValue?.toInt() ?: 0
            
            // Calling the Lambda
            val grade = gradingLogic(mark)
            students.add(Student(name, mark, grade))
        }
        workbook.close()
        return students
    }
}

// 3. THE UI (Jetpack Compose)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GradeApp()
        }
    }
}

@Composable
fun GradeApp() {
    val processor = GradeProcessor()
    var results by remember { mutableStateOf<List<Student>>(emptyList()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "GCE Excel Grader", style = MaterialTheme.typography.headlineMedium)
        
        Button(
            onClick = {
                // In a real app, you'd use a FilePicker here. 
                // For this example, we assume you have a file in 'assets'
                // Or you can simulate data for testing.
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Upload & Process Excel")
        }

        // Displaying results
        results.forEach { student ->
            Text("${student.name}: ${student.grade}")
        }
    }
}