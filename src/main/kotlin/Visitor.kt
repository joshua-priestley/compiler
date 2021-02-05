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

//    override fun visitIntLiter(ctx: IntLiterContext): Node {
//        println("At int liter")
//        return IntLiterNode(ctx.text)
//    }

    override fun visitStrLiter(ctx: StrLiterContext): Node {
        println("At str liter")
        return StrLiterNode(ctx.text)
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

    override fun visitSequence(ctx: SequenceContext): Node {
        println("sequence tiem")
        return SequenceNode(visit(ctx.stat(0)) as StatementNode,visit(ctx.stat(1)) as StatementNode)
    }

    override fun visitVarDeclaration(ctx: VarDeclarationContext): Node {
        println("at a variable declaration")
        return DeclarationNode(visit(ctx.type()) as TypeNode, Ident(ctx.ident().text), visit(ctx.assign_rhs()) as AssignRHSNode)
    }
}