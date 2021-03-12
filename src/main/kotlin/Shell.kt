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
    val shell = Shell()
    shell.run()
}

class Shell {


    fun makeProgram(input: String?): String = "begin\n$input\nend\n"


    fun run() {
        println("-----WACC Shell-----")
        println(" - type *quit to exit")
        println(" - you don't need the global begin/end statements")
        println(" - the number of '>'s at the start of the line represents the level of nesting")
        println()
        val symbolTable = SymbolTable(null, 0)
        var indentLevel = 0

        while (true) {
            print(">".repeat(indentLevel + 1) + "  ")
            val line = readLine()

            // if can parse properly, then executea
            // else set moreinput, wait until
            if (line == "*tog") {
                indentLevel++
            } else if (line == "*togg") {
                indentLevel--
            }

            if (line == "*quit") {
                break
            }

            // true if line can be executed
            var valid = true

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
                valid = false
            }

            val visitor = ASTBuilder(semanticErrorHandler, listener, symbolTable)
            val root = visitor.visit(tree)

            if (listener.hasSyntaxErrors()) {
                listener.printSyntaxErrors()
                valid = false
            }

            if (semanticErrorHandler.hasSemanticErrors()) {
                semanticErrorHandler.printSemanticErrors()
                valid = false
            }

            /*
            println(root)
            print(input)
            println(tokens[0].toString())
             */
            if (valid && indentLevel == 0) {
                println("valid")

                //generateCode(symbolTable, root as ProgramNode)
            }
        }
    }
}
