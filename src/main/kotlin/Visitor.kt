import antlr.WACCParser.*
import antlr.WACCParserBaseVisitor

class Visitor(private val semanticListener: SemanticErrorHandler,
              private val syntaxListener: WACCErrorListener,
              private var globalSymbolTable: SymbolTable) : WACCParserBaseVisitor<Node>() {

    var semantic = false

    override fun visitProgram(ctx: ProgramContext): Node {
        addAllFunctions(ctx.func())

        val functionNodes = mutableListOf<FunctionNode>()
        ctx.func().map { functionNodes.add(visit(it) as FunctionNode) }
        val stat = visit(ctx.stat()) as StatementNode

        return ProgramNode(functionNodes, stat)
    }

    fun addAllFunctions(funcCTXs: MutableList<FuncContext>) {
        for (func in funcCTXs) {
            val ident = visit(func.ident()) as Ident
            val type = visit(func.type()) as TypeNode
            if (globalSymbolTable.containsNode(ident.toString())) {
                println("SEMANTIC ERROR DETECTED --- FUNCTION ALREADY EXISTS")
                semantic = true
            } else {
                globalSymbolTable.addNode(ident.toString(), Type(type))
            }
        }
    }

    override fun visitFunc(ctx: FuncContext): Node {
        val functionSymbolTable = SymbolTable(globalSymbolTable)

        val ident = visit(ctx.ident()) as Ident
        val type = visit(ctx.type()) as TypeNode

        val parameterNodes = mutableListOf<Param>()
        if (ctx.param_list() != null) {
            for (i in 0..ctx.param_list().childCount step 2) {
                val p = visit(ctx.param_list().getChild(i)) as Param
                parameterNodes.add(p)
                functionSymbolTable.addNode(p.ident.name, Type(p.type))
            }
        }

        functionSymbolTable.addNode(ident.toString(), Type(type))

        globalSymbolTable = functionSymbolTable

        val stat = visit(ctx.stat()) as StatementNode
        // TODO: CHANGE ERROR MESSAGE TO SMTH BETTER
        if (!stat.valid()) {
            syntaxListener.addSyntaxError(ctx, "return type of function invalid")
        }

        globalSymbolTable = globalSymbolTable.parentT!!

        if (!globalSymbolTable.containsNode(ident.name)) {
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

    private fun getLHSType(lhs: AssignLHSNode): Type? {
        return when (lhs) {
            is AssignLHSIdentNode -> {
                if (!globalSymbolTable.containsNode(lhs.ident.toString())) {
                    println("SEMANTIC ERROR DETECTED --- VARIABLE REFERENCED BEFORE ASSIGNMENT")
                    semantic = true
                    null
                } else {
                    globalSymbolTable.getNode(lhs.ident.toString())
                }
            }
            is LHSArrayElemNode -> {
                if (!globalSymbolTable.containsNode(lhs.arrayElem.ident.toString())) {
                    println("SEMANTIC ERROR DETECTED --- ARRAY REFERENCED BEFORE ASSIGNMENT")
                    semantic = true
                    null
                } else {
                    globalSymbolTable.getNode(lhs.arrayElem.ident.toString())
                }
            }
            else -> {
                null
            }
        }
    }

    private fun getRHSType(rhs: AssignRHSNode): Type? {
        return when (rhs) {
            is RHSCallNode -> {
                if (!globalSymbolTable.containsNode(rhs.ident.toString())) {
                    println("SEMANTIC ERROR DETECTED --- FUNCTION REFERENCED BEFORE ASSIGNMENT")
                    semantic = true
                    null
                } else {
                    globalSymbolTable.getNode(rhs.ident.toString())
                }
            }
            else -> {
                null
            }
        }
    }

    override fun visitVarAssign(ctx: VarAssignContext): Node {
        val lhs = visit(ctx.assign_lhs()) as AssignLHSNode
        val rhs = visit(ctx.assign_rhs()) as AssignRHSNode

        val lhs_type = getLHSType(lhs)
        val rhs_type = getRHSType(rhs)

        if (rhs_type != null) {
            if (lhs_type != rhs_type) {
                println("SEMANTIC ERROR DETECTED --- LHS TYPE DOES NOT EQUAL RHS TYPE ASSIGNMENT")
                semantic = true
            }
        }

        return AssignNode(lhs, rhs)
    }

    override fun visitRead(ctx: ReadContext): Node {
        return ReadNode(visit(ctx.assign_lhs()) as AssignLHSNode)
    }

    override fun visitExit(ctx: ExitContext): Node {
        return ExitNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitFree(ctx: FreeContext): Node {
        val freedExpr = visit(ctx.expr()) as ExprNode
        return FreeNode(freedExpr)
    }

    override fun visitReturn(ctx: ReturnContext): Node {
        return ReturnNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitPrintln(ctx: PrintlnContext): Node {
        return PrintlnNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitSkip(ctx: SkipContext): Node {
        return SkipNode()
    }

    override fun visitPrint(ctx: PrintContext): Node {
        return PrintNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitIf(ctx: IfContext): Node {
        return IfElseNode(visit(ctx.expr()) as ExprNode,
                visit(ctx.stat(0)) as StatementNode,
                visit(ctx.stat((1))) as StatementNode)
    }

    override fun visitWhile(ctx: WhileContext): Node {
        return WhileNode(visit(ctx.expr()) as ExprNode,
                visit(ctx.stat()) as StatementNode)
    }

    override fun visitBegin(ctx: BeginContext): Node {
        return BeginEndNode(visit(ctx.stat()) as StatementNode)
    }

    override fun visitSequence(ctx: SequenceContext): Node {
        return SequenceNode(visit(ctx.stat(0)) as StatementNode, visit(ctx.stat(1)) as StatementNode)
    }

    override fun visitVarDeclaration(ctx: VarDeclarationContext): Node {
        val type = visit(ctx.type()) as TypeNode
        val ident = Ident(ctx.ident().text)
        val rhs = visit(ctx.assign_rhs()) as AssignRHSNode
        if (globalSymbolTable.containsNode(ident.toString())) {
            println("SEMANTIC ERROR DETECTED --- VARIABLE ALREADY EXISTS")
            semantic = true
        } else {
            globalSymbolTable.addNode(ident.toString(), Type(type))
        }

        val lhs_type = Type(type)
        val rhs_type = getRHSType(rhs)

        if (rhs_type != null) {
            if (lhs_type != rhs_type) {
                println("SEMANTIC ERROR DETECTED --- LHS TYPE DOES NOT EQUAL RHS TYPE DECLARATION")
                semantic = true
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
        val ret = when {
            ctx.INT() != null -> Str()
            ctx.BOOL() != null -> Bool()
            ctx.CHAR() != null -> Chr()
            ctx.STRING() != null -> Str()
            else -> TODO()
        }

        return ret;
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

    override fun visitLiter(ctx: LiterContext): Node {
        val ret = when {
            ctx.BOOL_LITER() != null -> BoolLiterNode(ctx.text)
            ctx.CHAR_LITER() != null -> CharLiterNode(ctx.text)
            ctx.STR_LITER() != null -> StrLiterNode(ctx.text)
            else -> {
                val value = ctx.text.toLong()
                if (value !in Integer.MIN_VALUE..Integer.MAX_VALUE) {
                    syntaxListener.addSyntaxError(ctx, "int value must be between -2147483648 and 2147483647")
                }
                IntLiterNode(ctx.text)
            }
        }
        return ret;
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

        return BinaryOpNode(op, visit(ctx.expr(0)) as ExprNode, visit(ctx.expr(1)) as ExprNode)
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