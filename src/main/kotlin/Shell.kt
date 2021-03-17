package compiler

import AST.FunctionNode
import AST.SymbolTable
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.util.*

fun main() {
    val shell = Shell()
    shell.test()
}

class Shell : FrontendUtils() {

    private val symbolTable = SymbolTable(null, 0)
    private val varStore = VarStore()
    private val funcList: MutableList<FunctionNode> = mutableListOf()
    //private val func

    private val history = mutableListOf<String>()
    private val scanner = Scanner(System.`in`)
    private val terminal = TerminalBuilder.builder()
        .jna(true)
        .system(true)
        .build();

    private var inFunction = false
    private var inMultiLineStatement = false
    private var buffer = ""

    private fun makeProgram(input: String?): String {
        val program = "begin\n$input\n" + (if (inFunction) "skip\n" else "") + "end\n"
        inFunction = false
        return program
    }


    //TODO look into raw mode
    fun getUserInput(reader: NonBlockingReader): String {
// raw mode means we get keypresses rather than line buffered input
        var read = '0'.toInt()
        val sb = StringBuilder()
        while (read != '\n'.toInt()) {
            read = reader.read();
            sb.append(read.toChar())
        }
        return sb.toString()
    }

    fun test() {
        terminal.enterRawMode();
        val reader = terminal.reader();
        while (true) {
            print("> ")
            println(getUserInput(reader))
        }
        reader.close();
        terminal.close();
    }

    fun run() {
        println("-----WACC Shell-----")
        println(" - type *quit to exit")
        println(" - you don't need the global begin/end statements")
        println(" - when writing a multiple line statement/funciton provide enter a blank line to execute")
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
                //println(buffer)
                if (line == "") {
                    execute(buffer)
                    buffer = ""
                    inMultiLineStatement = false
                } else {
                    buffer += "\n$line"
                }
            } else if (enteringMultiLineStatement(line)) {
                buffer += "$line"
                inMultiLineStatement = true
            } else {
                execute(line)
            }
        }
    }

    //TODO change to use the lexer definitions somehow
    private fun enteringMultiLineStatement(line: String): Boolean {
        val stripped = line.replace("\\s".toRegex(), "")
        // Handle function parsing case
        if (stripped.substring(stripped.length - 2, stripped.length) == "is") {
            inFunction = true
            return true
        }
        return stripped.substring(0, 2) == "if" ||
                stripped.substring(0, 5) == "while" ||
                stripped.substring(0, 3)  == "for" ||
                stripped[stripped.length - 1] == ';'
    }

    private fun execute(statement: String) {
        val toRun = makeProgram(statement)
        val result = lexAndParse(toRun, symbolTable)
        println(toRun)
        if (result !is FailedParse) {
            val backend = InterpreterBackend(symbolTable, varStore, funcList)
            backend.executeProgram((result as SuccessfulParse).root)
            for (f in funcList) {
                println(f)
            }
        }
    }
}
