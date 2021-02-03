package compiler

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*
import antlr.*

import kotlin.system.exitProcess
import java.io.File

//TODO: do proper visibilities

fun main(args: Array<String>) {
    //TODO: args/file validation?
    val compiler = Compiler(args[0])
    val result = compiler.compile()
    exitProcess(result)
}

class Compiler(val inputFile: String) {
    //TODO: rethink return types to handle syntax vs semantic fail, error messages etc...
    fun check(): Boolean {
        val file = File(inputFile)
        val input = CharStreams.fromPath(file.toPath())
        val lexer = WACCLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = WACCParser(tokens)
        val tree = parser.program()
        println(tree.toStringTree(parser))

        return true
    }

    fun compile(): Int {
        val result = check()
        if (result) {
            return 0
        } else {
            return 100;
        }
    }
}