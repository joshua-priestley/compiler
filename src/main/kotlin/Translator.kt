package compiler

import AST.ASTBuilder
import AST.ProgramNode
import AST.SymbolTable
import AST.Types.Type
import ErrorHandler.SemanticErrorHandler
import ErrorHandler.SyntaxErrorHandler
import antlr.WACCLexer
import antlr.WACCParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

interface ParseResult

data class FailedParse(val statusCode: Int): ParseResult
data class SuccessfulParse(val root: ProgramNode, val symbolTable: SymbolTable): ParseResult


abstract class FrontendUtils {

    // Returns the contents of a file as a string
    fun fileToString(fileName: String):String {
        val file = File(fileName)

        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("Cannot find input file at ${file.absolutePath}")
        }
        return file.readText()
    }


    fun lexAndParse(
        program: String,
        st: SymbolTable = SymbolTable(null, 0),
        funcParams: LinkedHashMap<String, List<Type>> = linkedMapOf()
    ): ParseResult {

        val listener = SyntaxErrorHandler()
        val input = CharStreams.fromString(program)
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
            return FailedParse(SYNTACTIC_ERROR)
        }

        val visitor = ASTBuilder(semanticErrorHandler, listener, st, funcParams)
        val root = visitor.visit(tree)

        if (listener.hasSyntaxErrors()) {
            listener.printSyntaxErrors()
            return FailedParse(SYNTACTIC_ERROR)
        }

        if (semanticErrorHandler.hasSemanticErrors()) {
            semanticErrorHandler.printSemanticErrors()
            return FailedParse(SEMANTIC_ERROR)
        }
        return SuccessfulParse(root as ProgramNode, st)
    }
}