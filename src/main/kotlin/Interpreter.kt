package compiler

import AST.*

class InterpreterFrontend : FrontendUtils() {
    fun run(fileName: String): Int {
        val parseResult = lexAndParse(fileToString(fileName))
        return when (parseResult) {
            is SuccessfulParse -> {
                val backend = InterpreterBackend(parseResult.symbolTable, parseResult.root)
                backend.execute()
            }
            is FailedParse -> parseResult.statusCode
            else -> throw Error("Should not get here")
        }
    }
}

class InterpreterBackend (
    private var globalSymbolTable: SymbolTable,
    private val root: ProgramNode
) {
    private var exitCode = 0
    private var varStore: MutableMap<String, Any> = HashMap()


    fun displayVarStore() {
        println("All vars:")
        varStore.forEach{
            println("${it.key} = ${it.value}")
        }
    }

    fun execute(): Int {
        visitProgram(root)
        displayVarStore()
        println("Exitcode: $exitCode")
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
        val value = visitAssignRhs(stat.rhs)
        when (stat.lhs) {
            is AssignLHSIdentNode -> {
                varStore[stat.lhs.ident.name] = value
                println("${stat.lhs.ident.name} = $value")
            }
            is LHSArrayElemNode -> TODO("Not yet implemented")
            is LHSPairElemNode -> TODO("Not yet implemented")
            else -> throw Error("Should not get here")
        }
    }

    private fun visitDeclaration(stat: DeclarationNode) {
        val value = visitAssignRhs(stat.value)
        varStore[stat.ident.name] = value
        println("${stat.ident.name} = $value")
    }

    private fun visitAssignRhs(stat: AssignRHSNode): Any {
        return when (stat) {
            is RHSExprNode -> visitExpr(stat.expr)
            is RHSArrayLitNode -> TODO("Not yet implemented")
            is RHSNewPairNode -> TODO("Not yet implemented")
            is RHSPairElemNode -> TODO("Not yet implemented")
            is RHSCallNode -> TODO("Not yet implemented")
            else -> throw Error("Should not get here")
        }
    }

    private fun visitExpr(expr: ExprNode): Any {
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
            is Ident -> varStore[expr.name]!!
            else -> throw Error("Should not get here")
        }
    }

}