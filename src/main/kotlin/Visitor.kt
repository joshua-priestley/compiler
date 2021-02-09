import antlr.WACCParser.*
import antlr.WACCParserBaseVisitor

class Visitor : WACCParserBaseVisitor<Node>() {

    private fun addStatToSymbolTable(symbolTable: SymbolTable, stat: StatementNode) {
        when (stat) {
            is DeclarationNode -> symbolTable.addNode(stat.ident.name, stat)
            is SequenceNode -> {
                addStatToSymbolTable(symbolTable, stat.stat1)
                addStatToSymbolTable(symbolTable, stat.stat2)
            }
        }
    }

    override fun visitProgram(ctx: ProgramContext): Node {
        println("At a program")
        /* AST */
        val functionNodes = mutableListOf<FunctionNode>()
        ctx.func().map { functionNodes.add(visit(it) as FunctionNode) }
        val stat = visit(ctx.stat()) as StatementNode

        /* Symbol Table */
        val globalSymbolTable = SymbolTable(null)
        for (fNode in functionNodes) {
            fNode.functionSymbolTable.setParentTable(globalSymbolTable)
            globalSymbolTable.addChildTable(fNode.functionSymbolTable)
            globalSymbolTable.addNode(fNode.ident.name, fNode)
        }
        addStatToSymbolTable(globalSymbolTable, stat)

        return ProgramNode(functionNodes, stat, globalSymbolTable)
    }

