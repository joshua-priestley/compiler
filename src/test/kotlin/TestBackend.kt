import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.InlineDataPart
import com.github.kittinunf.fuel.gson.responseObject
import com.google.gson.Gson
import compiler.Compiler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

data class CompilerReply(val test: String, val upload: String, val compiler_out: String)

class TestBackend {

    private val testDirsPath = "./src/test/kotlin/testDirs"
    private val examplesPath = "./wacc_examples/"
    private val gson by lazy { Gson() }

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
        //assert(File(executableName).exists())

        val process = ProcessBuilder("qemu-arm", "-L", "/usr/arm-linux-gnueabi/", executableName).start()

        val sb = StringBuilder()
//        sb.append("-- Compiling...\n")
//        sb.append("-- Assembling and Linking...\n")
//        sb.append("-- Executing...\n")
//        sb.append("===========================================================\n")
        process.inputStream.reader(Charsets.UTF_8).use {
            sb.append(it.readText())
        }
//        sb.append("===========================================================\n")
//        sb.append("The exit code is 0.\n")
//        sb.append("-- Finished")


        val a = Fuel.upload("https://teaching.doc.ic.ac.uk/wacc_compiler/run.cgi")
            .add(FileDataPart(inputFile, "testfile", inputFile.name, "application/octet-stream"))
            .add(InlineDataPart("-x","options[]"))
            .responseObject<CompilerReply>(gson).third
            .component1()

        println(sb.toString())

        if (a != null) {
            val strs = a.compiler_out.split("===========================================================").toTypedArray()
            println(strs[1])
            assertEquals(strs[1], "\n" + sb)
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