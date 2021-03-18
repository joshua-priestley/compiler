package interpreter

import AST.FunctionNode
import AST.SymbolTable
import AST.Types.Type
import compiler.FailedParse
import compiler.FrontendUtils
import compiler.SuccessfulParse
import compiler.interpreter.VarStore
import java.util.*


class Shell : FrontendUtils() {

    // Structures allowing for values/functions to persist between statements
    private val symbolTable = SymbolTable(null, 0)
    private val varStore = VarStore()
    private val funcList: MutableList<FunctionNode> = mutableListOf()
    private val functionParameters: LinkedHashMap<String, List<Type>> = linkedMapOf()

    // Variables handling multi-line statements/functions
    private var inFunction = false
    private var inMultiLineStatement = false
    private var buffer = ""

    // Format the statement to a valid program
    private fun makeProgram(input: String?): String {
        return "begin\n$input\n" + (if (inFunction) "skip\n" else "") + "end\n"
    }

    // Main shell loop
    fun run() {
        println("-----WACC Shell-----")
        println(" - type *quit to exit")
        println(" - you don't need the global begin/end statements")
        println(" - when writing a multiple line statement/funciton enter a blank line after to execute")
        println()

        while (true) {

            if (inMultiLineStatement) {
                print("... ")
            } else {
                print(">>> ")
            }

            val line = readLine()!!

            if (line == "*quit") {
                break
            } else if (inMultiLineStatement) {
                if (line == "") {
                    execute(buffer)
                } else {
                    buffer += "\n$line"
                }
            } else if (enteringMultiLineStatement(line)) {
                buffer += line
                inMultiLineStatement = true
            } else {
                execute(line)
            }
        }
    }

    // Check if entering a multi-line statement
    private fun enteringMultiLineStatement(line: String): Boolean {
        val stripped = line.replace("\\s".toRegex(), "")
        // Handle function parsing case
        if (stripped.substring(stripped.length - 2, stripped.length) == "is") {
            inFunction = true
            return true
        }
        return stripped.substring(0, 2) == "if" ||
                stripped.length >= 5 && stripped.substring(0, 5) == "while" ||
                stripped[stripped.length - 1] == ';'
    }

    // Execute the user's input
    private fun execute(statement: String) {
        val toRun = makeProgram(statement)
        val result = lexAndParse(toRun, symbolTable, functionParameters)
        if (result !is FailedParse) {
            val backend = InterpreterBackend(symbolTable, varStore, funcList)
            backend.executeProgram((result as SuccessfulParse).root)
        }
        // Reset all variables to do with multi-line input
        buffer = ""
        inFunction = false
        inMultiLineStatement = false
    }
}
