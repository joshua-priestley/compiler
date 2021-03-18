package compiler

import compiler.CodeGen.CodeGeneration
import interpreter.InterpreterFrontend
import interpreter.Shell
import java.io.File
import kotlin.system.exitProcess

const val SYNTACTIC_ERROR = 100
const val SEMANTIC_ERROR = 200
const val OK = 0

fun main(args: Array<String>) {
    when (args.size) {
        1 -> {
            when (args[0]) {
                "-s" -> {
                    val shell = Shell()
                    shell.run()
                }
                else -> {
                    val compiler = Compiler(args[0], true)
                    exitProcess(compiler.compile())
                }
            }
        }
        2 -> {
            if (args[1] == "-i") {
                val interpreter = InterpreterFrontend()
                exitProcess(interpreter.run(args[0]))
            } else {
                throw IllegalArgumentException("Invalid format: to interpret programs use -i")
            }
        }
        else -> throw IllegalArgumentException("Wrong number of arguments: expected: 1 or 2, actual: ${args.size}")
    }
}

class Compiler(private val inputFile: String, private val assembly: Boolean = false): FrontendUtils() {
    fun compile(): Int {

        val file = File(inputFile)

        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("Cannot find input file at ${file.absolutePath}")
        }

        println("Compiling...")
        val result = lexAndParse(file.readText())

        if (result is SuccessfulParse) {
            println(result.root)
            if (assembly) {
                val codeGeneration = CodeGeneration(result.symbolTable)
                val listOfInstructions = codeGeneration.generateProgram(result.root)
                val instructions = listOfInstructions.joinToString(separator = "\n") + "\n"

                val assemblyFileName = file.name.replace(".wacc", ".s")
                File(assemblyFileName).writeText(instructions)
                println(instructions)
            }

            println("Successfully finished compilation with exit code 0.")
            return OK
        }
        return (result as FailedParse).statusCode
    }
}