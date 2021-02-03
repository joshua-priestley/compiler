import antlr.WACCParser;
import antlr.WACCLexer;
import antlr.WACCParserBaseVisitor

import org.antlr.v4.*;

class Visitor : WACCParserBaseVisitor<Int>() {
//    override fun visitProgram(ctx : WACCParser.ProgramContext): Int {
//        println("At a program")
//        visitChildren(ctx);
//        return 1;
//    }

    override fun visitFunc(ctx: WACCParser.FuncContext): Int {
        println("At a function")
        visitChildren(ctx);
        return 1;
    }
}