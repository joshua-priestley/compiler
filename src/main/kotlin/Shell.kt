package compiler

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

    private val history = mutableListOf<String>()
    private val scanner = Scanner(System.`in`)
    private val terminal = TerminalBuilder.builder()
        .jna(true)
        .system(true)
        .build();


    private fun makeProgram(input: String?): String = "begin\n$input\nend\n"


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
        println(" - press ")
        println()

        //
        var inMultiLineStatement = false
        var buffer = ""

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
                if (buffer[buffer.length - 1] == '\n' && line == "") {
                    execute(buffer)
                    buffer = ""
                    inMultiLineStatement = false
                } else {
                    buffer += "\n$line"
                }
            } else if (enteringMultiLineStatement(line)) {
                buffer += "\n$line"
                inMultiLineStatement = true
            } else {
                execute(line)
            }
        }
    }

    //TODO change to use the lexer definitions somehow
    private fun enteringMultiLineStatement(line: String): Boolean {
        val stripped = line.replace("\\s".toRegex(), "")
        return stripped.substring(0, 2) == "if" ||
                stripped.substring(0, 5) == "while" ||
                stripped.substring(0, 3)  == "for" ||
                stripped[stripped.length - 1] == ';'
    }

    private fun execute(statement: String) {
        val toRun = makeProgram(statement)
        val result = lexAndParse(toRun, symbolTable)

        if (result !is FailedParse) {
            val backend = InterpreterBackend(symbolTable, varStore)
            backend.executeProgram((result as SuccessfulParse).root)
        }
    }
}
