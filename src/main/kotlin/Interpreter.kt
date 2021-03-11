package compiler

import AST.ASTBuilder
import AST.ProgramNode
import AST.SymbolTable
import ErrorHandler.SemanticErrorHandler
import ErrorHandler.SyntaxErrorHandler
import antlr.WACCLexer
import antlr.WACCParser
import compiler.CodeGen.CodeGeneration
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.lang.StringBuilder


fun main() {
    val interpreter = Interpreter()
    interpreter.run()
}

class Interpreter {


    fun makeProgram(input: String?): String = "begin\n$input\nend\n"


    fun run() {


        println("-----WACC Interpreter-----")
        println(" - type *quit to exit")
        println(" - you don't need the global begin/end statements")
        println(" - the number of '>'s at the start of the line represents the level of nesting")
        println()
        var indentLevel = 0
        while (true) {
            print(">".repeat(indentLevel + 1) + "  ")
            val line = readLine()
            //val aaa = WACCLexer.


            val listener = SyntaxErrorHandler()
            val input = CharStreams.fromString(makeProgram(line))
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
            }

            val symbolTable = SymbolTable(null, 0)
            val visitor = ASTBuilder(semanticErrorHandler, listener, symbolTable)
            val root = visitor.visit(tree)

            if (listener.hasSyntaxErrors()) {
                listener.printSyntaxErrors()
            }

            if (semanticErrorHandler.hasSemanticErrors()) {
                semanticErrorHandler.printSemanticErrors()
            }

            /*
            println(root)
            print(input)
            println(tokens[0].toString())
             */

            generateCode(symbolTable, root as ProgramNode)
            // if can parse properly, then execute
            // else set moreinput, wait until
            if (line == "*tog") {
                indentLevel++
            } else if (line == "*togg") {
                indentLevel--
            }

            if (line == "*quit") {
                break
            }
        }
    }

    fun generateCode(symbolTable: SymbolTable, root: ProgramNode) {

        val codeGeneration = CodeGeneration(symbolTable)
        val listOfInstructions = codeGeneration.generateProgram(root)
        val instructions = listOfInstructions.joinToString(separator = "\n") + "\n"

        val assemblyFileName = "temp.s"
        val executableName = "temp"
        File(assemblyFileName).writeText(instructions)


        // Create the executable file
        Runtime.getRuntime()
            .exec("arm-linux-gnueabi-gcc -o ./$executableName -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ./$assemblyFileName")
            .waitFor()

        // Run QEMU on the created executable file
        val qemu = ProcessBuilder("/bin/sh", "-c", "qemu-arm -L /usr/arm-linux-gnueabi/ $executableName").start()
        val exitCode = qemu.waitFor().toString()

        // Read the content produced by qemu
        val outputContent = StringBuilder()
        qemu.inputStream.reader().use {
            outputContent.append(it.readText())
        }

        if (outputContent.isNotBlank()) {
            print(outputContent.toString())
            println()
        }
    }
}
