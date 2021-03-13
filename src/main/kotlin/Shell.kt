package compiler

import AST.ASTBuilder
import AST.ProgramNode
import AST.SymbolTable
import ErrorHandler.SemanticErrorHandler
import ErrorHandler.SyntaxErrorHandler
import antlr.WACCLexer
import antlr.WACCParser
import com.sun.java.accessibility.util.Translator
import compiler.CodeGen.CodeGeneration
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.lang.StringBuilder


fun main() {
    val shell = Shell()
    shell.run()
}

class Shell : FrontendUtils() {

    private fun makeProgram(input: String?): String = "begin\n$input\nend\n"


    fun run() {
        println("-----WACC Shell-----")
        println(" - type *quit to exit")
        println(" - you don't need the global begin/end statements")
        println(" - the number of '>'s at the start of the line represents the level of nesting")
        println()
        val symbolTable = SymbolTable(null, 0)
        val varStore: MutableMap<String, Any> = HashMap()
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

            val toRun = makeProgram(line)
            val result = lexAndParse(toRun, symbolTable)


            if (result !is FailedParse && indentLevel == 0) {
                val backend = InterpreterBackend(symbolTable, varStore)
                backend.executeProgram((result as SuccessfulParse).root)
            }

            /*
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
            */

        }
    }
}
