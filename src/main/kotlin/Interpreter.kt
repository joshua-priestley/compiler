package compiler

class Interpreter {
}

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