import compiler.Compiler
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.DynamicTest
import java.io.File

//TODO clean up/rethink how to control which tests are run in a more elegant way

class TestPrograms {

    /*
        We can get the compiler output as follows:

            val gson { Gson() }

    val f = File("./wacc_examples/valid/basic/skip/skip.wacc")
    val a = Fuel.upload("https://teaching.doc.ic.ac.uk/wacc_compiler/run.cgi")
            .add(FileDataPart(f, "testfile", f.name, "application/octet-stream"))
            .responseObject<CompilerReply>(gson).third
            .component1()

    println("aa ")
    if (a != null) {
        println(a.compiler_out)
    }
     */
    private val testDirsPath = "./src/test/kotlin/testDirs"
    private val examplesPath = "./wacc_examples/"

    //all directories specified in the testDirs file
    private val allTestDirs = File(testDirsPath).readLines()

    //all files in directories in allTestDirs (recursive)
    private val testFiles = allTestDirs
            .map { dir ->
                File(examplesPath + dir).walk()
                        .filter { ".wacc" in it.path }.toSortedSet()
            }.flatten()

    private fun runTest(inputFile: File) {
        println(inputFile.canonicalPath)
        val compiler = Compiler(inputFile.canonicalPath)

        //TODO: do we need to test anything other than exit code for this part?
        val ret = compiler.compile()
        //TODO move cases switching earlier? maybe use a map?
        when {
            inputFile.canonicalPath.contains("syntax") -> assertEquals(100, ret)
            inputFile.canonicalPath.contains("semantic") -> assertEquals(200, ret)
            inputFile.canonicalPath.contains("valid") -> assertEquals(0, ret)
        }
    }

    //note running through intellij will not show the file testnames - make in terminal and view the test report
    @TestFactory
    fun createTests(): List<DynamicTest> {
        val files = when (System.getProperty("test.type")) {
            "syntax" -> testFiles.filter {it.canonicalPath.contains("syntaxErr")}
            "semantic" -> testFiles.filter {it.canonicalPath.contains("semanticErr")}
            "valid" -> testFiles.filter {it.canonicalPath.contains("valid")}
            else -> testFiles
        }
        return files.map {f -> DynamicTest.dynamicTest(f.name) { runTest(f) } }
    }
}