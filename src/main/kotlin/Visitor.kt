import antlr.WACCParser
import antlr.WACCParser.*
import antlr.WACCParserBaseVisitor
import antlr.WACCParserVisitor
import kotlin.Pair

class Visitor : WACCParserBaseVisitor<Node>() {
    override fun visitProgram(ctx: ProgramContext): Node {
        println("At a program")
        val functionNodes = mutableListOf<FunctionNode>()
        ctx.func().map { functionNodes.add(visit(it) as FunctionNode) }
        val stat = visit(ctx.stat()) as StatementNode
        return ProgramNode(functionNodes, stat)
    }

    override fun visitFunc(ctx: FuncContext): Node {
        println("At a function")
        val type = visit(ctx.type()) as TypeNode

        val ident = visit(ctx.ident()) as Ident

        val parameterNodes = mutableListOf<Param>()
        if (ctx.param_list() != null) {
            for (i in 0..ctx.param_list().childCount step 2) {
                parameterNodes.add(visit(ctx.param_list().getChild(i)) as Param)
            }
        }

        val stat = visit(ctx.stat()) as StatementNode

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
            ctx.PAIR() != null -> kotlin.Pair()
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
            ctx.INT_LITER() != null -> IntLiterNode(ctx.text)
            ctx.STR_LITER() != null -> StrLiterNode(ctx.text)
            else -> TODO()
        }

        return ret;
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