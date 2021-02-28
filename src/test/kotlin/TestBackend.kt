import compiler.Compiler
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.DynamicTest
import java.io.File

class TestBackend {
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
        val assemblyName = inputFile.absolutePath.replace(".wacc", ".s");
        val executableName = inputFile.absolutePath.replace(".wacc", "");

        Runtime.getRuntime()
            .exec("arm-linux-gnueabi-gcc -o $executableName -mcpu=arm1176jzf-s -mtune=arm1176jzf-s $assemblyName")
        if(File(executableName).exists()) {
            println("successfully compiled a .o file..... :)")
        } else {
            println("didn't compile t :(")
        }

    }

    @TestFactory
    fun createTests(): List<DynamicTest> {
        if(System.getProperty("test.type") == "backend") {
            val files = when (System.getProperty("test.type")) {
                "syntax" -> testFiles.filter { it.absolutePath.contains("/syntaxErr/") }
                "semantic" -> testFiles.filter { it.absolutePath.contains("/semanticErr/") }
                "valid" -> testFiles.filter { it.absolutePath.contains("/valid/") }
                else -> testFiles
            }
            return files.map { f -> DynamicTest.dynamicTest(f.name) { runTest(f) } }
        } else {
            return mutableListOf()
        }
    }
}