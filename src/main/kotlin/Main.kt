import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*
import antlr.*

import kotlin.system.exitProcess
import java.io.File


fun main(args: Array<String>) {

    var exitCode = 0
    //TODO: args/file validation?
    val file = File(args[0])
    val input = CharStreams.fromPath(file.toPath())
    val lexer = WACCLexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = WACCParser(tokens)

    exitProcess(exitCode)
}