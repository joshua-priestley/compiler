import compiler.Compiler
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.DynamicTest
import java.io.File

class TestPrograms {
    private val testDirsPath = "./src/test/kotlin/testDirs"
    private val examplesPath = "./wacc_examples/"

    // All directories specified in the testDirs file
    private val allTestDirs = File(testDirsPath).readLines()

    // All files in directories in allTestDirs (recursive)
    private val testFiles = allTestDirs
        .map { dir ->
            File(examplesPath + dir).walk()
                .filter { ".wacc" in it.path }.toSortedSet()
        }.flatten()

    private fun runTest(inputFile: File) {
        val compiler = Compiler(inputFile.canonicalPath, true)

        val ret = compiler.compile()
        when {
            inputFile.canonicalPath.contains("syntax") -> assertEquals(100, ret)
            inputFile.canonicalPath.contains("semantic") -> assertEquals(200, ret)
            inputFile.canonicalPath.contains("valid") -> assertEquals(0, ret)
        }
    }

    @TestFactory
    fun createTests(): List<DynamicTest> {
        val files = when (System.getProperty("test.type")) {
            "syntax" -> testFiles.filter { it.absolutePath.contains("/syntaxErr/") }
            "semantic" -> testFiles.filter { it.absolutePath.contains("/semanticErr/") }
            "valid" -> testFiles.filter { it.absolutePath.contains("/valid/") }
            else -> testFiles
        }
        return files.map { f -> DynamicTest.dynamicTest(f.name) { runTest(f) } }
    }
}