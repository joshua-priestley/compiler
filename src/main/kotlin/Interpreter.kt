package compiler

import AST.ASTBuilder
import AST.SymbolTable
import ErrorHandler.SemanticErrorHandler
import ErrorHandler.SyntaxErrorHandler
import antlr.WACCLexer
import antlr.WACCParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class Interpreter {
}

fun makeProgram(input: String?): String = "begin\n$input\nend\n"


fun main() {


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


        println(root)
        print(input)
        println(tokens[0].toString())

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