import compiler.Compiler
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import java.io.File

//TODO clean up/rethink how to control which tests are run in a more elegant way

class Test {

    val testDirsPath = "./src/test/kotlin/testDirs"
    val examplesPath = "./wacc_examples/"

    //all directories specified in the testDirs file
    val allTestDirs = File(testDirsPath).readLines()

    //all files in directories in allTestDirs (recursive)
    val testFiles = allTestDirs
        .map { dir ->
            File(examplesPath + dir).walk()
                .filter { ".wacc" in it.path }.toSortedSet()
        }.flatten()

    fun runTest(inputFile: File) {
        println(inputFile.canonicalPath)
        val compiler = Compiler(inputFile.canonicalPath)

        //TODO: do we need to test anything other than exit code for this part?
        val ret = compiler.compile()
        //TODO move cases switching earlier? maybe use a map?
        when {
            inputFile.canonicalPath.contains("syntax") -> assertEquals(ret, 100)
            inputFile.canonicalPath.contains("semantic") -> assertEquals(ret, 200)
            inputFile.canonicalPath.contains("valid") -> assertEquals(ret, 0)
        }
    }

    //note running through intellij will not show the file testnames - make in terminal and view the test report
    @TestFactory
    fun createTests(): List<DynamicTest> {
        return testFiles.map { f -> DynamicTest.dynamicTest(f.name) { runTest(f) } }
    }
}