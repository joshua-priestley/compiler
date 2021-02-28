import compiler.Compiler
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


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

        val exec1 = Runtime.getRuntime()
            .exec("arm-linux-gnueabi-gcc -o $executableName -mcpu=arm1176jzf-s -mtune=arm1176jzf-s $assemblyName")
        val exec = Runtime.getRuntime().exec("qemu-arm -L /usr/arm-linux-gnueabi/ $executableName")

        val stdInput = BufferedReader(InputStreamReader(exec.getInputStream()))

        val stdError = BufferedReader(InputStreamReader(exec.getErrorStream()))

// Read the output from the command

// Read the output from the command
        println("Here is the standard output of the command:\n")
        var s: String? = null
        while (stdInput.readLine().also { s = it } != null) {
            println(s)
        }

// Read any errors from the attempted command

// Read any errors from the attempted command
        println("Here is the standard error of the command (if any):\n")
        while (stdError.readLine().also { s = it } != null) {
            println(s)
        }
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