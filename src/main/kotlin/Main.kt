import java.io.File

fun main(args: Array<String>) {
    Test.printInputFile(args[0])
}

object Test {
    fun printInputFile(filename: String) {
        File(filename).forEachLine { println(it) }
    }
}