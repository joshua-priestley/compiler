import antlr.WACCParser.*
import antlr.WACCParserBaseVisitor
import Type
import kotlin.math.exp

class Visitor(private val semanticListener: SemanticErrorHandler,
              private val syntaxListener: WACCErrorListener,
              private var globalSymbolTable: SymbolTable) : WACCParserBaseVisitor<Node>() {

    private val functionParameters: LinkedHashMap<String, List<Type>> = linkedMapOf()
    var semantic = false
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
                println("SEMANTIC ERROR DETECTED --- FUNCTION ALREADY EXISTS")
                semantic = true
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

    private fun getPairElemType(pairElem: PairElemNode): Type? {
        val expr: ExprNode
        return if (pairElem::class == FstExpr::class) {
            expr = (pairElem as FstExpr).expr
            if (expr::class != Ident::class) {
                println("SEMANTIC ERROR DETECTED --- FST EXPRESSION MUST BE AN IDENT")
                semantic = true
                null
            } else if (!globalSymbolTable.containsNodeGlobal((expr as Ident).toString())) {
                println("SEMANTIC ERROR DETECTED --- PAIR DOES NOT EXIST")
                semantic = true
                null
            } else {
                globalSymbolTable.getNodeGlobal(expr.toString())!!.getPairFst()
            }
        } else {
            expr = (pairElem as SndExpr).expr
            if (expr::class != Ident::class) {
                println("SEMANTIC ERROR DETECTED --- SND EXPRESSION MUST BE AN IDENT")
                semantic = true
                null
            } else if (!globalSymbolTable.containsNodeGlobal((expr as Ident).toString())) {
                println("SEMANTIC ERROR DETECTED --- PAIR DOES NOT EXIST")
                semantic = true
                null
            } else {
                globalSymbolTable.getNodeGlobal(expr.toString())!!.getPairSnd()
            }
        }
    }

    private fun checkParameters(rhs: RHSCallNode): Boolean {
        val parameterTypes = functionParameters[rhs.ident.toString()]
        if (rhs.argList == null && parameterTypes!!.isEmpty()) {
            return true
        } else if (rhs.argList!!.size != parameterTypes!!.size) {
            println("SEMANTIC ERROR DETECTED --- NUMBER OF ARGUMENTS DOES NOT MATCH")
            semantic = true
            return false
        }
        val argTypes = mutableListOf<Type>()
        for (arg in rhs.argList) {
            val type = getExprType(arg)
            if (type == null) {
                return false
            } else {
                argTypes.add(type)
            }
        }
        for (i in argTypes.indices) {
            if (argTypes[i] != parameterTypes[i]) {
                println("SEMANTIC ERROR DETECTED --- MISMATCHED PARAMETER TYPES")
                semantic = true
                return false
            }
        }
        return true
    }

    private fun getLHSType(lhs: AssignLHSNode): Type? {
        return when (lhs) {
            is AssignLHSIdentNode -> {
                if (!globalSymbolTable.containsNodeGlobal(lhs.ident.toString())) {
                    println("SEMANTIC ERROR DETECTED --- VARIABLE REFERENCED BEFORE ASSIGNMENT")
                    semantic = true
                } else if (globalSymbolTable.getNodeGlobal(lhs.ident.toString())!!.isFunction()) {
                    println("SEMANTIC ERROR DETECTED --- CANNOT ASSIGN A FUNCTION")
                    semantic = true
                }
                globalSymbolTable.getNodeGlobal(lhs.ident.toString())
            }
            is LHSArrayElemNode -> {
                if (!globalSymbolTable.containsNodeGlobal(lhs.arrayElem.ident.toString())) {
                    println("SEMANTIC ERROR DETECTED --- ARRAY REFERENCED BEFORE ASSIGNMENT")
                    semantic = true
                } else if (getExprType(lhs.arrayElem.expr[0]) != Type(INT)) {
                    println("SEMANTIC ERROR DETECTED --- ARRAY INDEX IS NOT AN INTEGER")
                    semantic = true
                } else if (globalSymbolTable.getNodeGlobal(lhs.arrayElem.ident.toString())!!.getType() == STRING) {
                    println("SEMANTIC ERROR DETECTED --- STRINGS CANNOT BE INDEXED")
                    semantic = true
                }
                globalSymbolTable.getNodeGlobal(lhs.arrayElem.ident.toString())!!.getBaseType()
            }
            else -> {
                getPairElemType((lhs as LHSPairElemNode).pairElem)
            }
        }
    }

    private fun checkElemsSameType(exprs: List<ExprNode>) {
        if (exprs.isEmpty()) {
            return
        }
        val firstType = getExprType(exprs[0])
        for (i in exprs.indices) {
            // If the type cannot be found, something is wrong with the element
            if (getExprType(exprs[i]) == null) {
                println("SEMANTIC ERROR --- Invalid array element")
                semantic = true
                break
                // If the elements type does not match the first then there is an error
            } else if (getExprType(exprs[i]) != firstType) {
                println("SEMANTIC ERROR --- Array elements have differing types")
                semantic = true
                break
            }
        }
    }

    private fun getRHSType(rhs: AssignRHSNode): Type? {
        return when (rhs) {
            is RHSExprNode -> {
                getExprType(rhs.expr)
            }
            is RHSCallNode -> {
                if (!globalSymbolTable.containsNodeGlobal(rhs.ident.toString())) {
                    println("SEMANTIC ERROR DETECTED --- FUNCTION REFERENCED BEFORE ASSIGNMENT")
                    semantic = true
                    null
                } else if (!checkParameters(rhs)) {
                    null
                } else {
                    globalSymbolTable.getNodeGlobal(rhs.ident.toString())
                }
            }
            is RHSPairElemNode -> {
                getPairElemType(rhs.pairElem)
            }
            is RHSArrayLitNode -> {
                checkElemsSameType(rhs.exprs)
                if (rhs.exprs.isEmpty()) {
                    Type(EMPTY_ARR)
                } else {
                    val type = getExprType(rhs.exprs[0])
                    if (type == null) {
                        null
                    } else {
                        Type(type)
                    }
                }
            }
            else -> {
                // RHSNewPairElemNode
                val expr1 = getExprType((rhs as RHSNewPairNode).expr1)
                val expr2 = getExprType(rhs.expr2)
                if (expr1 == null) {
                    println("SEMANTIC ERROR DETECTED --- NEWPAIR EXPRESSION 1 IS FALSE")
                    semantic = true
                    null
                } else if (expr2 == null) {
                    println("SEMANTIC ERROR DETECTED --- NEWPAIR EXPRESSION 2 IS FALSE")
                    semantic = true
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

        val lhsType = getLHSType(lhs)

        cond = true
        val rhsType = getRHSType(rhs)
        cond = false
        if (lhsType != null) {
            if (lhsType != rhsType && !(lhsType.getArray() && rhsType == Type(EMPTY_ARR))) {
                println("SEMANTIC ERROR DETECTED --- LHS TYPE DOES NOT EQUAL RHS TYPE ASSIGNMENT Line: " + ctx.getStart().line)
                semantic = true
            }
        }

        return AssignNode(lhs, rhs)
    }

    override fun visitRead(ctx: ReadContext): Node {
        val lhsNode = visit(ctx.assign_lhs()) as AssignLHSNode
        val type = when (lhsNode) {
            is LHSPairElemNode -> getPairElemType(lhsNode.pairElem)
            is LHSArrayElemNode -> getExprType(lhsNode.arrayElem)
            else -> {
                if (lhsNode !is AssignLHSIdentNode) {
                    println("SEMANTIC ERROR DETECTED --- MUST READ INTO VARIABLE Line: " + ctx.getStart().line)
                    semantic = true
                }
                if (!globalSymbolTable.containsNodeGlobal((lhsNode as AssignLHSIdentNode).ident.toString())) {
                    println("SEMANTIC ERROR DETECTED --- VARIABLE DOES NOT EXIST Line: " + ctx.getStart().line)
                    semantic = true
                }
                globalSymbolTable.getNodeGlobal(lhsNode.ident.toString())
            }
        }

        if (!(type == Type(INT) || type == Type(CHAR))) {
            println("SEMANTIC ERROR DETECTED --- READ MUST GO INTO AN INT OR CHAR Line: " + ctx.getStart().line)
            semantic = true
        }
        return ReadNode(lhsNode)
    }

    override fun visitExit(ctx: ExitContext): Node {
        val expr = visit(ctx.expr()) as ExprNode
        if (getExprType(expr) != Type(INT)) {
            println("SEMANTIC ERROR DETECTED --- EXIT CODE MUST BE INT Line: " + ctx.getStart().line)
            semantic = true
        }
        return ExitNode(expr)
    }

    override fun visitFree(ctx: FreeContext): Node {
        val freedExpr = visit(ctx.expr()) as ExprNode
        val freeType = getExprType(freedExpr)
        if (freeType == null || !freeType.getPair()) {
            println("SEMANTIC ERROR DETECTED --- CAN ONLY FREE A PAIR Line: " + ctx.getStart().line)
            semantic = true
        }
        return FreeNode(freedExpr)
    }

    override fun visitReturn(ctx: ReturnContext): Node {
        if (globalSymbolTable.parentT == null) {
            println("SEMANTIC ERROR DETECTED --- RETURNING FROM GLOBAL Line: " + ctx.getStart().line)
            semantic = true
        }
        val expr = visit(ctx.expr()) as ExprNode
        val type = getExprType(expr)
        if (type != globalSymbolTable.getNodeLocal("\$RET")) {
            println("SEMANTIC ERROR DETECTED --- RETURN TYPES NOT EQUAL Line: " + ctx.getStart().line)
            semantic = true
        }
        return ReturnNode(expr)
    }

    private fun checkPrint(expr: ExprNode) {
        if (expr is Ident && !globalSymbolTable.containsNodeLocal(expr.toString())) {
            println("SEMANTIC ERROR DETECTED --- CANNOT PRINT A NON EXISTENT VARIABLE")
            semantic = true
        }
    }

    override fun visitPrintln(ctx: PrintlnContext): Node {
        val expr = visit(ctx.expr()) as ExprNode
        checkPrint(expr)
        return PrintlnNode(expr)
    }

    override fun visitSkip(ctx: SkipContext): Node {
        return SkipNode()
    }

    override fun visitPrint(ctx: PrintContext): Node {
        val expr = visit(ctx.expr()) as ExprNode
        checkPrint(expr)
        return PrintNode(expr)
    }

    override fun visitIf(ctx: IfContext): Node {
        cond = true
        val condExpr = visit(ctx.expr()) as ExprNode
        if (getExprType(condExpr) != Type(BOOL)) {
            println("SEMANTIC ERROR DETECTED --- IF STATEMENT CONDITION NOT BOOLEAN Line: " + ctx.getStart().line)
            semantic = true
        }
        cond = false
        return IfElseNode(condExpr,
                visit(ctx.stat(0)) as StatementNode,
                visit(ctx.stat((1))) as StatementNode)
    }

    override fun visitWhile(ctx: WhileContext): Node {
        cond = true
        val condExpr = visit(ctx.expr()) as ExprNode
        if (getExprType(condExpr) != Type(BOOL)) {
            println("SEMANTIC ERROR DETECTED --- WHILE CONDITION NOT BOOLEAN Line: " + ctx.getStart().line)
            semantic = true
        }
        cond = false
        return WhileNode(condExpr,
                visit(ctx.stat()) as StatementNode)
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
        if (globalSymbolTable.containsNodeLocal(ident.toString()) && !globalSymbolTable.getNodeLocal(ident.toString())!!.isFunction()) {
            println("SEMANTIC ERROR DETECTED --- VARIABLE ALREADY EXISTS  Line: " + ctx.getStart().line)
            semantic = true
        } else {
            globalSymbolTable.addNode(ident.toString(), type.type)
        }

        val lhsType = type.type
        cond = true
        val rhsType = getRHSType(rhs)
        cond = false

        if (lhsType != rhsType && !(lhsType.getArray() && rhsType == Type(EMPTY_ARR)) && !(lhsType.getPair() && rhsType == Type(PAIR_LITER))) {
            println("SEMANTIC ERROR DETECTED --- LHS TYPE DOES NOT EQUAL RHS TYPE DECLARATION Line: " + ctx.getStart().line)
            semantic = true

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
        return PairTypeNode(visit(ctx.pair_elem_type(0)) as PairElemTypeNode,
                visit(ctx.pair_elem_type(1)) as PairElemTypeNode)
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
            operator in 12..14 -> mutableListOf(Type(BOOL))
            operator in 10..11 -> mutableListOf(Type(ANY))
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

    private fun getExprType(expr: ExprNode): Type? {
        return when (expr) {
            is IntLiterNode -> Type(INT)
            is StrLiterNode -> Type(STRING)
            is BoolLiterNode -> Type(BOOL)
            is CharLiterNode -> Type(CHAR)
            is Ident -> {
                if (!globalSymbolTable.containsNodeGlobal(expr.toString())) {
                    println("SEMANTIC ERROR DETECTED --- VARIABLE DOES NOT EXIST")
                    semantic = true
                    null
                } else {
                    globalSymbolTable.getNodeGlobal(expr.toString())
                }
            }

            is ArrayElem -> {
                if (getExprType(expr.expr[0]) != Type(INT)) {
                    println("SEMANTIC ERROR DETECTED --- ARRAY INDEX IS NOT AN INTEGER")
                    semantic = true
                } else if (!globalSymbolTable.containsNodeGlobal(expr.ident.toString())) {
                    println("SEMANTIC ERROR DETECTED --- ARRAY DOES NOT EXIST")
                    semantic = true

                } else if (globalSymbolTable.getNodeGlobal(expr.ident.toString())!!.getType() == STRING) {
                    println("SEMANTIC ERROR DETECTED --- STRINGS CANNOT BE INDEXED")
                    semantic = true
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
                    if (binaryOpsRequires(expr.operator.value).contains(getExprType(expr.expr1))) {
                        getExprType(expr.expr1)
                    } else {
                        println("SEMANTIC ERROR DETECTED -- INCORRECT TYPE FOR BINARY OPERATOR")
                        semantic = true
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
                    syntaxListener.addSyntaxError(ctx, "int value must be between -2147483648 and 2147483647 Line: " + ctx.getStart().line)
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

        val exprType = getExprType(expr1)
        if (exprType != getExprType(expr2)) {
            println("SEMANTIC ERROR DETECTED --- BOOLEAN EXPRESSION TYPES DO NOT MATCH WITH EACHOTHER Line: " + ctx.getStart().line)
            semantic = true
        }
        if (!binaryOpsRequires(op.value).contains(exprType) && !binaryOpsRequires(op.value).contains(Type(ANY))) {
            println("SEMANTIC ERROR DETECTED --- WRONG TYPE FOR THIS BIN OP Line: " + ctx.getStart().line)
            semantic = true
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
        return RHSNewPairNode(visit(ctx.expr(0)) as ExprNode,
                visit(ctx.expr(1)) as ExprNode)
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