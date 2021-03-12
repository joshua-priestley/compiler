package compiler

import AST.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        throw IllegalArgumentException("Wrong number of arguments: expected: 1, actual: {$args.size}")
    }
    val frontend = InterpreterFrontend()
    val parseResult = frontend.lexAndParse(frontend.fileToString(args[0]))

    val exitCode = when (parseResult) {
        is SuccessfulParse -> {
            val backend = InterpreterBackend(parseResult.symbolTable, parseResult.root)
            backend.execute()
        }
        is FailedParse -> parseResult.statusCode
        else -> throw Error("Should not get here")
    }
    exitProcess(exitCode)
}

class InterpreterFrontend : FrontendUtils()

class InterpreterBackend (
    private var globalSymbolTable: SymbolTable,
    private val root: ProgramNode
) {
    private var exitCode = 0

    fun execute(): Int {
        visitProgram(root)
        return exitCode
    }

    private fun visitProgram(program: ProgramNode) {
        visitStat(program.stat)
    }

    private fun visitStat(stat: StatementNode) {
        when (stat) {
            is SkipNode -> return
            is DeclarationNode -> visitDeclaration(stat)
            is AssignNode -> visitAssign(stat)
            is ReadNode -> visitRead(stat)
            is FreeNode -> visitFree(stat)
            is ReturnNode -> visitReturn(stat)
            is ExitNode -> visitExit(stat)
            is PrintNode -> visitPrint(stat)
            is PrintlnNode -> visitPrintln(stat)
            is IfElseNode -> visitIf(stat)
            is WhileNode -> visitWhile(stat)
            is BeginEndNode -> visitBegin(stat)
            is SequenceNode -> visitSeq(stat)
        }
    }

    private fun visitSeq(stat: SequenceNode) {
        stat.statList.forEach { visitStat(it) }
    }

    private fun visitBegin(stat: BeginEndNode) {
        TODO("Not yet implemented")
    }

    private fun visitWhile(stat: WhileNode) {
        TODO("Not yet implemented")
    }

    private fun visitIf(stat: IfElseNode) {
        if (visitExpr(stat.expr) as Boolean) {
            visitStat(stat.then)
        } else {
            visitStat(stat.else_)
        }
    }

    private fun visitPrintln(stat: PrintlnNode) {
        visitPrint(PrintNode(stat.expr))
        println()
    }

    private fun visitPrint(stat: PrintNode) {
        print(visitExpr(stat.expr))
    }

    private fun visitExit(stat: ExitNode) {
        // This cast is safe as the program is semantically correct
        exitCode = visitExpr(stat.expr) as Int
    }

    private fun visitReturn(stat: ReturnNode) {
        TODO("Not yet implemented")
    }

    private fun visitFree(stat: FreeNode) {
        TODO("Not yet implemented")
    }

    private fun visitRead(stat: ReadNode) {
        TODO("Not yet implemented")
    }

    private fun visitAssign(stat: AssignNode) {
        TODO("Not yet implemented")
    }

    private fun visitDeclaration(stat: DeclarationNode) {
        TODO("Not yet implemented")
    }

    fun visitExpr(expr: ExprNode): Any {
        return when (expr) {
            is LiterNode -> generateLiterNode(expr)
            is BinaryOpNode -> generateBinOp(expr)
            is UnaryOpNode -> generateUnOp(expr)
            is ArrayElem -> generateArrayElem(expr)
            is PairLiterNode -> TODO("Not yet implemented")
            else -> TODO("Not yet implemented")
        }
    }

    private fun generateArrayElem(expr: ArrayElem): Any {
        TODO("Not yet implemented")
    }

    private fun generateUnOp(expr: UnaryOpNode): Any {
        TODO("Not yet implemented")
    }

    private fun generateBinOp(expr: BinaryOpNode): Any {
        TODO("Not yet implemented")
    }

    private fun generateLiterNode(expr: LiterNode): Any {
        return when(expr) {
            is IntLiterNode -> expr.value.toInt()
            is StrLiterNode -> expr.value
            is CharLiterNode -> expr.value[0] //TODO deal with escapes later
            is BoolLiterNode -> expr.value == "true"
            else -> throw Error("Should not get here")
        }
    }

}