import antlr.WACCParser.*;
import antlr.WACCLexer;
import antlr.WACCParserBaseVisitor

import org.antlr.v4.*;

class Visitor : WACCParserBaseVisitor<Node>() {
    override fun visitProgram(ctx : ProgramContext): Node {
        println("At a program")
        val stat = visit(ctx.stat()) as StatementNode
        return ProgramNode(ArrayList(), stat)
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
        return SequenceNode(visit(ctx.stat(0)) as StatementNode,visit(ctx.stat(1)) as StatementNode)
    }

    override fun visitVarDeclaration(ctx: VarDeclarationContext): Node {
        println("at a variable declaration")
        return DeclarationNode(visit(ctx.type()) as TypeNode, Ident(ctx.ident().text), visit(ctx.assign_rhs()) as AssignRHSNode)
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
                         ctx.expr().map {visit(it) as ExprNode})
    }

    override fun visitUnaryOp(ctx: UnaryOpContext): Node {
        println("At unary op")
        //TODO handle semantic errors here? or handle later using NOT_SUPPORTED flag
        //TODO is there a ore elegant way to do this?
        val op =  when {
            ctx.unaryOper().NOT() != null -> UnOp.NOT
            ctx.unaryOper().MINUS() != null -> UnOp.MINUS
            ctx.unaryOper().LEN() != null -> UnOp.LEN
            ctx.unaryOper().ORD() != null -> UnOp.ORD
            ctx.unaryOper().CHR() != null -> UnOp.CHR
            else -> UnOp.NOT_SUPPORTED
        }
        return UnaryOpNode(op, visit(ctx.expr()) as ExprNode)
    }

    override fun visitBinaryOp(ctx: BinaryOpContext): Node {
        println("At binary op")

        val op = when {
            ctx.binaryOper().pre1().MUL() != null -> BinOp.MUL
            ctx.binaryOper().pre1().DIV() != null -> BinOp.DIV
            ctx.binaryOper().pre1().MOD() != null -> BinOp.MOD
            ctx.binaryOper().pre2().PLUS() != null -> BinOp.PLUS
            ctx.binaryOper().pre2().MINUS() != null -> BinOp.MINUS
            ctx.binaryOper().pre3().GT() != null -> BinOp.GT
            ctx.binaryOper().pre3().GTE() != null -> BinOp.GTE
            ctx.binaryOper().pre3().LT() != null -> BinOp.LT
            ctx.binaryOper().pre3().LTE() != null -> BinOp.LTE
            ctx.binaryOper().pre4().EQ() != null -> BinOp.EQ
            ctx.binaryOper().pre4().NEQ() != null -> BinOp.NEQ
            ctx.binaryOper().pre5().AND() != null -> BinOp.AND
            ctx.binaryOper().pre6().OR() != null -> BinOp.OR
            else -> BinOp.NOT_SUPPORTED
        }

        return BinaryOpNode(op, visit(ctx.expr(0)) as ExprNode, visit(ctx.expr(1)) as ExprNode)
    }
}
