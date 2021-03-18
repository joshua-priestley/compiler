package compiler

import AST.ASTBuilder
import AST.ProgramNode
import AST.SymbolTable
import ErrorHandler.SemanticErrorHandler
import ErrorHandler.SyntaxErrorHandler
import antlr.WACCLexer
import antlr.WACCParser
import compiler.CodeGen.CodeGeneration
import interpreter.InterpreterFrontend
import interpreter.Shell
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import kotlin.system.exitProcess

const val SYNTACTIC_ERROR = 100
const val SEMANTIC_ERROR = 200
const val OK = 0

fun main(args: Array<String>) {
    when (args.size) {
        1 -> {
            when (args[0]) {
                "-s" -> {
                    val shell = Shell()
                    shell.run()
                }
                else -> {
                    val compiler = Compiler(args[0], true)
                    exitProcess(compiler.compile())
                }
            }
        }
        2 -> {
            if (args[1] == "-i") {
                val interpreter = InterpreterFrontend()
                exitProcess(interpreter.run(args[0]))
            } else {
                throw IllegalArgumentException("Invalid format: to interpret programs use -i")
            }
        }
        else -> throw IllegalArgumentException("Wrong number of arguments: expected: 1 or 2, actual: ${args.size}")
    }
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

        println(root)

        if (assembly) {
            val codeGeneration = CodeGeneration(symbolTable)
            val listOfInstructions = codeGeneration.generateProgram(root as ProgramNode)
            val instructions = listOfInstructions.joinToString(separator = "\n") + "\n"

            val assemblyFileName = file.name.replace(".wacc", ".s")
            File(assemblyFileName).writeText(instructions)
            println(instructions)
        }

        println("Successfully finished compilation with exit code 0.")
        return OK
    }
}