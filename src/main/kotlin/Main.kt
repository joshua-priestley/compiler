package compiler

import SymbolTable
import Visitor
import WACCErrorListener
import org.antlr.v4.runtime.*
import antlr.*

import kotlin.system.exitProcess
import java.io.File
import java.lang.IllegalArgumentException

//TODO: do proper visibilities

fun main(args: Array<String>) {
    if(args.size != 1) {
        throw IllegalArgumentException("Wrong number of arguments: expected: 1, actual: {$args.size}")
    }

    val compiler = Compiler(args[0])
    val result = compiler.compile()
    exitProcess(result)
}

class Compiler(val inputFile: String) {
    //TODO: rethink return types to handle syntax vs semantic fail, error messages etc...
    fun check(): Boolean {
        val file = File(inputFile)

        if(!file.exists() || !file.isFile) {
            throw IllegalArgumentException("Cannot find input file at ${file.absolutePath}")
        }

        val input = CharStreams.fromPath(file.toPath())
        val lexer = WACCLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = WACCParser(tokens)
        parser.removeErrorListeners() // uncomment to get rid of antlr error messages
        val listener = WACCErrorListener()
        parser.addErrorListener(listener)
        val tree = parser.program()

        if (listener.hasSyntaxErrors()) {
            listener.printSyntaxErrors()
            return false
        }

        println(tree.toStringTree(parser))

        println("--------")
        val visitor = Visitor(listener);
        println(visitor.visit(tree).toString())

        if (listener.hasSyntaxErrors()) {
            listener.printSyntaxErrors()
            return false
        }

        return true
    }

    fun compile(): Int {
        val result = check()
        return if (result) {
            0
        } else {
            100
        }

    }
}