    override fun visitFunc(ctx: FuncContext): Node {
        println("At a function")
        /* AST */
        val type = visit(ctx.type()) as TypeNode

        val ident = visit(ctx.ident()) as Ident

        val parameterNodes = mutableListOf<Param>()
        if (ctx.param_list() != null) {
            for (i in 0..ctx.param_list().childCount step 2) {
                parameterNodes.add(visit(ctx.param_list().getChild(i)) as Param)
            }
        }

        val stat = visit(ctx.stat()) as StatementNode

        /* Symbol Table */
        val functionSymbolTable = SymbolTable(null)
        for (pNode in parameterNodes) {
            functionSymbolTable.addNode(pNode.ident.name, pNode)
        }
        addStatToSymbolTable(functionSymbolTable, stat)

        functionSymbolTable.addNode(ctx.toString(), type)

        return FunctionNode(type, ident, parameterNodes.toList(), stat, functionSymbolTable)
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

    override fun visitVarAssign(ctx: VarAssignContext): Node {
        println("At variable assignment")
        return AssignNode(visit(ctx.assign_lhs()) as AssignLHSNode,
                visit(ctx.assign_rhs()) as AssignRHSNode)
    }

    override fun visitRead(ctx: ReadContext): Node {
        println("At read node")
        return ReadNode(visit(ctx.assign_lhs()) as AssignLHSNode)
    }

    override fun visitExit(ctx: ExitContext): Node {
        println("At exit node")
        return ExitNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitFree(ctx: FreeContext): Node {
        println("at free node")
        return FreeNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitReturn(ctx: ReturnContext): Node {
        return ReturnNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitPrintln(ctx: PrintlnContext): Node {
        println("at println node")
        return PrintlnNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitSkip(ctx: SkipContext): Node {
        println("at a skip")
        return SkipNode()
    }

    override fun visitPrint(ctx: PrintContext): Node {
        println("at a print")
        return PrintNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitIf(ctx: IfContext): Node {
        println("at an if")
        return IfElseNode(visit(ctx.expr()) as ExprNode,
                visit(ctx.stat(0)) as StatementNode,
                visit(ctx.stat((1))) as StatementNode)
    }

    override fun visitWhile(ctx: WhileContext): Node {
        println("at a while")
        return WhileNode(visit(ctx.expr()) as ExprNode,
                visit(ctx.stat()) as StatementNode)
    }

    override fun visitBegin(ctx: BeginContext): Node {
        println("at a begin")
        return BeginEndNode(visit(ctx.stat()) as StatementNode)
    }

    override fun visitSequence(ctx: SequenceContext): Node {
        println("sequence item")
        return SequenceNode(visit(ctx.stat(0)) as StatementNode, visit(ctx.stat(1)) as StatementNode)
    }

    override fun visitVarDeclaration(ctx: VarDeclarationContext): Node {
        println("at a variable declaration")
        return DeclarationNode(visit(ctx.type()) as TypeNode, Ident(ctx.ident().text), visit(ctx.assign_rhs()) as AssignRHSNode)
    }

/*
================================================================
TYPES
 */

    override fun visitType(ctx: TypeContext): Node {
        when {
            ctx.base_type() != null -> {
                visit(ctx.base_type())
            }
            ctx.OPEN_SQUARE() != null -> {
                return ArrayNode(visit(ctx.type()) as TypeNode)
            }
            ctx.pair_type() != null -> {
                return visit(ctx.pair_type())
            }
        }
        return visitChildren(ctx)
    }

    override fun visitInt(ctx: IntContext): Node {
        println("At int")
        return Int()
    }

    override fun visitBool(ctx: BoolContext): Node {
        println("At bool")
        return Bool()
    }

    override fun visitChar(ctx: CharContext): Node {
        println("At char")
        return Chr()
    }

    override fun visitString(ctx: StringContext): Node {
        println("At string")
        return Str()
    }

    override fun visitArray_type(ctx: Array_typeContext): Node {
        println("At array type")
        return ArrayNode(visit(ctx.type()) as TypeNode)
    }

    override fun visitPair_type(ctx: Pair_typeContext): Node {
        println("At pair type")
        return PairTypeNode(visit(ctx.pair_elem_type(0)) as PairElemTypeNode,
                visit(ctx.pair_elem_type(1)) as PairElemTypeNode)
    }

    override fun visitPair_elem_type(ctx: Pair_elem_typeContext): Node {
        println("At pair elem type")
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

    override fun visitIntLiter(ctx: IntLiterContext): Node {
        println("At int liter")
        return IntLiterNode(ctx.text)
    }

    override fun visitStrLiter(ctx: StrLiterContext): Node {
        println("At str liter")
        return StrLiterNode(ctx.text)
    }

    override fun visitCharLiter(ctx: CharLiterContext): Node {
        println("At char liter")
        return CharLiterNode(ctx.text)
    }

    override fun visitBoolLiter(ctx: BoolLiterContext): Node {
        println("At bool liter")
        println(ctx.text)
        return BoolLiterNode(ctx.text)
    }

    override fun visitPairLiter(ctx: PairLiterContext): Node {
        println("At pair liter")
        return PairLiterNode()
    }

    //TODO can we change the parser to avoid having to do this
    override fun visitId(ctx: IdContext): Node {
        return visit(ctx.ident())
    }

    override fun visitIdent(ctx: IdentContext): Node {
        println("At ident")
        return Ident(ctx.text)
    }

    //TODO can we change the parser to avoid having to do this
    override fun visitArrayElem(ctx: ArrayElemContext): Node {
        return visit(ctx.array_elem())
    }

    override fun visitArray_elem(ctx: Array_elemContext): Node {
        println("At array elem")
        return ArrayElem(visit(ctx.ident()) as Ident,
                ctx.expr().map { visit(it) as ExprNode })
    }

    override fun visitUnaryOp(ctx: UnaryOpContext): Node {
        println("At unary op")
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

    override fun visitPre1(ctx: Pre1Context): Node {
        val op = when {
            ctx.MUL() != null -> BinOp.MUL
            ctx.DIV() != null -> BinOp.DIV
            ctx.MOD() != null -> BinOp.MOD
            else -> BinOp.NOT_SUPPORTED
        }
        return BinaryOpNode(op, visit(ctx.expr(0)) as ExprNode, visit(ctx.expr(1)) as ExprNode)
    }

    override fun visitPre2(ctx: Pre2Context): Node {
        val op = when {
            ctx.PLUS() != null -> BinOp.PLUS
            ctx.MINUS() != null -> BinOp.MINUS
            else -> BinOp.NOT_SUPPORTED
        }
        return BinaryOpNode(op, visit(ctx.expr(0)) as ExprNode, visit(ctx.expr(1)) as ExprNode)
    }

    override fun visitPre3(ctx: Pre3Context): Node {
        val op = when {
            ctx.GT() != null -> BinOp.GT
            ctx.GTE() != null -> BinOp.GTE
            ctx.LT() != null -> BinOp.LT
            ctx.LTE() != null -> BinOp.LTE
            else -> BinOp.NOT_SUPPORTED
        }
        return BinaryOpNode(op, visit(ctx.expr(0)) as ExprNode, visit(ctx.expr(1)) as ExprNode)
    }

    override fun visitPre4(ctx: Pre4Context): Node {
        val op = when {
            ctx.EQ() != null -> BinOp.EQ
            ctx.NEQ() != null -> BinOp.NEQ
            else -> BinOp.NOT_SUPPORTED
        }
        return BinaryOpNode(op, visit(ctx.expr(0)) as ExprNode, visit(ctx.expr(1)) as ExprNode)
    }

    override fun visitPre5(ctx: Pre5Context): Node {
        return BinaryOpNode(BinOp.AND, visit(ctx.expr(0)) as ExprNode, visit(ctx.expr(1)) as ExprNode)
    }

    override fun visitPre6(ctx: Pre6Context): Node {
        return BinaryOpNode(BinOp.OR, visit(ctx.expr(0)) as ExprNode, visit(ctx.expr(1)) as ExprNode)

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
