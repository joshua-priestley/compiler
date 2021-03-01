import compiler.Compiler
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit


class TestBackend {
    fun String.runCommand(workingDir: File): String? {
        try {
            val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(60, TimeUnit.MINUTES)
            return proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            return null
        }
    }


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

        "arm-"

        val exec1 = Runtime.getRuntime()
            .exec("arm-linux-gnueabi-gcc -o $executableName -mcpu=arm1176jzf-s -mtune=arm1176jzf-s $assemblyName")

        val exec = "qemu-arm -L /usr/arm-linux-gnueabi/ $executableName".runCommand(inputFile.parentFile);
        println(exec)

// Read the output from the command

// Read the output from the command

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