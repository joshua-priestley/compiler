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
            runBackendTests(inputFile)
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

    private fun runBackendTests(inputFile: File) {
        val assemblyName = inputFile.name.replace(".wacc", ".s")
        val executableName = inputFile.nameWithoutExtension

        // Create the executable file
        Runtime.getRuntime()
            .exec("arm-linux-gnueabi-gcc -o ./$executableName -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ./$assemblyName").waitFor()

        val assemblyFile = File("./$assemblyName")
        val executableFile = File("./$executableName")

        // Get the value we should pass to stdin
        val stdinDataName = "./wacc_examples/inputs/${inputFile.name.replace(".wacc", ".input")}"

        // Get input for IO tests
        var stdin = ""
        if(File(stdinDataName).exists()) {
            stdin = File(stdinDataName).readText()
        }

        // Run QEMU on the created executable file
        val qemu = ProcessBuilder("/bin/sh", "-c", "echo $stdin | qemu-arm -L /usr/arm-linux-gnueabi/ $executableName").start()
        val exitCode = qemu.waitFor().toString()

        // Read the content produced by qemu
        val outputContent = StringBuilder()
        qemu.inputStream.reader().use {
            outputContent.append(it.readText())
        }

        // Cache the file if isn't already cached to reduce HTTP overhead
        val cachedName = "./wacc_examples/cached_outputs/${inputFile.name.replace(".wacc", "")}.output"
        val cachedFile = File(cachedName)
        if(!cachedFile.exists()) {
            // Contact the reference compiler using Fuel and gson
            val referenceCompiler =
                Fuel.upload("https://teaching.doc.ic.ac.uk/wacc_compiler/run.cgi")
                    .add(FileDataPart(inputFile, "testfile", inputFile.name, "text/plain"))
                    .add(InlineDataPart("-x","options[]"))
                    .add(InlineDataPart(stdin, "stdin"))
                    .responseObject<CompilerOutput>(gson)
                    .third
                    .component1()!!

            cachedFile.writeText(referenceCompiler.compiler_out)
        }

        val expectedContent = cachedFile.readText()
        val actualContent = formatToReferenceStyle(outputContent.toString(), exitCode)

        // Done with the files. Delete them.
        assemblyFile.delete()
        executableFile.delete()

        assertEquals(expectedContent, actualContent)
    }

    // Helper function to match style of reference compiler
    private fun formatToReferenceStyle(outputContent: String, exitCode: String): String {
        val str = StringBuilder()
        str.append("-- Compiling...\n")
        str.append("-- Assembling and Linking...\n")
        str.append("-- Executing...\n")
        str.append("===========================================================\n")
        str.append(outputContent)
        str.append("===========================================================\n")
        str.append("The exit code is $exitCode.\n")
        str.append("-- Finished\n")
        return str.toString()
    }
}

// Data class to store results given from reference compiler
// Note the names of the fields have to stay the same (as these are predefined
// by the reference compiler)
data class CompilerOutput(val test: String, val upload: String, val compiler_out: String)