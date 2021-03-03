package compiler

import ErrorHandler.SemanticErrorHandler
import AST.SymbolTable
import AST.ASTBuilder
import AST.ProgramNode
import ErrorHandler.SyntaxErrorHandler
import org.antlr.v4.runtime.*
import antlr.*
import compiler.Instructions.CodeGeneration

import kotlin.system.exitProcess
import java.io.File
import java.lang.IllegalArgumentException

const val SYNTACTIC_ERROR = 100
const val SEMANTIC_ERROR = 200
const val OK = 0

fun main(args: Array<String>) {
    if (args.size != 1) {
        throw IllegalArgumentException("Wrong number of arguments: expected: 1, actual: {$args.size}")
    }

    val assembly = "-OC" in args

    val compiler = Compiler(args[0], true)

    exitProcess(compiler.compile())
}

class Compiler(private val inputFile: String, private val assembly: Boolean = false) {
    fun compile(): Int {
        val file = File(inputFile)

        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("Cannot find input file at ${file.absolutePath}")
        }

        println("Compiling...")

        val listener = SyntaxErrorHandler()
        val input = CharStreams.fromPath(file.toPath())
        val lexer = WACCLexer(input)
        lexer.removeErrorListeners()
        lexer.addErrorListener(listener)
        val tokens = CommonTokenStream(lexer)
        val parser = WACCParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(listener)
        val tree = parser.program()
        val semanticErrorHandler = SemanticErrorHandler()

        if (listener.hasSyntaxErrors()) {
            listener.printSyntaxErrors()
            return SYNTACTIC_ERROR
        }

        val symbolTable = SymbolTable(null, 0)
        val visitor = ASTBuilder(semanticErrorHandler, listener, symbolTable)
        val root = visitor.visit(tree)

        if (listener.hasSyntaxErrors()) {
            listener.printSyntaxErrors()
            return SYNTACTIC_ERROR
        }

        if (semanticErrorHandler.hasSemanticErrors()) {
            semanticErrorHandler.printSemanticErrors()
            return SEMANTIC_ERROR
        }

        if (assembly) {
            val codeGeneration = CodeGeneration(symbolTable)
            val listOfInstructions = codeGeneration.generateProgram(root as ProgramNode)
            val instructions = listOfInstructions.joinToString(separator = "\n") + "\n"

            val assemblyFileName = file.name.replace(".wacc", ".s")
            println(instructions)
            File(assemblyFileName).writeText(instructions)
            println("Put the assembly file in: ${File(assemblyFileName).canonicalPath}")
        }

        println("Successfully finished compilation with exit code 0.")
        return OK
    }
}