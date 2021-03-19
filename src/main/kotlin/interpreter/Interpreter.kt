package interpreter

import AST.*
import compiler.FailedParse
import compiler.FrontendUtils
import compiler.SuccessfulParse
import compiler.interpreter.VarStore

// Class for interpreting whole programs
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

// Interal representation for Wacc pairs
data class PairObject<T, S>(var fst: T?, var snd: S?) {
    fun isNull(): Boolean = fst == null && snd == null

    fun setNull() {
        fst = null
        snd = null
    }
}

class RuntimeError(): Exception()

// Runtime error messages
enum class Error(val msg: String) {
    ARRAY_NEGATIVE_INDEX("ArrayIndexOutOfBoundsError: negative index"),
    ARRAY_INDEX_TOO_LARGE("ArrayIndexOutOfBoundsError: index too large"),
    DIVIDE_BY_ZERO("DivideByZeroError: divide or modulo by zero"),
    INTEGER_OVERFLOW("OverflowError: the result is too small/large to store in a 4-byte signed-integer."),
    NULL_DEFERENCE("NullReferenceError: dereference a null reference.")
}


class InterpreterBackend (
    private var globalSymbolTable: SymbolTable,
    private var varStore: VarStore,
    private val funcList: MutableList<FunctionNode> = mutableListOf()
) {
    private var exitCode = 0
    private var funcReturn: Any? = null
    private var isRetruning = false

    // Prints all the variables in the current scope
    fun displayVarStore(varStore: VarStore) {
        println("All vars:")
        varStore.varStore.forEach{
            println("${it.key} = ${it.value}")
        }
        println("\n")
    }

    fun executeProgram(root: ProgramNode): Int {
        try {
            visitProgram(root)
        } catch (e: RuntimeError) {
            return 255
        }
        return exitCode
    }

    private fun visitProgram(program: ProgramNode) {
        program.funcs.forEach { funcList.add(it) }
        visitStat(program.stat)
    }

    // Stop traversal of the ast, return to executeProgram
    private fun runtimeErr(error: Error) {
        println(error.msg)
        throw RuntimeError()
    }


    fun visitStat(stat: StatementNode) {
        if (isRetruning) {
            return
        }
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
        varStore = varStore.newScope()
        visitStat(stat.stat)
        varStore = varStore.exitScope()
    }

    private fun visitWhile(stat: WhileNode) {
        varStore = varStore.newScope()
        while (visitExpr(stat.expr) as Boolean) {
            visitStat(stat.do_)
        }
        varStore = varStore.exitScope()
    }

    private fun visitIf(stat: IfElseNode) {
        if (visitExpr(stat.expr) as Boolean) {
            varStore = varStore.newScope()
            visitStat(stat.then)
            varStore = varStore.exitScope()
        } else {
            varStore = varStore.newScope()
            visitStat(stat.else_!!)
            varStore = varStore.exitScope()
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
        //println("in return: $stat")
        val returnVal = visitExpr(stat.expr)
        funcReturn = returnVal
        isRetruning = true
    }

    private fun visitFree(stat: FreeNode) {
        val toFree = visitExpr(stat.expr)
        val ident = stat.expr
        when (toFree) {
            is PairObject<*, *> -> {
                if (!toFree.isNull()) {
                    toFree.setNull()
                } else {
                    runtimeErr(Error.NULL_DEFERENCE)
                }
            }
            else -> runtimeErr(Error.NULL_DEFERENCE)
        }
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
            }
            is LHSArrayElemNode -> assignArrayElem(value, stat.lhs)
            is LHSPairElemNode -> assignPairElem(value, stat.lhs)
            else -> throw Error("Should not get here")
        }
    }

    private fun <T> assignPairElem(value: T, stat: LHSPairElemNode) {
        val pairElem = stat.pairElem
        val pair = visitExpr(pairElem.expr) as PairObject<*, *>
        if (pair.isNull()) {
            runtimeErr(Error.NULL_DEFERENCE)
        }
        when (pairElem) {
            // These unchecked casts are safe as any errors would be caught during semantic checks
            is FstExpr -> (pair as PairObject<T, *>).fst = value
            is SndExpr -> (pair as PairObject<*, T>).snd = value
            else -> throw Error("Should not get here")
        }
    }

    private fun <T> assignArrayElem(value: T, stat: LHSArrayElemNode) {
        val expr = stat.arrayElem
        var current: Any = varStore.getValue(expr.ident.name)
        // Keep indexing into subarrays for each index expression there is
        for (i in 0 .. expr.expr.size - 2) {
            val index = (visitExpr(expr.expr[i]) as Int)
            val length = (current as Array<*>).size
            if (index >= length) {
                runtimeErr(Error.ARRAY_INDEX_TOO_LARGE)
            } else if (index < 0) {
                runtimeErr(Error.ARRAY_NEGATIVE_INDEX)
            }
            current = current[index]!!
        }
        val index = (visitExpr(expr.expr[expr.expr.size - 1]) as Int)
        // This unchecked cast is safe as any errors would be caught during semantic analysis
        val length = (current as Array<T>).size
        if (index >= length) {
            runtimeErr(Error.ARRAY_INDEX_TOO_LARGE)
        } else if (index < 0) {
            runtimeErr(Error.ARRAY_NEGATIVE_INDEX)
        }
        current[index] = value
    }

    private fun visitDeclaration(stat: DeclarationNode) {
        val value = visitAssignRhs(stat.value)
        varStore.declareBaseValue(stat.ident.name, value)
    }

    private fun visitAssignRhs(stat: AssignRHSNode): Any {
        return when (stat) {
            is RHSExprNode -> visitExpr(stat.expr)
            is RHSArrayLitNode -> visitRHSArrayLit(stat)
            is RHSNewPairNode -> visitRHSNewPair(stat)
            is RHSPairElemNode -> visitRHSPairElem(stat)
            is RHSCallNode -> visitRHSCallNode(stat)
            else -> throw Error("Should not get here")
        }
    }

    private fun getFuncNode(id: Ident): FunctionNode? {
        for (func in funcList) {
            if (func.ident.name.contains(id.name)) {
                return func
            }
        }
        return null
    }

    private fun visitRHSCallNode(expr: RHSCallNode): Any {
        val func = getFuncNode(expr.ident)!!
        if (expr.argList != null) {
            val args = expr.argList.map { visitExpr(it) }
            val params = func.params.map { it.ident.name }
            // Set the parameters values as variables in the function scope
            varStore = varStore.enterFunction(args, params)
        } else {
            varStore = varStore.newScope()
        }
        visitStat(func.stat)
        // return to the calling scope
        varStore = varStore.exitScope()
        val returnVal = funcReturn!!
        funcReturn = null
        isRetruning = false
        return returnVal
    }

    private fun visitRHSPairElem(expr: RHSPairElemNode): Any {
        val pair = ((visitExpr(expr.pairElem.expr)) as PairObject<*, *>)
        if (pair.isNull()) {
            runtimeErr(Error.NULL_DEFERENCE)
        }
        return when (expr.pairElem) {
            is FstExpr -> pair.fst!!
            is SndExpr -> pair.snd!!
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
            else -> throw Error("should not get here")
        }
    }

    private fun visitArrayElem(expr: ArrayElem): Any {
        var current: Any = varStore.getValue(expr.ident.name)
        // Keep indexing into subarrays for each index expression there is
        for (indexExp in expr.expr) {
            val index = (visitExpr(indexExp) as Int)
            val length = (current as Array<*>).size
            if (index >= length) {
                runtimeErr(Error.ARRAY_INDEX_TOO_LARGE)
            } else if (index < 0) {
                runtimeErr(Error.ARRAY_NEGATIVE_INDEX)
            }
            current = current[index]!!
        }
        return current
    }

    private fun visitUnOp(expr: UnaryOpNode): Any {
        return when (expr.operator) {
            UnOp.NOT -> !(visitExpr(expr.expr) as Boolean)
            UnOp.MINUS -> {
                val result = (visitExpr(expr.expr) as Int).toLong().unaryMinus()
                val asInt = result.toInt()
                if (result != asInt.toLong()) {
                    runtimeErr(Error.INTEGER_OVERFLOW)
                }
                result
            }
            UnOp.CHR -> (visitExpr(expr.expr) as Int).toChar()
            UnOp.ORD -> (visitExpr(expr.expr) as Char).toInt()
            UnOp.LEN -> (visitExpr(expr.expr) as Array<*>).size
            else -> throw Error("Should not get here")
        }
    }

    private fun visitBinOp(expr: BinaryOpNode): Any {
        val expr1 = visitExpr(expr.expr1)
        val expr2 = visitExpr(expr.expr2)
        return when (expr.operator) {
            BinOp.PLUS -> {
                val result: Long = ((expr1 as Int).toLong()) + ((expr2 as Int).toLong())
                val asInt = result.toInt()
                if (result != asInt.toLong()) {
                    runtimeErr(Error.INTEGER_OVERFLOW)
                }
                asInt
            }
            BinOp.MINUS -> {
                val result: Long = ((expr1 as Int).toLong()) - ((expr2 as Int).toLong())
                val asInt = result.toInt()
                if (result != asInt.toLong()) {
                    runtimeErr(Error.INTEGER_OVERFLOW)
                }
                asInt
            }
            BinOp.MUL -> {
                val result: Long = ((expr1 as Int).toLong()) * ((expr2 as Int).toLong())
                val asInt = result.toInt()
                if (result != asInt.toLong()) {
                    runtimeErr(Error.INTEGER_OVERFLOW)
                }
                asInt
            }
            BinOp.MOD -> {
                if (expr2 as Int == 0) {
                    runtimeErr(Error.DIVIDE_BY_ZERO)
                }
                (expr1 as Int) % (expr2)
            }
            BinOp.DIV -> {
                if (expr2 as Int == 0) {
                    runtimeErr(Error.DIVIDE_BY_ZERO)
                }
                (expr1 as Int) % (expr2)
            }
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
            is StrLiterNode -> formatEscapeChars(expr.value)
            is CharLiterNode -> formatEscapeChars(expr.value)[0]
            is BoolLiterNode -> expr.value == "true"
            is Ident -> varStore.getValue(expr.name)
            else -> throw Error("Should not get here")
        }
    }

    // characters we want to escape will be parsed as an escaped \ followed by the character
    // if we find a \\ in a string, add the correct escape character
    private fun formatEscapeChars(string: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < string.length) {
            if (string[i] == '\\') {
                i += 1
                when (string[i]) {
                    '0' -> sb.append('\u0000')
                    'b' -> sb.append('\b')
                    't' -> sb.append('\t')
                    'n' -> sb.append('\n')
                    'f' -> sb.append('\u000C')
                    'r' -> sb.append('\r')
                    '"' -> sb.append('\"')
                    '\'' -> sb.append('\'')
                    '\\' -> sb.append('\\')
                }
            } else {
                sb.append(string[i])
            }
            i += 1
        }
        return sb.toString()
    }
}