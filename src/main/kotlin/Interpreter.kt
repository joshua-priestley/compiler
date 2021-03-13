package compiler

import AST.*

class InterpreterFrontend : FrontendUtils() {
    fun run(fileName: String): Int {
        val parseResult = lexAndParse(fileToString(fileName))
        val varStore = VarStore()
        return when (parseResult) {
            is SuccessfulParse -> {
                val backend = InterpreterBackend(parseResult.symbolTable, varStore)
                backend.executeProgram(parseResult.root)
            }
            is FailedParse -> parseResult.statusCode
            else -> throw Error("Should not get here")
        }
    }
}


data class PairObject<T, S>(var fst: T?, var snd: S?)

class InterpreterBackend (
    private var globalSymbolTable: SymbolTable,
    private var varStore: VarStore
) {
    private var exitCode = 0


    fun displayVarStore(varStore: VarStore) {
        println("\n\nAll vars:")
        varStore.varStore.forEach{
            println("${it.key} = ${it.value}")
        }
    }

    fun executeProgram(root: ProgramNode): Int {
        visitProgram(root)
        //displayVarStore()
        //println("Exitcode: $exitCode")
        return exitCode
    }

    private fun visitProgram(program: ProgramNode) {
        visitStat(program.stat)
    }

    fun visitStat(stat: StatementNode) {
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
        val expr = visitExpr(stat.expr)
        when (expr) {
            is PairObject<*, *> -> print("#pair_addr#")
            is Array<*> -> print("#arr_addr#")
            else -> print(expr)
        }
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
        val input = readLine()
        when (stat.lhs) {
            is AssignLHSIdentNode -> {
                if (varStore.typeIsInt(stat.lhs.ident.name)) {
                    varStore.assignBaseValue(stat.lhs.ident.name, input!!.toInt())
                } else {
                    varStore.assignBaseValue(stat.lhs.ident.name, input!![0])
                }
            }
            // Pair and array assigning handle checking of char and int types
            is LHSArrayElemNode -> assignArrayElem(input, stat.lhs)
            is LHSPairElemNode -> assignPairElem(input, stat.lhs)
            else -> throw Error("Should not get here")
        }
    }

    private fun visitAssign(stat: AssignNode) {
        val value = visitAssignRhs(stat.rhs)
        when (stat.lhs) {
            is AssignLHSIdentNode -> {
                varStore.assignBaseValue(stat.lhs.ident.name, value)
                //println("${stat.lhs.ident.name} = $value")
            }
            is LHSArrayElemNode -> assignArrayElem(value, stat.lhs)
            is LHSPairElemNode -> assignPairElem(value, stat.lhs)
            else -> throw Error("Should not get here")
        }
    }

    private fun <T> assignPairElem(value: T, stat: LHSPairElemNode) {
        when (val pairElem = stat.pairElem) {
            // These unchecked casts are safe as any errors would be caught during semantic checks
            is FstExpr -> ((visitExpr(pairElem.expr)) as PairObject<T, *>).fst = value
            is SndExpr -> ((visitExpr(pairElem.expr)) as PairObject<*, T>).snd = value
            else -> throw Error("Should not get here")
        }
    }

    private fun <T> assignArrayElem(value: T, stat: LHSArrayElemNode) {
        val expr = stat.arrayElem
        var current: Any = varStore.getValue(expr.ident.name)
        // Keep indexing into subarrays for each index expression there is
        for (i in 0 .. expr.expr.size - 2) {
            current = (current as Array<*>)[(visitExpr(expr.expr[i]) as Int)]!!
        }
        // This unchecked cast is safe as any errors would be caught during semantic analysis
        (current as Array<T>)[(visitExpr(expr.expr[expr.expr.size - 1]) as Int)] = value
    }

    private fun visitDeclaration(stat: DeclarationNode) {
        val value = visitAssignRhs(stat.value)
        varStore.declareBaseValue(stat.ident.name, value)
        //println("${stat.ident.name} = $value")
    }

    private fun visitAssignRhs(stat: AssignRHSNode): Any {
        return when (stat) {
            is RHSExprNode -> visitExpr(stat.expr)
            is RHSArrayLitNode -> visitRHSArrayLit(stat)
            is RHSNewPairNode -> visitRHSNewPair(stat)
            is RHSPairElemNode -> visitRHSPairElem(stat)
            is RHSCallNode -> TODO("Not yet implemented")
            else -> throw Error("Should not get here")
        }
    }

    // TODO remove non null assertions with runtime errors
    private fun visitRHSPairElem(expr: RHSPairElemNode): Any {
        return when (expr.pairElem) {
            is FstExpr -> ((visitExpr(expr.pairElem.expr)) as PairObject<*, *>).fst!!
            is SndExpr -> ((visitExpr(expr.pairElem.expr)) as PairObject<*, *>).snd!!
            else -> throw Error("Should not get here")
        }
    }

    private fun visitRHSNewPair(expr: RHSNewPairNode): Any {
        return PairObject(visitExpr(expr.expr1), visitExpr(expr.expr2))
    }

    private fun visitRHSArrayLit(expr: RHSArrayLitNode): Any {
        return expr.exprs.stream()
            .map{visitExpr(it)}.toArray()
    }

    private fun visitExpr(expr: ExprNode): Any {
        return when (expr) {
            is LiterNode -> visitLiterNode(expr)
            is BinaryOpNode -> visitBinOp(expr)
            is UnaryOpNode -> visitUnOp(expr)
            is ArrayElem -> visitArrayElem(expr)
            is PairLiterNode -> PairObject(null, null)
            else -> TODO("Not yet implemented")
        }
    }

    private fun visitArrayElem(expr: ArrayElem): Any {
        var current: Any = varStore.getValue(expr.ident.name)
        // Keep indexing into subarrays for each index expression there is
        for (index in expr.expr) {
            current = (current as Array<*>)[(visitExpr(index) as Int)]!!
        }
        return current
    }

    private fun visitUnOp(expr: UnaryOpNode): Any {
        return when (expr.operator) {
            UnOp.NOT -> !(visitExpr(expr.expr) as Boolean)
            UnOp.MINUS -> (visitExpr(expr.expr) as Int).unaryMinus()
            UnOp.CHR -> (visitExpr(expr.expr) as Int).toChar()
            UnOp.ORD -> (visitExpr(expr.expr) as Char).toInt()
            UnOp.LEN -> TODO("Arrays not yet implemented")
            else -> throw Error("Should not get here")
        }
    }

    private fun visitBinOp(expr: BinaryOpNode): Any {
        val expr1 = visitExpr(expr.expr1)
        val expr2 = visitExpr(expr.expr2)
        return when (expr.operator) {
            BinOp.PLUS -> (expr1 as Int) + (expr2 as Int)
            BinOp.MINUS -> (expr1 as Int) - (expr2 as Int)
            BinOp.MUL -> (expr1 as Int) * (expr2 as Int)
            BinOp.MOD -> (expr1 as Int) % (expr2 as Int)
            BinOp.DIV -> (expr1 as Int) % (expr2 as Int)
            BinOp.AND -> (expr1 as Boolean) && (expr2 as Boolean)
            BinOp.OR -> (expr1 as Boolean) || (expr2 as Boolean)
            BinOp.EQ -> expr1 == expr2
            BinOp.NEQ -> expr1 != expr2
            // We only ever compare ints and chars so are safe to cast to int
            BinOp.LT -> (expr1 as Int) < (expr2 as Int)
            BinOp.GT -> (expr1 as Int) > (expr2 as Int)
            BinOp.GTE -> (expr1 as Int) >= (expr2 as Int)
            BinOp.LTE -> (expr1 as Int) <= (expr2 as Int)
            else -> throw Error("Should not get here")
        }
    }

    private fun visitLiterNode(expr: LiterNode): Any {
        return when(expr) {
            is IntLiterNode -> expr.value.toInt()
            is StrLiterNode -> expr.value
            is CharLiterNode -> expr.value[0] //TODO deal with escapes later
            is BoolLiterNode -> expr.value == "true"
            is Ident -> varStore.getValue(expr.name)
            else -> throw Error("Should not get here")
        }
    }
}