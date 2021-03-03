import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.InlineDataPart
import com.github.kittinunf.fuel.gson.responseObject
import com.google.gson.Gson
import compiler.Compiler
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.DynamicTest
import java.io.File
import java.lang.StringBuilder

data class CompilerReply(val test: String, val upload: String, val compiler_out: String)

class TestPrograms {
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
        println(inputFile.canonicalPath)
        val compiler = Compiler(inputFile.canonicalPath, true)
        val ret = compiler.compile()
        when {
            inputFile.canonicalPath.contains("syntax") -> assertEquals(100, ret)
            inputFile.canonicalPath.contains("semantic") -> assertEquals(200, ret)
            inputFile.canonicalPath.contains("valid") -> assertEquals(0, ret)
        }

        if(System.getProperty("test.type") == "backend") {
            val assemblyName = inputFile.canonicalPath.replace(".wacc", ".s");
            val executableName = inputFile.canonicalPath.replace(".wacc", "");

            Runtime.getRuntime()
                .exec("arm-linux-gnueabi-gcc -o $executableName -mcpu=arm1176jzf-s -mtune=arm1176jzf-s $assemblyName")
            println("Does the executable exist? ${File(executableName).exists()} $assemblyName")
            println("Does the assembly exist? ${File(assemblyName).exists()} $executableName")

            val process = ProcessBuilder("qemu-arm", "-L", "/usr/arm-linux-gnueabi/", executableName).start()

            val sb = StringBuilder()
            process.inputStream.reader(Charsets.UTF_8).use {
                sb.append(it.readText())
            }

            println("printing:    kkfkkfkfkfkfkfkfkf $sb")

            val x = process.waitFor()
            println("EXIITITITITITI CODE ISSSS: ${x}")
            println("is the process alive? ${process.isAlive}")

            val a = Fuel.upload("https://teaching.doc.ic.ac.uk/wacc_compiler/run.cgi")
                .add(FileDataPart(inputFile, "testfile", inputFile.name, "application/octet-stream"))
                .add(InlineDataPart("-x","options[]"))
                .responseObject<CompilerReply>(gson).third
                .component1()

            if (a != null) {
                val strs = a.compiler_out.split("===========================================================").toTypedArray()
                println(strs[1])
                assertEquals(strs[1], "\n" + sb)
            }

//            File(assemblyName).delete()
//            File(executableName).delete()
        }
    }

    @TestFactory
    fun createTests(): List<DynamicTest> {
        val files = when (System.getProperty("test.type")) {
            "syntax" -> testFiles.filter { it.absolutePath.contains("/syntaxErr/") }
            "semantic" -> testFiles.filter { it.absolutePath.contains("/semanticErr/") }
            "valid" -> testFiles.filter { it.absolutePath.contains("/valid/") }
            "backend" -> testFiles.filter { it.absolutePath.contains("/valid/") }
            else -> testFiles
        }
        return files.map { f -> DynamicTest.dynamicTest(f.name) { runTest(f) } }
    }
}