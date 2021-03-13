package compiler

import AST.SymbolTable
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.util.*
import kotlin.collections.HashMap

fun main() {
    val shell = Shell()
    shell.test()
}

class Shell : FrontendUtils() {

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
        }
    }
}
