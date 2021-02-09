package compiler

import SymbolTable
import Visitor
import WACCErrorListener
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
        parser.removeErrorListeners() // uncomment to get rid of antlr error messages
        val listener = WACCErrorListener()
        parser.addErrorListener(listener)
        val tree = parser.program()
        if(!listener.errorList.isEmpty()) {

            System.out.println("----- Syntactic Errors Detected -----")

            listener.errorList.forEach {
                println(it)
            }

            println("${listener.errorList.size} parser errors detected. No further compilation attempted.")
            return true;
        }
        println(tree.toStringTree(parser))

        println("--------")
        val visitor = Visitor(SymbolTable(null));
        println(visitor.visit(tree).toString())

        return true
    }

    fun compile(): Int {
        val result = check()
        return if (result) {
            0
        } else {
            100;
        }
    }
}