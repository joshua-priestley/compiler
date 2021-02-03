import antlr.WACCParser;
import antlr.WACCLexer;

import org.antlr.v4.*;

class Visitor {
    fun visitProgram(ctx : WACCParser.ProgramContext) {
        println("At a program")
    }
}