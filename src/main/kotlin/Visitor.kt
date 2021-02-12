import antlr.WACCParser.*
import antlr.WACCParserBaseVisitor
import Type
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import javax.swing.plaf.nimbus.State
import kotlin.math.exp

class Visitor(
    private val semanticListener: SemanticErrorHandler,
    private val syntaxListener: WACCErrorListener,
    private var globalSymbolTable: SymbolTable
) : WACCParserBaseVisitor<Node>() {

    private val functionParameters: LinkedHashMap<String, List<Type>> = linkedMapOf()
    var cond = false

    override fun visitProgram(ctx: ProgramContext): Node {
        addAllFunctions(ctx.func())

//        println("Added Functions:")
//        globalSymbolTable.printTableEntries()
        val functionNodes = mutableListOf<FunctionNode>()
        ctx.func().map { functionNodes.add(visit(it) as FunctionNode) }
        val stat = visit(ctx.stat()) as StatementNode

//        println("Added eveything else")
//        globalSymbolTable.printTableEntries()
        return ProgramNode(functionNodes, stat)
    }

    private fun addAllFunctions(funcCTXs: MutableList<FuncContext>) {
        for (func in funcCTXs) {
            val ident = visit(func.ident()) as Ident
            val type = visit(func.type()) as TypeNode
            if (globalSymbolTable.containsNodeLocal(ident.toString())) {
                // println("SEMANTIC ERROR DETECTED --- FUNCTION ALREADY EXISTS")
                semanticListener.redefinedVariable(ident.name + "()", func)
            } else {
                globalSymbolTable.addNode(ident.toString(), type.type.setFunction(true))
            }

            val parameterTypes = mutableListOf<Type>()
            if (func.param_list() != null) {
                for (i in 0..func.param_list().childCount step 2) {
                    val p = visit(func.param_list().getChild(i)) as Param
                    parameterTypes.add(p.type.type)
                }
            }
            functionParameters[ident.toString()] = parameterTypes
        }
    }

    override fun visitFunc(ctx: FuncContext): Node {
        val functionSymbolTable = SymbolTable(globalSymbolTable)

        val ident = visit(ctx.ident()) as Ident
        val type = visit(ctx.type()) as TypeNode

        val parameterNodes = mutableListOf<Param>()
        val parameterTypes = mutableListOf<Type>()
        if (ctx.param_list() != null) {
            for (i in 0..ctx.param_list().childCount step 2) {
                val p = visit(ctx.param_list().getChild(i)) as Param
                parameterNodes.add(p)
                functionSymbolTable.addNode(p.ident.toString(), p.type.type)
                parameterTypes.add(p.type.type)
            }
        }

        functionSymbolTable.addNode("\$RET", type.type.setFunction(true))
        functionSymbolTable.addNode(ident.toString(), type.type.setFunction(true))

        functionParameters[ident.toString()] = parameterTypes

        globalSymbolTable = functionSymbolTable
        val stat = visit(ctx.stat()) as StatementNode
        // TODO: CHANGE ERROR MESSAGE TO SMTH BETTER
        if (!stat.valid()) {
            syntaxListener.addSyntaxError(ctx, "return type of function invalid")
        }

        globalSymbolTable = globalSymbolTable.parentT!!

        if (!globalSymbolTable.containsNodeLocal(ident.name)) {
            globalSymbolTable.addChildTable(functionSymbolTable)
        }
        return FunctionNode(type, ident, parameterNodes.toList(), stat)
    }

/*
================================================================
PARAMETERS
 */

    override fun visitParam(ctx: ParamContext): Node {
        val type = visit(ctx.type()) as TypeNode
        val ident = visit(ctx.ident()) as Ident
        return Param(type, ident)
    }

    override fun visitParam_list(ctx: Param_listContext?): Node {
        return visitChildren(ctx)
    }
/*
================================================================
STATEMENTS
 */

    private fun getPairElemType(pairElem: PairElemNode, ctx: ParserRuleContext): Type? {
        val expr: ExprNode
        return if (pairElem::class == FstExpr::class) {
            expr = (pairElem as FstExpr).expr
            if (expr::class != Ident::class) {
                semanticListener.fstSndMustBePair(ctx)
                null
            } else if (!globalSymbolTable.containsNodeGlobal((expr as Ident).toString())) {
                semanticListener.undefinedType(expr.name, ctx)
                null
            } else {
                globalSymbolTable.getNodeGlobal(expr.toString())!!.getPairFst()
            }
        } else {
            expr = (pairElem as SndExpr).expr
            if (expr::class != Ident::class) {
                semanticListener.fstSndMustBePair(ctx)
                null
            } else if (!globalSymbolTable.containsNodeGlobal((expr as Ident).toString())) {
                semanticListener.undefinedType(expr.name, ctx)
                null
            } else {
                globalSymbolTable.getNodeGlobal(expr.toString())!!.getPairSnd()
            }
        }
    }

    private fun checkParameters(rhs: RHSCallNode, ctx: ParserRuleContext): Boolean {
        val parameterTypes = functionParameters[rhs.ident.toString()]
        if (rhs.argList == null && parameterTypes!!.isEmpty()) {
            return true
        } else if (rhs.argList!!.size != parameterTypes!!.size) {
            semanticListener.mismatchedArgs(parameterTypes.size.toString(), rhs.argList.size.toString(), ctx)
            return false
        }
        val argTypes = mutableListOf<Type>()
        for (arg in rhs.argList) {
            val type = getExprType(arg, null)
            if (type == null) {
                return false
            } else {
                argTypes.add(type)
            }
        }
        for (i in argTypes.indices) {
            if (argTypes[i].getType() != parameterTypes[i].getType()) {
                semanticListener.mismatchedParamTypes(
                    Type(argTypes[i].getType()).toString(),
                    Type(parameterTypes[i].getType()).toString(),
                    ctx
                )
                return false
            }
        }
        return true
    }

    private fun getLHSType(lhs: AssignLHSNode, ctx: Assign_lhsContext): Type? {
        return when (lhs) {
            is AssignLHSIdentNode -> {
                if (!globalSymbolTable.containsNodeGlobal(lhs.ident.toString())) {
                    semanticListener.undefinedType(lhs.ident.name, ctx)
                } else if (globalSymbolTable.getNodeGlobal(lhs.ident.toString())!!.isFunction()) {
                    semanticListener.assigningFunction(lhs.ident.name, ctx)
                }
                globalSymbolTable.getNodeGlobal(lhs.ident.toString())
            }
            is LHSArrayElemNode -> {
                if (!globalSymbolTable.containsNodeGlobal(lhs.arrayElem.ident.toString())) {
                    semanticListener.undefinedType(lhs.arrayElem.ident.name, ctx)
                } else if (getExprType(lhs.arrayElem.expr[0], null) != Type(INT)) {
                    semanticListener.arrayIndex("0", "INT", getExprType(lhs.arrayElem.expr[0], null).toString(), ctx)
                } else if (globalSymbolTable.getNodeGlobal(lhs.arrayElem.ident.toString())!!.getType() == STRING) {
                    semanticListener.indexStrings(ctx)
                }
                globalSymbolTable.getNodeGlobal(lhs.arrayElem.ident.toString())!!.getBaseType()
            }
            else -> {
                getPairElemType((lhs as LHSPairElemNode).pairElem, ctx)
            }
        }
    }

    private fun checkElemsSameType(exprs: List<ExprNode>, ctx: ParserRuleContext) {
        if (exprs.isEmpty()) {
            return
        }
        val firstType = getExprType(exprs[0], null)
        for (i in exprs.indices) {
            // If the type cannot be found, something is wrong with the element
            if (getExprType(exprs[i], null) == null) {
                semanticListener.arrayIndex(i.toString(), getExprType(exprs[i], null).toString(), "NULL", ctx)
                break
                // If the elements type does not match the first then there is an error
            } else if (getExprType(exprs[i], null) != firstType) {
                semanticListener.arrayDifferingTypes(ctx)
                break
            }
        }
    }

    private fun getRHSType(rhs: AssignRHSNode, ctx: ParserRuleContext): Type? {
        return when (rhs) {
            is RHSExprNode -> {
                getExprType(rhs.expr, null)
            }
            is RHSCallNode -> {
                if (!globalSymbolTable.containsNodeGlobal(rhs.ident.toString())) {
                    semanticListener.funRefBeforeAss(rhs.ident.name, ctx)
                    null
                } else if (!checkParameters(rhs, ctx)) {
                    null
                } else {
                    globalSymbolTable.getNodeGlobal(rhs.ident.toString())
                }
            }
            is RHSPairElemNode -> {
                getPairElemType(rhs.pairElem, ctx)
            }
            is RHSArrayLitNode -> {
                checkElemsSameType(rhs.exprs, ctx)
                if (rhs.exprs.isEmpty()) {
                    Type(EMPTY_ARR)
                } else {
                    val type = getExprType(rhs.exprs[0], null)
                    if (type == null) {
                        null
                    } else {
                        Type(type)
                    }
                }
            }
            else -> {
                // RHSNewPairElemNode
                val pair = rhs as RHSNewPairNode
                var expr1 = getExprType(pair.expr1, null)
                var expr2 = getExprType(rhs.expr2, null)

                if (expr1 != null) {
                    if (expr1.getType() == PAIR_LITER) {
                        expr1 = Type(PAIR_LITER)
                    }
                }
                if (expr2 != null) {
                    if (expr2.getType() == PAIR_LITER) {
                        expr2 = Type(PAIR_LITER)
                    }
                }

                if (expr1 == null) {
                    semanticListener.newPairFalse("1", ctx)
                    null
                } else if (expr2 == null) {
                    semanticListener.newPairFalse("2", ctx)
                    null
                } else {
                    Type(expr1, expr2)
                }
            }
        }
    }

    override fun visitVarAssign(ctx: VarAssignContext): Node {
        val lhs = visit(ctx.assign_lhs()) as AssignLHSNode
        val rhs = visit(ctx.assign_rhs()) as AssignRHSNode

        val lhsType = getLHSType(lhs, ctx.assign_lhs())

        cond = true
        val rhsType = getRHSType(rhs, ctx)
        cond = false
        if (lhsType != null) {
            if (lhsType.getType() != rhsType!!.getType() && !(lhsType.getArray() && rhsType.getType() == Type(EMPTY_ARR).getType())
                && !(lhsType.getPair() && rhsType.getType() == Type(PAIR_LITER).getType())
            ) {
                semanticListener.incompatibleTypeAss(lhsType.toString(), rhsType.toString(), ctx)
            }
        }

        return AssignNode(lhs, rhs)
    }

    override fun visitRead(ctx: ReadContext): Node {
        val lhsNode = visit(ctx.assign_lhs()) as AssignLHSNode
        val type = when (lhsNode) {
            is LHSPairElemNode -> getPairElemType(lhsNode.pairElem, ctx)
            is LHSArrayElemNode -> getExprType(lhsNode.arrayElem, null)
            else -> {
                if (lhsNode !is AssignLHSIdentNode) {
                    semanticListener.readNotVariable(ctx)
                }
                if (!globalSymbolTable.containsNodeGlobal((lhsNode as AssignLHSIdentNode).ident.toString())) {
                    semanticListener.undefinedType(lhsNode.ident.name, ctx)
                }
                globalSymbolTable.getNodeGlobal(lhsNode.ident.toString())
            }
        }

        if (!(type == Type(INT) || type == Type(CHAR))) {
            semanticListener.readTypeError(type.toString(), ctx)

        }
        return ReadNode(lhsNode)
    }

    override fun visitExit(ctx: ExitContext): Node {
        val expr = visit(ctx.expr()) as ExprNode
        if (getExprType(expr, ctx.expr()) != Type(INT)) {
            semanticListener.incompatibleExitCode(ctx.expr().text, getExprType(expr, ctx.expr()).toString(), ctx)
        }
        return ExitNode(expr)
    }

    override fun visitFree(ctx: FreeContext): Node {
        val freedExpr = visit(ctx.expr()) as ExprNode
        val freeType = getExprType(freedExpr, ctx.expr())
        if (freeType == null || !freeType.getPair()) {
            semanticListener.incompatibleTypeFree(freeType.toString(), ctx)
        }
        return FreeNode(freedExpr)
    }

    override fun visitReturn(ctx: ReturnContext): Node {
        if (globalSymbolTable.parentT == null) {
            semanticListener.returnFromGlobal(ctx)
        }

        val expr = visit(ctx.expr()) as ExprNode
        cond = true
        val type = getExprType(expr, ctx.expr())
        cond = false
        val expected = globalSymbolTable.getNodeGlobal("\$RET")

        if (type != expected) {
            semanticListener.incompatibleTypeReturn(expected.toString(), type.toString(), ctx)
        }
        return ReturnNode(expr)
    }

    private fun checkPrint(expr: ExprNode, ctx: ParserRuleContext) {
        if (expr is Ident && !globalSymbolTable.containsNodeGlobal(expr.toString())) {
            semanticListener.undefinedType(expr.name, ctx)
        }
    }

    override fun visitPrintln(ctx: PrintlnContext): Node {
        val expr = visit(ctx.expr()) as ExprNode
        checkPrint(expr, ctx)
        return PrintlnNode(expr)
    }

    override fun visitSkip(ctx: SkipContext): Node {
        return SkipNode()
    }

    override fun visitPrint(ctx: PrintContext): Node {
        val expr = visit(ctx.expr()) as ExprNode
        checkPrint(expr, ctx)
        return PrintNode(expr)
    }

    override fun visitIf(ctx: IfContext): Node {
        cond = true
        val condExpr = visit(ctx.expr()) as ExprNode
        if (getExprType(condExpr, ctx.expr()) != Type(BOOL)) {
            semanticListener.conditionalBoolean(ctx)
        }
        cond = false

        val ifSymbolTable = SymbolTable(globalSymbolTable)
        globalSymbolTable = ifSymbolTable
        val stat1 = visit(ctx.stat(0)) as StatementNode
        globalSymbolTable = globalSymbolTable.parentT!!

        val elseSymbolTable = SymbolTable(globalSymbolTable)
        globalSymbolTable = elseSymbolTable
        val stat2 = visit(ctx.stat(1)) as StatementNode
        globalSymbolTable = globalSymbolTable.parentT!!

        return IfElseNode(condExpr, stat1, stat2)
    }

    override fun visitWhile(ctx: WhileContext): Node {
        cond = true
        val condExpr = visit(ctx.expr()) as ExprNode
        if (getExprType(condExpr, ctx.expr()) != Type(BOOL)) {
            semanticListener.conditionalBoolean(ctx)
        }
        cond = false

        val loopSymbolTable = SymbolTable(globalSymbolTable)
        globalSymbolTable = loopSymbolTable
        val stat = visit(ctx.stat()) as StatementNode
        globalSymbolTable = globalSymbolTable.parentT!!

        return WhileNode(condExpr, stat)
    }

    override fun visitBegin(ctx: BeginContext): Node {
        val scopeSymbolTable = SymbolTable(globalSymbolTable)
        globalSymbolTable = scopeSymbolTable
        val stat = visit(ctx.stat()) as StatementNode
        globalSymbolTable = globalSymbolTable.parentT!!
        return BeginEndNode(stat)
    }

    override fun visitSequence(ctx: SequenceContext): Node {
        return SequenceNode(visit(ctx.stat(0)) as StatementNode, visit(ctx.stat(1)) as StatementNode)
    }

    override fun visitVarDeclaration(ctx: VarDeclarationContext): Node {

        val type = visit(ctx.type()) as TypeNode
        val ident = Ident(ctx.ident().text)
        val rhs = visit(ctx.assign_rhs()) as AssignRHSNode
        if (!cond && globalSymbolTable.containsNodeLocal(ident.toString()) && globalSymbolTable.containsNodeLocal(ident.toString())
            && !globalSymbolTable.getNodeLocal(ident.toString())!!.isFunction()
        ) {
            semanticListener.redefinedVariable(ident.name, ctx)
        } else {
            globalSymbolTable.addNode(ident.toString(), type.type)
        }

        val lhsType = type.type
        cond = true
        val rhsType = getRHSType(rhs, ctx)
        cond = false

        if (rhsType != null) {
            if (lhsType.getType() != rhsType.getType()
                && !(lhsType.getArray() && rhsType.getType() == Type(EMPTY_ARR).getType())
                && !(lhsType.getPair() && rhsType.getType() == Type(PAIR_LITER).getType())
            ) {
                semanticListener.incompatibleTypeDecl(ident.name, lhsType.toString(), rhsType.toString(), ctx)
            }
        }

        return DeclarationNode(type, ident, rhs)
    }

/*
================================================================
TYPES
 */

    override fun visitType(ctx: TypeContext): Node {
        when {
            ctx.base_type() != null -> visit(ctx.base_type())
            ctx.OPEN_SQUARE() != null -> return ArrayNode(visit(ctx.type()) as TypeNode)
            ctx.pair_type() != null -> return visit(ctx.pair_type())
        }

        return visitChildren(ctx)
    }

    override fun visitBaseT(ctx: BaseTContext): Node {
        return when {
            ctx.INT() != null -> Int()
            ctx.BOOL() != null -> Bool()
            ctx.CHAR() != null -> Chr()
            ctx.STRING() != null -> Str()
            else -> TODO()
        };
    }

    override fun visitArray_type(ctx: Array_typeContext): Node {
        return ArrayNode(visit(ctx.type()) as TypeNode)
    }

    override fun visitPair_type(ctx: Pair_typeContext): Node {
        return PairTypeNode(
            visit(ctx.pair_elem_type(0)) as PairElemTypeNode,
            visit(ctx.pair_elem_type(1)) as PairElemTypeNode
        )
    }

    override fun visitPair_elem_type(ctx: Pair_elem_typeContext): Node {
        val type: Any = when {
            ctx.PAIR() != null -> Pair()
            ctx.array_type() != null -> visit(ctx.array_type())
            ctx.base_type() != null -> visit(ctx.base_type())
            else -> println("Shouldn't get here...")
        }
        return PairElemTypeNode(type as TypeNode)
    }

    /*
    ================================================================
    EXPRESSIONS
     */
    //Get the type of a binary operator
    private fun binaryOpsProduces(operator: kotlin.Int): Type {
        return when {
            //Tokens 1-5 are int operators
            operator <= 5 -> Type(INT)
            //Tokens 6-13 are bool operators
            operator in 6..13 -> Type(BOOL)
            else -> Type(INVALID)
        }
    }

    private fun binaryOpsRequires(operator: kotlin.Int): List<Type> {
        return when {
            operator < 6 -> mutableListOf(Type(INT))
            operator in 6..9 -> mutableListOf(Type(INT), Type(CHAR))
            operator in 10..11 -> mutableListOf(Type(ANY))
            operator in 12..14 -> mutableListOf(Type(BOOL))
            operator in 12..14 -> mutableListOf(Type(BOOL))
            else -> mutableListOf(Type(INVALID))
        }
    }

    //Get the type of a unary operator
    private fun unaryOpsProduces(operator: kotlin.Int): Type {
        return when (operator) {
            NOT -> Type(BOOL)
            LEN, ORD, MINUS -> Type(INT)
            CHR -> Type(CHAR)
            else -> Type(INVALID)
        }
    }

    private fun unaryOpsRequires(operator: kotlin.Int): Type {
        return when (operator) {
            NOT -> Type(BOOL)
            ORD -> Type(CHAR)
            MINUS, CHR -> Type(INT)
            LEN -> Type(ARRAY)
            else -> Type(INVALID)
        }
    }

    private fun getExprType(expr: ExprNode, ctx: ExprContext?): Type? {
        return when (expr) {
            is IntLiterNode -> Type(INT)
            is StrLiterNode -> Type(STRING)
            is BoolLiterNode -> Type(BOOL)
            is CharLiterNode -> Type(CHAR)
            is Ident -> {
                if (!globalSymbolTable.containsNodeGlobal(expr.toString())) {
                    semanticListener.undefinedType(expr.name, ctx!!)
                    null
                } else {
                    globalSymbolTable.getNodeGlobal(expr.toString())
                }
            }

            is ArrayElem -> {
                if (getExprType(expr.expr[0], ctx) != Type(INT)) {
                    semanticListener.arrayIndex("0", getExprType(expr.expr[0], ctx).toString(), "INT", ctx!!)
                } else if (!globalSymbolTable.containsNodeGlobal(expr.ident.toString())) {
                    semanticListener.undefinedType(expr.ident.name, ctx!!)
                } else if (globalSymbolTable.getNodeGlobal(expr.ident.toString())!!.getType() == STRING) {
                    semanticListener.indexStrings(ctx!!)
                }
                globalSymbolTable.getNodeGlobal(expr.ident.toString())!!.getBaseType()
            }
            is UnaryOpNode -> {
                if (cond) {
                    unaryOpsProduces(expr.operator.value)
                } else {
                    unaryOpsRequires(expr.operator.value)
                }
            }
            is BinaryOpNode -> {
                if (cond) {
                    binaryOpsProduces(expr.operator.value)
                } else {
                    val requires = binaryOpsRequires(expr.operator.value)
                    if (requires.contains(getExprType(expr.expr1, ctx)) || requires.contains(Type(ANY))) {
                        //TODO check is this actually right? an operator expr should always have the produced type right???
                        binaryOpsProduces(expr.operator.value)
                    } else {
                        semanticListener.binaryOpType(ctx!!)
                        null
                    }
                }
            }
            else -> {
                // PairLiterNode
                Type(PAIR_LITER)
            }
        }
    }

    override fun visitLiter(ctx: LiterContext): Node {
        return when {
            ctx.BOOL_LITER() != null -> BoolLiterNode(ctx.text)
            ctx.CHAR_LITER() != null -> CharLiterNode(ctx.text)
            ctx.STR_LITER() != null -> StrLiterNode(ctx.text)
            else -> {
                val value = ctx.text.toLong()
                if (value !in Integer.MIN_VALUE..Integer.MAX_VALUE) {
                    syntaxListener.addSyntaxError(
                        ctx,
                        "int value must be between -2147483648 and 2147483647 Line: " + ctx.getStart().line
                    )
                }
                IntLiterNode(ctx.text)
            }
        }
    }

    override fun visitPairLiter(ctx: PairLiterContext): Node {
        return PairLiterNode()
    }

    //TODO can we change the parser to avoid having to do this
    override fun visitId(ctx: IdContext): Node {
        return visit(ctx.ident())
    }

    override fun visitIdent(ctx: IdentContext): Node {
        return Ident(ctx.text)
    }

    //TODO can we change the parser to avoid having to do this
    override fun visitArrayElem(ctx: ArrayElemContext): Node {
        return visit(ctx.array_elem())
    }

    override fun visitArray_elem(ctx: Array_elemContext): Node {
        return ArrayElem(visit(ctx.ident()) as Ident,
            ctx.expr().map { visit(it) as ExprNode })
    }

    override fun visitUnaryOp(ctx: UnaryOpContext): Node {
        //TODO handle semantic errors here? or handle later using NOT_SUPPORTED flag
        //TODO is there a ore elegant way to do this?
        val op = when {
            ctx.NOT() != null -> UnOp.NOT
            ctx.MINUS() != null -> UnOp.MINUS
            ctx.LEN() != null -> UnOp.LEN
            ctx.ORD() != null -> UnOp.ORD
            ctx.CHR() != null -> UnOp.CHR
            else -> UnOp.NOT_SUPPORTED
        }
        return UnaryOpNode(op, visit(ctx.expr()) as ExprNode)
    }

    override fun visitBinaryOp(ctx: BinaryOpContext): Node {
        val op = when {
            ctx.MUL() != null -> BinOp.MUL
            ctx.DIV() != null -> BinOp.DIV
            ctx.MOD() != null -> BinOp.MOD
            ctx.PLUS() != null -> BinOp.PLUS
            ctx.MINUS() != null -> BinOp.MINUS
            ctx.GT() != null -> BinOp.GT
            ctx.GTE() != null -> BinOp.GTE
            ctx.LT() != null -> BinOp.LT
            ctx.LTE() != null -> BinOp.LTE
            ctx.EQ() != null -> BinOp.EQ
            ctx.NEQ() != null -> BinOp.NEQ
            ctx.AND() != null -> BinOp.AND
            ctx.OR() != null -> BinOp.OR
            else -> BinOp.NOT_SUPPORTED
        }

        val expr1 = visit(ctx.expr(0)) as ExprNode
        val expr2 = visit(ctx.expr(1)) as ExprNode


        val exprType = getExprType(expr1, ctx)

        if ((exprType != null && getExprType(expr2, ctx.expr(1)) != null && exprType.getType() != getExprType(
                expr2,
                ctx.expr(1)
            )!!.getType())
            || (!binaryOpsRequires(op.value).contains(exprType) && !binaryOpsRequires(op.value).contains(Type(ANY)))
        ) {
            semanticListener.binaryOpType(ctx)
        }

        return BinaryOpNode(op, expr1, expr2)
    }

    override fun visitParentheses(ctx: ParenthesesContext): Node {
        return visit(ctx.expr()) as ExprNode
    }

/*
================================================================
EXPRESSIONS
 */


    override fun visitAssignLhsId(ctx: AssignLhsIdContext): Node {
        return AssignLHSIdentNode(visit(ctx.ident()) as Ident)
    }

    override fun visitAssignLhsArray(ctx: AssignLhsArrayContext): Node {
        return LHSArrayElemNode(visit(ctx.array_elem()) as ArrayElem)
    }

    override fun visitAssignLhsPair(ctx: AssignLhsPairContext): Node {
        return LHSPairElemNode(visit(ctx.pair_elem()) as PairElemNode)
    }


    override fun visitAssignRhsExpr(ctx: AssignRhsExprContext): Node {
        return RHSExprNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitAssignRhsArray(ctx: AssignRhsArrayContext): Node {
        return RHSArrayLitNode(ctx.array_liter().expr().map { visit(it) as ExprNode })
    }

    override fun visitAssignRhsNewpair(ctx: AssignRhsNewpairContext): Node {
        return RHSNewPairNode(
            visit(ctx.expr(0)) as ExprNode,
            visit(ctx.expr(1)) as ExprNode
        )
    }

    override fun visitAssignRhsPairElem(ctx: AssignRhsPairElemContext): Node {
        return RHSPairElemNode(visit(ctx.pair_elem()) as PairElemNode)
    }

    override fun visitAssignRhsCall(ctx: AssignRhsCallContext): Node {
        return RHSCallNode(visit(ctx.ident()) as Ident,
            when {
                ctx.arg_list() != null -> ctx.arg_list().expr().map { visit(it) as ExprNode }
                else -> null
            })
    }

    override fun visitPairFst(ctx: PairFstContext): Node {
        return FstExpr(visit(ctx.expr()) as ExprNode)
    }

    override fun visitPairSnd(ctx: PairSndContext): Node {
        return SndExpr(visit(ctx.expr()) as ExprNode)
    }
}