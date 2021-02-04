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

    override fun visitIntLiter(ctx: IntLiterContext): Node {
        println("At int liter")
        return IntLiterNode(ctx.text)
    }
}